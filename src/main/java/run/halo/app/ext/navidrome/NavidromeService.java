package run.halo.app.ext.navidrome;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;
import org.xml.sax.InputSource;

@Service
public class NavidromeService {

    private static final Logger log = LoggerFactory.getLogger(NavidromeService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String SUBSONIC_API_VERSION = "1.16.1";
    private static final String SUBSONIC_CLIENT = "halo";
    private static final Duration PLAYLIST_CACHE_TTL = Duration.ofMinutes(30);
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private final ReactiveSettingFetcher settingFetcher;
    private final WebClient webClient;
    private final ConcurrentHashMap<String, CachedPlaylist> playlistCache = new ConcurrentHashMap<>();

    public NavidromeService(ReactiveSettingFetcher settingFetcher) {
        this.settingFetcher = settingFetcher;
        this.webClient = WebClient.builder().build();
    }

    public Mono<List<APlayerAudio>> getPlaylistForAPlayer(String requestedPlaylistId) {
        return fetchSetting()
            .flatMap(setting -> {
                if (!setting.enablePlayer() || !setting.isConfigured()) {
                    return Mono.just(List.of());
                }
                var playlistId = resolvePlaylistId(setting, requestedPlaylistId);
                return getOrLoadCachedPlaylist(setting, playlistId)
                    .map(NavidromePlaylistPayload::audios);
            });
    }

    public Mono<List<NavidromePlaylistSummary>> getConfiguredPlaylists() {
        return fetchSetting()
            .flatMap(setting -> {
                if (!setting.enablePlayer() || !setting.isConfigured()) {
                    return Mono.just(List.of());
                }

                return Flux.fromIterable(setting.configuredPlaylistIds())
                    .concatMap(playlistId -> getOrLoadCachedPlaylist(setting, playlistId)
                        .map(payload -> new NavidromePlaylistSummary(
                            payload.id(),
                            payload.name(),
                            payload.audios().size()
                        ))
                        .onErrorResume(error -> {
                            log.warn(
                                "Failed to load Navidrome playlist summary [{}] from [{}].",
                                playlistId,
                                setting.serverUrl(),
                                error
                            );
                            return Mono.just(new NavidromePlaylistSummary(playlistId, playlistId, 0));
                        }))
                    .collectList();
            });
    }

    private Mono<NavidromeSetting> fetchSetting() {
        return settingFetcher.fetch(NavidromeSetting.GROUP, NavidromeSetting.class)
            .switchIfEmpty(Mono.just(NavidromeSetting.EMPTY));
    }

    private String resolvePlaylistId(NavidromeSetting setting, String requestedPlaylistId) {
        var normalizedRequested = NavidromeSetting.normalizePlaylistReference(requestedPlaylistId);
        if (StringUtils.hasText(normalizedRequested)
            && setting.configuredPlaylistIds().contains(normalizedRequested)) {
            return normalizedRequested;
        }
        return setting.primaryPlaylistId();
    }

    private Mono<NavidromePlaylistPayload> getOrLoadCachedPlaylist(
        NavidromeSetting setting,
        String playlistId
    ) {
        var cacheKey = buildCacheKey(setting, playlistId);
        var cachedPlaylist = playlistCache.compute(cacheKey, (key, existing) -> {
            if (isCacheValid(existing)) {
                return existing;
            }
            return newCachedPlaylist(key, setting, playlistId);
        });
        return cachedPlaylist.playlistMono();
    }

    private CachedPlaylist newCachedPlaylist(
        String cacheKey,
        NavidromeSetting setting,
        String playlistId
    ) {
        var expiresAt = Instant.now().plus(PLAYLIST_CACHE_TTL);
        final CachedPlaylist[] holder = new CachedPlaylist[1];

        var playlistMono = loadPlaylist(setting, playlistId)
            .doOnError(error -> {
                log.warn(
                    "Failed to load Navidrome playlist [{}] from [{}].",
                    playlistId,
                    setting.serverUrl(),
                    error
                );
                var current = holder[0];
                if (current != null) {
                    playlistCache.remove(cacheKey, current);
                }
            })
            .cache();

        holder[0] = new CachedPlaylist(playlistMono, expiresAt);
        return holder[0];
    }

    private Mono<NavidromePlaylistPayload> loadPlaylist(
        NavidromeSetting setting,
        String playlistId
    ) {
        var auth = SubsonicAuth.create(setting.password());
        var playlistUri = buildPlaylistUri(setting, auth, playlistId);

        return webClient.get()
            .uri(playlistUri)
            .exchangeToMono(response -> response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> parsePlaylistResponse(
                    response.statusCode(),
                    body,
                    setting,
                    auth,
                    playlistId
                )))
            .onErrorMap(error -> {
                if (error instanceof ResponseStatusException) {
                    return error;
                }
                return new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to load Navidrome playlist",
                    error
                );
            });
    }

    private Mono<NavidromePlaylistPayload> parsePlaylistResponse(
        HttpStatusCode statusCode,
        String body,
        NavidromeSetting setting,
        SubsonicAuth auth,
        String playlistId
    ) {
        if (!statusCode.is2xxSuccessful()) {
            return Mono.error(new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                buildUpstreamErrorMessage("Navidrome getPlaylist", statusCode, body)
            ));
        }

        return Mono.fromCallable(() -> parsePlaylistBody(body, setting, auth, playlistId));
    }

    private NavidromePlaylistPayload parsePlaylistBody(
        String body,
        NavidromeSetting setting,
        SubsonicAuth auth,
        String playlistId
    ) throws Exception {
        if (!StringUtils.hasText(body)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Navidrome getPlaylist returned an empty body."
            );
        }

        var trimmed = body.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            return parseJsonPlaylistBody(trimmed, setting, auth, playlistId);
        }
        if (trimmed.startsWith("<")) {
            return parseXmlPlaylistBody(trimmed, setting, auth, playlistId);
        }

        throw new ResponseStatusException(
            HttpStatus.BAD_GATEWAY,
            "Navidrome getPlaylist returned an unsupported payload: " + abbreviate(trimmed)
        );
    }

    private NavidromePlaylistPayload parseJsonPlaylistBody(
        String body,
        NavidromeSetting setting,
        SubsonicAuth auth,
        String playlistId
    ) throws Exception {
        var root = JSON_MAPPER.readTree(body);
        var playlist = extractPlaylist(root);
        var audios = toAudioList(playlist.path("entry"), setting, auth);
        var playlistName = firstNonBlank(playlist.path("name").asText(""), playlistId);
        return new NavidromePlaylistPayload(playlistId, playlistName, audios);
    }

    private NavidromePlaylistPayload parseXmlPlaylistBody(
        String body,
        NavidromeSetting setting,
        SubsonicAuth auth,
        String playlistId
    ) throws Exception {
        var factory = newSecureDocumentBuilderFactory();
        var builder = factory.newDocumentBuilder();
        var document = builder.parse(new InputSource(new StringReader(body)));

        var responseNodes = document.getElementsByTagName("subsonic-response");
        if (responseNodes.getLength() == 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Navidrome returned XML without subsonic-response."
            );
        }

        var response = (org.w3c.dom.Element) responseNodes.item(0);
        var status = response.getAttribute("status");
        if (!"ok".equalsIgnoreCase(status)) {
            var errorNodes = response.getElementsByTagName("error");
            var message = "";
            if (errorNodes.getLength() > 0) {
                message = ((org.w3c.dom.Element) errorNodes.item(0)).getAttribute("message");
            }
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                StringUtils.hasText(message) ? message : "Navidrome returned an unexpected XML error."
            );
        }

        var playlistNodes = response.getElementsByTagName("playlist");
        if (playlistNodes.getLength() == 0) {
            return new NavidromePlaylistPayload(playlistId, playlistId, List.of());
        }

        var playlist = (org.w3c.dom.Element) playlistNodes.item(0);
        var playlistName = firstNonBlank(trimToEmpty(playlist.getAttribute("name")), playlistId);
        var entryNodes = playlist.getElementsByTagName("entry");
        if (entryNodes.getLength() == 0) {
            return new NavidromePlaylistPayload(playlistId, playlistName, List.of());
        }

        var audios = new ArrayList<APlayerAudio>(entryNodes.getLength());
        for (var i = 0; i < entryNodes.getLength(); i++) {
            var entry = (org.w3c.dom.Element) entryNodes.item(i);
            var audio = toAudio(entry, setting, auth);
            if (audio != null) {
                audios.add(audio);
            }
        }
        return new NavidromePlaylistPayload(playlistId, playlistName, List.copyOf(audios));
    }

    private DocumentBuilderFactory newSecureDocumentBuilderFactory() throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory;
    }

    private boolean isCacheValid(CachedPlaylist cachedPlaylist) {
        return cachedPlaylist != null && Instant.now().isBefore(cachedPlaylist.expiresAt());
    }

    private String buildCacheKey(NavidromeSetting setting, String playlistId) {
        var rawKey = String.join("|",
            setting.serverUrl(),
            setting.username(),
            setting.password(),
            playlistId
        );
        return Base64.getEncoder().encodeToString(rawKey.getBytes(StandardCharsets.UTF_8));
    }

    private String buildUpstreamErrorMessage(String operation, HttpStatusCode statusCode, String body) {
        var details = extractUpstreamErrorDetails(body);
        if (StringUtils.hasText(details)) {
            return operation + " failed with upstream status " + statusCode.value() + ": " + details;
        }
        return operation + " failed with upstream status " + statusCode.value() + ".";
    }

    private String extractUpstreamErrorDetails(String body) {
        if (!StringUtils.hasText(body)) {
            return "";
        }

        try {
            var node = JSON_MAPPER.readTree(body);
            var message = node.path("subsonic-response").path("error").path("message").asText("");
            if (StringUtils.hasText(message)) {
                return message;
            }
        } catch (Exception ignored) {
            // Fall back to string extraction below.
        }

        var titlePattern = java.util.regex.Pattern.compile(
            "<title>(.*?)</title>",
            java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL
        );
        var titleMatcher = titlePattern.matcher(body);
        if (titleMatcher.find()) {
            return titleMatcher.group(1).replaceAll("\\s+", " ").trim();
        }

        var xmlMessagePattern = java.util.regex.Pattern.compile("message\\s*=\\s*\"([^\"]+)\"");
        var matcher = xmlMessagePattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }

        var compact = body.replaceAll("\\s+", " ").trim();
        return abbreviate(compact);
    }

    private JsonNode extractPlaylist(JsonNode root) {
        var response = root.path("subsonic-response");
        var status = response.path("status").asText("");
        if (!"ok".equalsIgnoreCase(status)) {
            var error = response.path("error");
            var message = error.path("message").asText("Navidrome returned an unexpected error.");
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, message);
        }
        return response.path("playlist");
    }

    private List<APlayerAudio> toAudioList(
        JsonNode entries,
        NavidromeSetting setting,
        SubsonicAuth auth
    ) {
        if (entries.isMissingNode() || entries.isNull()) {
            return List.of();
        }

        var audios = new ArrayList<APlayerAudio>();
        if (entries.isArray()) {
            entries.forEach(entry -> {
                var audio = toAudio(entry, setting, auth);
                if (audio != null) {
                    audios.add(audio);
                }
            });
        } else {
            var audio = toAudio(entries, setting, auth);
            if (audio != null) {
                audios.add(audio);
            }
        }
        return List.copyOf(audios);
    }

    private APlayerAudio toAudio(JsonNode entry, NavidromeSetting setting, SubsonicAuth auth) {
        var songId = firstNonBlank(entry, "id");
        if (!StringUtils.hasText(songId)) {
            return null;
        }

        var title = firstNonBlank(entry, "title", "name");
        var artist = firstNonBlank(entry, "artist", "albumArtist");
        var coverId = firstNonBlank(entry, "coverArt");
        var duration = integerValue(entry, "duration");

        return new APlayerAudio(
            StringUtils.hasText(title) ? title : songId,
            StringUtils.hasText(artist) ? artist : "Unknown Artist",
            buildStreamUrl(setting, auth, songId),
            buildCoverUrl(setting, auth, coverId),
            duration
        );
    }

    private APlayerAudio toAudio(
        org.w3c.dom.Element entry,
        NavidromeSetting setting,
        SubsonicAuth auth
    ) {
        var songId = trimToEmpty(entry.getAttribute("id"));
        if (!StringUtils.hasText(songId)) {
            return null;
        }

        var title = firstNonBlank(
            trimToEmpty(entry.getAttribute("title")),
            trimToEmpty(entry.getAttribute("name"))
        );
        var artist = firstNonBlank(
            trimToEmpty(entry.getAttribute("artist")),
            trimToEmpty(entry.getAttribute("albumArtist"))
        );
        var coverId = trimToEmpty(entry.getAttribute("coverArt"));
        var duration = parseInteger(trimToEmpty(entry.getAttribute("duration")));

        return new APlayerAudio(
            StringUtils.hasText(title) ? title : songId,
            StringUtils.hasText(artist) ? artist : "Unknown Artist",
            buildStreamUrl(setting, auth, songId),
            buildCoverUrl(setting, auth, coverId),
            duration
        );
    }

    private URI buildPlaylistUri(NavidromeSetting setting, SubsonicAuth auth, String playlistId) {
        return withCommonAuth(
            absoluteSubsonicUriBuilder(setting, "/rest/getPlaylist.view"),
            setting,
            auth
        )
            .queryParam("id", playlistId)
            .queryParam("f", "json")
            .build()
            .encode()
            .toUri();
    }

    private String buildStreamUrl(NavidromeSetting setting, SubsonicAuth auth, String songId) {
        return withCommonAuth(
            absoluteSubsonicUriBuilder(setting, "/rest/stream.view"),
            setting,
            auth
        )
            .queryParam("id", songId)
            .build()
            .encode()
            .toUriString();
    }

    private String buildCoverUrl(NavidromeSetting setting, SubsonicAuth auth, String coverId) {
        if (!StringUtils.hasText(coverId)) {
            return null;
        }

        return withCommonAuth(
            absoluteSubsonicUriBuilder(setting, "/rest/getCoverArt.view"),
            setting,
            auth
        )
            .queryParam("id", coverId)
            .build()
            .encode()
            .toUriString();
    }

    private UriComponentsBuilder absoluteSubsonicUriBuilder(NavidromeSetting setting, String path) {
        return UriComponentsBuilder.fromUriString(setting.serverUrl()).path(path);
    }

    private UriComponentsBuilder withCommonAuth(
        UriComponentsBuilder builder,
        NavidromeSetting setting,
        SubsonicAuth auth
    ) {
        return builder
            .queryParam("u", setting.username())
            .queryParam("t", auth.token())
            .queryParam("s", auth.salt())
            .queryParam("v", SUBSONIC_API_VERSION)
            .queryParam("c", SUBSONIC_CLIENT);
    }

    private String firstNonBlank(JsonNode node, String... fieldNames) {
        for (var fieldName : fieldNames) {
            var value = node.path(fieldName).asText("");
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private String firstNonBlank(String... values) {
        for (var value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private String abbreviate(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.length() > 180 ? value.substring(0, 180) + "..." : value;
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private Integer integerValue(JsonNode node, String fieldName) {
        if (node == null) {
            return null;
        }
        return parseInteger(node.path(fieldName).asText(""));
    }

    private Integer parseInteger(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private record SubsonicAuth(String salt, String token) {

        private static SubsonicAuth create(String password) {
            var salt = randomSalt();
            return new SubsonicAuth(salt, md5Hex(password + salt));
        }

        private static String randomSalt() {
            var bytes = new byte[8];
            RANDOM.nextBytes(bytes);
            return HexFormat.of().formatHex(bytes);
        }

        private static String md5Hex(String value) {
            try {
                var digest = MessageDigest.getInstance("MD5");
                var bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
                return HexFormat.of().formatHex(bytes);
            } catch (NoSuchAlgorithmException exception) {
                throw new IllegalStateException("MD5 algorithm is not available.", exception);
            }
        }
    }

    private record CachedPlaylist(Mono<NavidromePlaylistPayload> playlistMono, Instant expiresAt) {
    }

    private record NavidromePlaylistPayload(
        String id,
        String name,
        List<APlayerAudio> audios
    ) {
    }
}
