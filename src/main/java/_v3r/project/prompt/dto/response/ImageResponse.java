package _v3r.project.prompt.dto.response;

import _v3r.project.prompt.domain.enumtype.Paints;
import java.util.List;

public record ImageResponse(Paints paints, List<ImageData> data) {
    public record ImageData(String url) {}

}
