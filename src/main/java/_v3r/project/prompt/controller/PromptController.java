package _v3r.project.prompt.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.auth.model.CustomUserDetails;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.request.ChatRequest;
import _v3r.project.prompt.dto.response.ImageResponse;
import _v3r.project.prompt.service.PromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prompt")
@Tag(name = "프롬프트 컨트롤러", description = "프롬프트 관련 API입니다.")
public class PromptController {

    private final PromptService promptService;

    @PostMapping("/generate-image")
    @Operation(summary = "이미지 생성 기능")
    public CustomApiResponse<ImageResponse> generateImage(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(name = "paints") Paints paints,
            @RequestBody ChatRequest request) {
        ImageResponse response = promptService.generateImage(principal.getUserId(),request.chatId(), paints, request.promptContent());

        return CustomApiResponse.success(response, 200, "이미지 생성 성공");
    }
}

