package _v3r.project.category.dto.response;

import _v3r.project.category.domain.Category;
import _v3r.project.prompt.domain.Prompt;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record ReceiveCategoryResponse(
        String text,
        String classification) {

    public static ReceiveCategoryResponse of(String text, String classification) {
        return ReceiveCategoryResponse.builder()
                .text(text)
                .classification(classification)
                .build();
    }

    public static List<ReceiveCategoryResponse> of(List<Category> categoryList) {
        return categoryList.stream()
                .map(category -> ReceiveCategoryResponse.builder()
                        .text(category.getText())
                        .classification(category.getClassification())
                        .build())
                .collect(Collectors.toList());
    }

    // DTO를 Category 엔티티로 변환
    public Category toEntity(final Prompt prompt) {
        return Category.builder()
                .prompt(prompt)
                .text(this.text)
                .classification(this.classification)
                .build();
    }
}
