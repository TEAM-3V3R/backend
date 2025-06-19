package _v3r.project.user.dto.response;

import _v3r.project.user.domain.User;
import lombok.Builder;

@Builder
public record CreateUserResponse(
        Long userId,
        String id,
        String nickName
) {

    public static CreateUserResponse of(final User user) {
        return CreateUserResponse.builder()
                .userId(user.getUserId())
                .id(user.getId())
                .nickName(user.getNickname())
                .build();
    }
}
