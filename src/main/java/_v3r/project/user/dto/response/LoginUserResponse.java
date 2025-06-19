package _v3r.project.user.dto.response;

import lombok.Builder;

@Builder
public record LoginUserResponse(
        Long userId,
        String id,
        String nickName

) {

}
