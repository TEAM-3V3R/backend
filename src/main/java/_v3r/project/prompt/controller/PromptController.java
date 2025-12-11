package _v3r.project.prompt.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.request.ChatRequest;
import _v3r.project.prompt.dto.request.InpaintingImageRequest;
import _v3r.project.prompt.dto.response.ImageResponse;
import _v3r.project.prompt.service.PromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prompt")
@Tag(name = "프롬프트 컨트롤러", description = "프롬프트 관련 API입니다.")
public class PromptController {

    private final PromptService promptService;

    @PostMapping("/generate-image")
    @Operation(summary = "이미지 생성 기능")
    public CustomApiResponse<ImageResponse> generateImage(
            @RequestHeader("user-no") Long userId,
            @RequestParam(name = "paints") Paints paints,
            @RequestBody ChatRequest request) {

        ImageResponse response = switch (paints) {
            case 산수도 -> promptService.generateMountainImage(
                    userId, request.chatId(), Paints.산수도, request.promptContent()
            );
            case 어해도 -> promptService.generateFishImage(
                    userId, request.chatId(), Paints.어해도, request.promptContent()
            );
            case 탱화 -> promptService.generatePeopleImage(
                    userId, request.chatId(), Paints.탱화, request.promptContent()
            );
        };

        return CustomApiResponse.success(response, 200, "이미지 생성 성공");
    }
}

