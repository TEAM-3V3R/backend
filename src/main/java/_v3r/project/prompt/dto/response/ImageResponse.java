package _v3r.project.prompt.dto.response;

import _v3r.project.prompt.domain.enumtype.Paints;
import java.util.List;

public record ImageResponse(
        Long promptId,
        List<ImageData> data
) {
    public record ImageData(String url) {}

    public static ImageResponse of(Long promptId, String... urls) {
        return new ImageResponse(
                promptId,
                List.of(urls).stream().map(ImageData::new).toList()
        );
    }
}
