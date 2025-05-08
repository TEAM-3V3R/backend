package _v3r.project.morpheme.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.morpheme.dto.response.MorphemeResponse;
import _v3r.project.morpheme.service.MorphemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/morpheme")
@Tag(name = "형태소 컨트롤러", description = "형태소 관련 API입니다.")
public class MorphemeController {
    private final MorphemeService morphemeService;

    @PostMapping("/")
    @Operation(summary = "형태소 데이터 수신기능")
    public CustomApiResponse<MorphemeResponse> receiveMorpheme(
            @RequestHeader("user-no") Long userId,
            @RequestParam("prompt-id") Long promptId
    ) {
        MorphemeResponse response = morphemeService.receiveMorpheme(userId,promptId);
        return CustomApiResponse.success(response,200,"형태소 데이터 수신 성공");
    }

}
