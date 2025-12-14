package _v3r.project.history.dto;

import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.enumtype.Paints;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AllHistoryResponse(
        Long chatId,
        String chatTitle,
        String promptContent,
        LocalDateTime createAt,
        Paints paints,
        String image_url
) {
    public static AllHistoryResponse of(Chat chat,String promptContent,String image_url){
        return AllHistoryResponse.builder()
                .chatId(chat.getChatId())
                .chatTitle(chat.getChatTitle())
                .createAt(chat.getCreatedAt())
                .paints(chat.getPaints())
                .promptContent(promptContent)
                .image_url(image_url)
                .build();
    }
}
