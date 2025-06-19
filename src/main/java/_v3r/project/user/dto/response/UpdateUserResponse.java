package _v3r.project.user.dto.response;

import _v3r.project.user.domain.User;
import lombok.Builder;

@Builder
public record UpdateUserResponse(Long userId,String idName) {

    public static UpdateUserResponse of(User user) {
        return UpdateUserResponse.builder()
                .userId(user.getId())
                .idName(user.getIdName())
                .build();
    }
}
