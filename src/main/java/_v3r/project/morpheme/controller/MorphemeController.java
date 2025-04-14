package _v3r.project.morpheme.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.morpheme.dto.response.MorphemeResponse;
import _v3r.project.morpheme.service.MorphemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/morpheme")
public class MorphemeController {
    private final MorphemeService morphemeService;

    @PostMapping("/")
    public CustomApiResponse<MorphemeResponse> receiveMorpheme(
            @RequestHeader("user-no") Long userId,
            @RequestParam("prompt-id") Long promptId
    ) {
        MorphemeResponse response = morphemeService.receiveMorpheme(userId,promptId);
        return CustomApiResponse.success(response,200,"형태소 데이터 수신 성공");
    }

}
