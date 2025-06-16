package _v3r.project.user.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FindUserResponse(Long userId, String name, LocalDateTime createdAt) {

    public static FindUserResponse of(Long userId,String name,LocalDateTime createdAt) {
        return FindUserResponse.builder()
                .userId(userId)
                .name(name)
                .createdAt(createdAt)
                .build();
    }

}
