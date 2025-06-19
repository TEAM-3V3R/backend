package _v3r.project.prompt.dto.response;

import _v3r.project.prompt.domain.Prompt;
import lombok.Builder;

@Builder
public record PromptResponse(Long promptId,String promptContent) {

    public static PromptResponse of(final Prompt prompt) {
        return PromptResponse.builder()
                .promptId(prompt.getPromptId())
                .promptContent(prompt.getPromptContent())
                .build();
    }
}
