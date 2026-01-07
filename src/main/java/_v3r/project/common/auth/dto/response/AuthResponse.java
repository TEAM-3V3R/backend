package _v3r.project.common.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken
) {
    public static AuthResponse of(String accessToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
