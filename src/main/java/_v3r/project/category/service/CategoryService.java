package _v3r.project.category.service;

import _v3r.project.category.domain.Category;
import _v3r.project.category.dto.response.ReceiveCategoryResonse;
import _v3r.project.category.repository.CategoryRepository;
import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.flask.dto.CategoryFlaskResponse;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FlaskService flaskService;
    private final PromptRepository promptRepository;

    public ReceiveCategoryResonse receiveCategory(Long promptId){
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        CustomApiResponse<CategoryFlaskResponse> flaskResponse = flaskService.receiveCategory(promptId);

        CategoryFlaskResponse categoryData = flaskResponse.data();

        ReceiveCategoryResonse response = ReceiveCategoryResonse.of(promptId,categoryData);

        Category category = response.toEntity(prompt);

        categoryRepository.save(category);

        return response;
    }

}
