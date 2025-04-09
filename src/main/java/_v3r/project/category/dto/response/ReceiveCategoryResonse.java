package _v3r.project.category.dto.response;

import _v3r.project.category.domain.Category;
import _v3r.project.flask.dto.CategoryFlaskResponse;
import _v3r.project.prompt.domain.Prompt;
import lombok.Builder;

@Builder
public record ReceiveCategoryResonse(
        Long promptId,
        String text,
        String classification) {

    public static ReceiveCategoryResonse of(Long promptId, CategoryFlaskResponse response) {
        return ReceiveCategoryResonse.builder()
                .promptId(promptId)
                .text(response.text())
                .classification(response.classification())
                .build();
    }

    public Category toEntity(final Prompt prompt) {
        return Category.builder()
                .prompt(prompt)
                .text(this.text)
                .classification(this.classification)
                .build();
    }
}
