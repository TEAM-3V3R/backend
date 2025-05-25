package _v3r.project.history.dto;

import _v3r.project.prompt.domain.enumtype.Paints;
import java.time.LocalDateTime;

public record AllHistoryResponse(
        Long chatId,
        // 채팅방의 생성날짜
        LocalDateTime createAt,
        Paints paints,
        // 마지막 프롬프트의 image_url
        String image_url,
        //첫번째 프롬프트 갖고오기
        String promptContent
) {

}
