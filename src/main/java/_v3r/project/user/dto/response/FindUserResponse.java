package _v3r.project.user.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FindUserResponse(Long userId, String id,String nickName, LocalDateTime createdAt) {

    public static FindUserResponse of(Long userId,String id,String nickName,LocalDateTime createdAt) {
        return FindUserResponse.builder()
                .userId(userId)
                .id(id)
                .nickName(nickName)
                .createdAt(createdAt)
                .build();
    }

}
