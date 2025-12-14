package _v3r.project.history.dto;

import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.domain.enumtype.Paints;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record DetailHistoryResponse(
        Long chatId,
        String chatTitle,
        Paints paints,
        List<PromptHistory> prompts,
        String finalImageUrl  // 채팅방의 마지막 사진 url
) {

    public record PromptHistory(
            LocalDateTime createdAt,
            String promptContent,
            String imageUrl,
            List<String> classifications
    ) {
        public static PromptHistory of(
                Prompt prompt,
                List<String> classifications
        ) {
            return new PromptHistory(
                    prompt.getCreatedAt(),
                    prompt.getPromptContent(),
                    prompt.getImageUrl(),
                    classifications
            );
        }
    }

    public static DetailHistoryResponse of(
            Chat chat,
            List<PromptHistory> promptHistories
    ) {
        PromptHistory lastPrompt =
                promptHistories.get(promptHistories.size() - 1);

        return DetailHistoryResponse.builder()
                .chatId(chat.getChatId())
                .chatTitle(chat.getChatTitle())
                .paints(chat.getPaints())
                .prompts(promptHistories)
                .finalImageUrl(lastPrompt.imageUrl())
                .build();
    }
}
