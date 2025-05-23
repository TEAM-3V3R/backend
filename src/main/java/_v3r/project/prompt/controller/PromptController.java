package _v3r.project.prompt.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.exception.EverException;
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
    @Operation(summary = "달리 통한 이미지 생성 기능")
    public CustomApiResponse<ImageResponse> generateMountainImage(
            @RequestHeader("user-no") Long userId,
            @RequestParam(name = "paints") Paints paints,
            @RequestBody ChatRequest request) {

        ImageResponse response;
        if (paints == Paints.산수도) {
            response = promptService.generateMountainImage(userId, request.chatId(), Paints.산수도, request.promptContent());
        } else if (paints == Paints.어해도) {
            response = promptService.generateFishImage(userId, request.chatId(), Paints.어해도, request.promptContent());
        } else {
            response = promptService.generatePeopleImage(userId, request.chatId(), Paints.탱화, request.promptContent());
        }
        return CustomApiResponse.success(response,200,"이미지 생성 성공");
    }

    @PostMapping("/edit")
    @Operation(summary = "인페인팅 기능")
    public CustomApiResponse<ImageResponse> generateInpaintingImage(
            @RequestHeader("user-no") Long userId,
            @RequestBody InpaintingImageRequest request,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("maskFile") MultipartFile maskFile
    ) throws IOException {
        ImageResponse response = promptService.generateInpaintingImage(
                userId,
                request.chatId(),
                request.promptContent(),
                imageFile,
                maskFile
        );
        return CustomApiResponse.success(response,200,"인페인팅 성공");
    }
}
