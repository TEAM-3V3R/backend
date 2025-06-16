package _v3r.project.prompt.dto.response;

import lombok.Builder;

@Builder
public record UpdateChatTitleResponse(Long chatId,String chatTitle) {

    public static UpdateChatTitleResponse of(Long chatId, String chatTitle) {
        return UpdateChatTitleResponse.builder()
                .chatId(chatId)
                .chatTitle(chatTitle)
                .build();
    }
}
