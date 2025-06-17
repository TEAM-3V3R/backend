package _v3r.project.prompt.dto.response;

import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.enumtype.Paints;
import lombok.Builder;

@Builder
public record CreateChatResponse(
        Paints paints,
        Long chatId
) {
    public static CreateChatResponse of(Chat chat) {
        return CreateChatResponse.builder()
                .chatId(chat.getId())
                .paints(chat.getPaints())
                .build();
    }

}
