package _v3r.project.history.dto;

import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
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
    public static AllHistoryResponse of(Chat chat, Prompt lastPrompt){
        String title =
                (chat.getChatTitle() != null)
                        ? chat.getChatTitle()
                        : (lastPrompt != null ? lastPrompt.getPromptContent() : "생성된 채팅방");
        String imageUrl =
                lastPrompt != null ? lastPrompt.getImageUrl() : null;

        return AllHistoryResponse.builder()
                .chatId(chat.getChatId())
                .chatTitle(title)
                .paints(chat.getPaints())
                .image_url(imageUrl)
                .createAt(chat.getCreatedAt())
                .build();
    }
}
