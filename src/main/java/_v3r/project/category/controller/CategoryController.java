package _v3r.project.category.controller;

import _v3r.project.category.dto.response.ClassificationListResponse;
import _v3r.project.category.dto.response.ReceiveCategoryResponse;
import _v3r.project.category.service.CategoryService;
import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.auth.model.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
@Tag(name = "카테고리 컨트롤러", description = "카테고리 관련 API입니다.")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/receive-category")
    @Operation(summary = "카테고리 정보 저장")
    public CustomApiResponse<List<ReceiveCategoryResponse>> receiveCategory(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam("prompt-id") Long promptId) {

        List<ReceiveCategoryResponse> responses = categoryService.receiveCategory(principal.getUserId(), promptId);
        return CustomApiResponse.success(responses, 200, "카테고리 저장 및 응답 성공");
    }

    @GetMapping("/show-all-category-text")
    @Operation(summary = "카테고리-텍스트 조회")
    public CustomApiResponse<List<ReceiveCategoryResponse>> showAllCategoryText(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam("prompt-id") Long promptId) {
        List<ReceiveCategoryResponse> resonse = categoryService.showAllCategoryText(principal.getUserId(),promptId);
        return CustomApiResponse.success(resonse,200,"해당 프롬프트에 대한 카테고리-텍스트 조회 성공");
    }


    @GetMapping("/show-categorylist")
    @Operation(summary = "해당 프롬프트에 대한 카테고리 리스트 조회")
    public CustomApiResponse<List<ClassificationListResponse>> showCategoryList(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam("prompt-id") Long promptId) {
        List<ClassificationListResponse> resonse = categoryService.showCategoryList(principal.getUserId(),promptId);
        return CustomApiResponse.success(resonse,200,"해당 프롬프트에 대한 카테고리 리스트 조회 성공");
    }

}
