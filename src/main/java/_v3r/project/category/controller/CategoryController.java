package _v3r.project.category.controller;

import _v3r.project.category.dto.response.ReceiveCategoryResonse;
import _v3r.project.category.service.CategoryService;
import _v3r.project.common.apiResponse.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/receive-category")
    public CustomApiResponse<ReceiveCategoryResonse> receiveCategory(@RequestParam Long promptId) {
        ReceiveCategoryResonse response = categoryService.receiveCategory(promptId);
        return CustomApiResponse.success(response, 200, "카테고리 저장 및 응답 성공");
    }



}
