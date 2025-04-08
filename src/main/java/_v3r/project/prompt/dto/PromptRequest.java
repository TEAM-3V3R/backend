package _v3r.project.prompt.dto;

import _v3r.project.prompt.domain.Prompt;
import _v3r.project.user.domain.User;
import jakarta.validation.constraints.NotNull;

public record PromptRequest(@NotNull String promptContent) {

    public Prompt toEntity(final User user) {
        return Prompt.builder()
                .user(user)
                .promptContent(this.promptContent)
                .build();

    }
}
