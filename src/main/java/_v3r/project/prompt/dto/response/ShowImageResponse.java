package _v3r.project.prompt.dto.response;

import lombok.Builder;

@Builder
public record ShowImageResponse(
        String imageUrl
) {
    public static ShowImageResponse of(String imageUrl) {
        return ShowImageResponse.builder()
                .imageUrl(imageUrl)
                .build();
    }
}
