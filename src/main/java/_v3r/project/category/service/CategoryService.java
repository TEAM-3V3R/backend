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
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FlaskService flaskService;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;

    public ReceiveCategoryResonse receiveCategory(Long userId,Long promptId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        CustomApiResponse<CategoryFlaskResponse> flaskResponse = flaskService.receiveCategory(promptId);

        CategoryFlaskResponse categoryData = flaskResponse.data();

        ReceiveCategoryResonse response = ReceiveCategoryResonse.of(promptId,categoryData);

        Category category = response.toEntity(prompt);

        categoryRepository.save(category);

        return response;
    }

    public List<ReceiveCategoryResonse> showAllCategoryText(Long userId,Long promptId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<Category> categoryList = categoryRepository.findAllByPromptId(promptId);

        return ReceiveCategoryResonse.of(categoryList);

    }
}
