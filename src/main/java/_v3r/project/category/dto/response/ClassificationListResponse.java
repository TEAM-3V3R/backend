package _v3r.project.category.dto.response;

import _v3r.project.category.domain.Category;
import java.util.List;
import lombok.Builder;

@Builder
public record ClassificationListResponse(Long promptId,String Classification) {

    public static ClassificationListResponse of(Category category) {
        return ClassificationListResponse.builder()
                .promptId(category.getPrompt().getPromptId())
                .Classification(category.getClassification())
                .build();

    }
}
