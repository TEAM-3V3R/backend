package _v3r.project.category.dto.response;

import _v3r.project.category.domain.Category;
import _v3r.project.prompt.domain.Prompt;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record ReceiveCategoryResonse(
        Long promptId,
        String text,
        String classification) {

    public static ReceiveCategoryResonse of(Long promptId, String text, String classification) {
        return ReceiveCategoryResonse.builder()
                .promptId(promptId)
                .text(text)
                .classification(classification)
                .build();
    }


    public static List<ReceiveCategoryResonse> of(List<Category> categoryList) {
        return categoryList.stream()
                .map(category -> ReceiveCategoryResonse.builder()
                        .promptId(category.getPrompt().getId())
                        .text(category.getText())
                        .classification(category.getClassification())
                        .build())
                .collect(Collectors.toList());
    }


    public Category toEntity(final Prompt prompt) {
        return Category.builder()
                .prompt(prompt)
                .text(this.text)
                .classification(this.classification)
                .build();
    }
}
