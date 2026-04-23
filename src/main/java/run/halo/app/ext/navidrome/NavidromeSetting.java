package run.halo.app.ext.navidrome;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

public record NavidromeSetting(
    String serverUrl,
    String username,
    String password,
    String playlistId,
    List<String> extraPlaylistIds,
    boolean enablePlayer,
    String pjaxMode
) {

    public static final String GROUP = "navidrome";
    public static final String PJAX_MODE_OFF = "off";
    public static final String PJAX_MODE_PLUGIN = "plugin";
    public static final String PJAX_MODE_THEME_COMPATIBLE = "theme-compatible";
    public static final NavidromeSetting EMPTY =
        new NavidromeSetting("", "", "", "", List.of(), false, PJAX_MODE_OFF);
    private static final Pattern PLAYLIST_URL_PATTERN =
        Pattern.compile("(?:^|/)playlist/([^/?#]+)(?:/show)?(?:$|[/?#])");

    public NavidromeSetting {
        serverUrl = normalizeServerUrl(serverUrl);
        username = trimToEmpty(username);
        password = password == null ? "" : password;
        playlistId = normalizePlaylistReference(playlistId);
        extraPlaylistIds = normalizePlaylistReferences(extraPlaylistIds);
        pjaxMode = normalizePjaxMode(pjaxMode);
    }

    public boolean isConfigured() {
        return StringUtils.hasText(serverUrl)
            && StringUtils.hasText(username)
            && StringUtils.hasText(password)
            && !configuredPlaylistIds().isEmpty();
    }

    public List<String> configuredPlaylistIds() {
        var ordered = new LinkedHashSet<String>();
        if (StringUtils.hasText(playlistId)) {
            ordered.add(playlistId);
        }
        ordered.addAll(extraPlaylistIds);
        return List.copyOf(ordered);
    }

    public String primaryPlaylistId() {
        var playlists = configuredPlaylistIds();
        return playlists.isEmpty() ? "" : playlists.get(0);
    }

    private static String normalizePjaxMode(String value) {
        var normalized = trimToEmpty(value);
        return switch (normalized) {
            case PJAX_MODE_PLUGIN -> PJAX_MODE_PLUGIN;
            case PJAX_MODE_THEME_COMPATIBLE -> PJAX_MODE_THEME_COMPATIBLE;
            default -> PJAX_MODE_OFF;
        };
    }

    private static String normalizeServerUrl(String value) {
        var normalized = trimToEmpty(value);
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public static String normalizePlaylistReference(String value) {
        var normalized = trimToEmpty(value);
        if (!StringUtils.hasText(normalized)) {
            return "";
        }

        var extracted = extractPlaylistId(normalized);
        return StringUtils.hasText(extracted) ? extracted : normalized;
    }

    private static List<String> normalizePlaylistReferences(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }

        var normalized = new ArrayList<String>();
        for (var value : values) {
            var playlistId = normalizePlaylistReference(value);
            if (StringUtils.hasText(playlistId)) {
                normalized.add(playlistId);
            }
        }
        return List.copyOf(normalized);
    }

    private static String extractPlaylistId(String value) {
        try {
            var uri = URI.create(value);
            var fragmentMatch = extractPlaylistIdFromPathLike(uri.getFragment());
            if (StringUtils.hasText(fragmentMatch)) {
                return fragmentMatch;
            }

            var pathMatch = extractPlaylistIdFromPathLike(uri.getPath());
            if (StringUtils.hasText(pathMatch)) {
                return pathMatch;
            }
        } catch (IllegalArgumentException ignored) {
            // Fall back to parsing the raw input below.
        }

        return extractPlaylistIdFromPathLike(value);
    }

    private static String extractPlaylistIdFromPathLike(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }

        var matcher = PLAYLIST_URL_PATTERN.matcher(value);
        if (matcher.find()) {
            return trimToEmpty(matcher.group(1));
        }

        return "";
    }

    private static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
