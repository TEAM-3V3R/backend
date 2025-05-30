package _v3r.project.user.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FindUserResponse(Long userId, String name, LocalDateTime createAt) {

    public static FindUserResponse of(Long userId,String name,LocalDateTime createAt) {
        return FindUserResponse.builder()
                .userId(userId)
                .createAt(createAt)
                .name(name)
                .build();
    }

}
