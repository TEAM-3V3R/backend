package _v3r.project.common.auth.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken
) {
    public static LoginResponse of(String accessToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
