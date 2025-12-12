package _v3r.project.prompt.dto.response;

import _v3r.project.prompt.domain.Chat;
import lombok.Builder;

@Builder
public record FindAllChatResponse(
        Long chatId,
        String chatTitle,
        Boolean isFinished,
        String promptContent
) {
    public static FindAllChatResponse of(Chat chat,String promptContent) {
        return FindAllChatResponse.builder()
                .chatId(chat.getChatId())
                .chatTitle(chat.getChatTitle())
                .isFinished(chat.getIsFinished())
                .promptContent(promptContent)
                .build();
    }
}
