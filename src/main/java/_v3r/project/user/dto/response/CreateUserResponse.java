package _v3r.project.user.dto.response;

import _v3r.project.user.domain.User;
import lombok.Builder;

@Builder
public record CreateUserResponse(
        String id,
        String nickName
) {

    public static CreateUserResponse of(final User user) {
        return CreateUserResponse.builder()
                .id(user.getLoginId())
                .nickName(user.getNickname())
                .build();
    }
}
