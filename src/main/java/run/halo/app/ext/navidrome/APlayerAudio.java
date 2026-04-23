package run.halo.app.ext.navidrome;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record APlayerAudio(
    String name,
    String artist,
    String url,
    String cover,
    Integer duration
) {
}
