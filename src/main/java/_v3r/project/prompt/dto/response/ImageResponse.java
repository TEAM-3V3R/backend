package _v3r.project.prompt.dto.response;

import java.util.List;

public record ImageResponse(List<ImageData> data) {
    public record ImageData(String url) {}

}
