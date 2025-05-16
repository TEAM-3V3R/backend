package _v3r.project.history.dto;

import _v3r.project.prompt.domain.enumtype.Paints;
import java.time.LocalDateTime;
import java.util.List;

public record DetailHistoryResponse(
        Long chatId,
        Paints paints,
        List<PromptHistory> prompts,
        String finalImageUrl // lastPrompt의 image_url
) {
    public record PromptHistory(
            LocalDateTime createdAt,
            String promptContent,
            String imageUrl,
            List<String> classifications
    ) {}
}
