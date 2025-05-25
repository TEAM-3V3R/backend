package _v3r.project.user.dto.response;

import lombok.Builder;

@Builder
public record FindUserResponse(Long userId,String name) {
    //TODO 채팅관련 기능 추가후 사용자 조회 시 완료된 채팅방,생성된 이미지 개수 보이도록 수정필요

    public static FindUserResponse of(Long userId,String name) {
        return FindUserResponse.builder()
                .userId(userId)
                .name(name)
                .build();
    }

}
