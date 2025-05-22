package _v3r.project.prompt.dto.response;

import _v3r.project.prompt.domain.enumtype.Paints;
import java.util.List;

public record FindChatResponse(
        Long chatId,
        Boolean isFinished,
        Paints paints,
        List<PromptItem> prompts
) {
    public record PromptItem(
            Boolean inpaintingImage,
            String promptContent,
            String imageUrl
    ) {}
}

