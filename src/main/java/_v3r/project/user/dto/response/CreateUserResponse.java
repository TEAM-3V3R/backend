package _v3r.project.user.dto.response;

import _v3r.project.user.domain.User;
import lombok.Builder;

@Builder
public record CreateUserResponse(
        Long userId,
        String idName,
        String nickName
) {

    public static CreateUserResponse of(final User user) {
        return CreateUserResponse.builder()
                .userId(user.getId())
                .idName(user.getIdName())
                .nickName(user.getNickname())
                .build();
    }
}
