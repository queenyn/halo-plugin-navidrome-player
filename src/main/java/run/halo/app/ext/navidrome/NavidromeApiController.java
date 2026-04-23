package run.halo.app.ext.navidrome;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;

@Component
public class NavidromeApiController implements CustomEndpoint {

    private final NavidromeService navidromeService;

    public NavidromeApiController(NavidromeService navidromeService) {
        this.navidromeService = navidromeService;
    }

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        return RouterFunctions.route()
            .GET("playlist", this::playlist)
            .GET("playlists", this::playlists)
            .build();
    }

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion("ext.navidrome/v1");
    }

    private Mono<ServerResponse> playlist(ServerRequest request) {
        var playlistId = request.queryParam("playlistId").orElse("");
        return navidromeService.getPlaylistForAPlayer(playlistId)
            .flatMap(audios -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(audios))
            .onErrorResume(ResponseStatusException.class, error -> ServerResponse
                .status(error.getStatusCode())
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(error.getReason() == null ? "Failed to load Navidrome playlist" : error.getReason()))
            .onErrorResume(error -> ServerResponse.status(502)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Failed to load Navidrome playlist"));
    }

    private Mono<ServerResponse> playlists(ServerRequest request) {
        return navidromeService.getConfiguredPlaylists()
            .flatMap(playlists -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(playlists))
            .onErrorResume(ResponseStatusException.class, error -> ServerResponse
                .status(error.getStatusCode())
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(error.getReason() == null ? "Failed to load Navidrome playlists" : error.getReason()))
            .onErrorResume(error -> ServerResponse.status(502)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Failed to load Navidrome playlists"));
    }
}
