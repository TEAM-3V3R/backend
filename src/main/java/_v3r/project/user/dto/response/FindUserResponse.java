package _v3r.project.user.dto.response;

import lombok.Builder;

@Builder
public record FindUserResponse(Long userId,String name) {

    public static FindUserResponse of(Long userId,String name) {
        return FindUserResponse.builder()
                .userId(userId)
                .name(name)
                .build();
    }

}
