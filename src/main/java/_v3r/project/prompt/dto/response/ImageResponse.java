package _v3r.project.prompt.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ImageResponse(
        Long promptId,
        List<ImageData> data
) {
    public record ImageData(
            @JsonProperty("url")
            String base64
    ) {}

    public static ImageResponse of(Long promptId, String... urls) {
        return new ImageResponse(
                promptId,
                List.of(urls).stream().map(ImageData::new).toList()
        );
    }
}
