package _v3r.project.prompt.dto;

import _v3r.project.prompt.domain.Prompt;
import lombok.Builder;

@Builder
public record PromptResponse(Long id,String promptContent) {

    public static PromptResponse of(final Prompt prompt) {
        return PromptResponse.builder()
                .id(prompt.getId())
                .promptContent(prompt.getPromptContent())
                .build();
    }
}
