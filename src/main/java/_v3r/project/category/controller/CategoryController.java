package _v3r.project.category.controller;

import _v3r.project.category.dto.response.ClassificationListResponse;
import _v3r.project.category.dto.response.ReceiveCategoryResonse;
import _v3r.project.category.service.CategoryService;
import _v3r.project.common.apiResponse.CustomApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/receive-category")
    public CustomApiResponse<ReceiveCategoryResonse> receiveCategory(
            @RequestHeader("user-no") Long userId,
            @RequestParam("prompt-id") Long promptId) {
        ReceiveCategoryResonse response = categoryService.receiveCategory(userId,promptId);
        return CustomApiResponse.success(response, 200, "카테고리 저장 및 응답 성공");
    }

    @GetMapping("/show-all-category-text")
    public CustomApiResponse<List<ReceiveCategoryResonse>> showAllCategoryText(
            @RequestHeader("user-no") Long userId,
            @RequestParam("prompt-id") Long promptId) {
        List<ReceiveCategoryResonse> resonse = categoryService.showAllCategoryText(userId,promptId);
        return CustomApiResponse.success(resonse,200,"해당 프롬프트에 대한 카테고리-텍스트 조회 성공");
    }


    @GetMapping("/show-categorylist")
    public CustomApiResponse<List<ClassificationListResponse>> showCategoryList(
            @RequestHeader("user-no") Long userId,
            @RequestParam("prompt-id") Long promptId) {
        List<ClassificationListResponse> resonse = categoryService.showCategoryList(userId,promptId);
        return CustomApiResponse.success(resonse,200,"해당 프롬프트에 대한 카테고리 리스트 조회 성공");
    }

}
