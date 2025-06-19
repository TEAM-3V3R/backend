package _v3r.project.category.service;

import _v3r.project.category.domain.Category;
import _v3r.project.category.dto.response.ClassificationListResponse;
import _v3r.project.category.dto.response.ReceiveCategoryResponse;
import _v3r.project.category.repository.CategoryRepository;
import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FlaskService flaskService;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<ReceiveCategoryResponse> receiveCategory(Long userId, Long promptId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        CustomApiResponse<List<ReceiveCategoryResponse>> flaskResponse = flaskService.receiveCategory(promptId);
        List<ReceiveCategoryResponse> categoryDataList = flaskResponse.data();

        List<Category> categories = new ArrayList<>();

        for (ReceiveCategoryResponse categoryData : categoryDataList) {
            ReceiveCategoryResponse response = ReceiveCategoryResponse.of(categoryData.text(), categoryData.classification());
            Category category = response.toEntity(prompt);
            categories.add(category);
        }

        categoryRepository.saveAll(categories);

        return categoryDataList;
    }

    @Transactional(readOnly = true)
    public List<ReceiveCategoryResponse> showAllCategoryText(Long userId,Long promptId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));


        List<Category> categoryList = categoryRepository.findAllByPromptId(promptId);

        if (categoryList.isEmpty()) {
            throw new EverException(ErrorCode.ENTITY_NOT_FOUND);
        }

        return ReceiveCategoryResponse.of(categoryList);
    }
    @Transactional(readOnly = true)
    public List<ClassificationListResponse> showCategoryList(Long userId,Long promptId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<Category> categoryList = categoryRepository.findAllByPromptId(promptId);

        if (categoryList.isEmpty()) {
            throw new EverException(ErrorCode.ENTITY_NOT_FOUND);
        }

        return categoryList.stream()
                .map(ClassificationListResponse::of)
                .toList();
    }

}
