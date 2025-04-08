package _v3r.project.user.dto.response;

import _v3r.project.user.domain.User;
import lombok.Builder;

@Builder
public record CreateUserResponse(
        Long id,
        String name
) {

    public static CreateUserResponse of(final User user) {
        return CreateUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
