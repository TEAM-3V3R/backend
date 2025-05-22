package _v3r.project.prompt.controller;

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
    @PostMapping("/fish-image")
    @Operation(summary = "어해도 채팅방의 프롬프트 전송기능")
    public ImageResponse generateFishImage(@RequestHeader("user-no") Long userId,@RequestParam(name = "paints") Paints paints, @RequestBody ChatRequest request) {
        return promptService.generateFishImage(userId, request.chatId(),paints.어해도,request.promptContent());
    }

    @PostMapping("/mountain-image")
    @Operation(summary = "산수도 채팅방의 프롬프트 전송기능")
    public ImageResponse generateMountainImage(@RequestHeader("user-no") Long userId,@RequestParam(name = "paints") Paints paints, @RequestBody ChatRequest request) {
        return promptService.generateMountainImage(userId, request.chatId(), paints.산수도,request.promptContent());
    }

    @PostMapping("/edit")
    @Operation(summary = "인페인팅 기능")
    //TODO imageFile,maskFile 둘다 파일로 받을지 url로 받을지 회의 후 결정 (imageFile만 url로 ?)
    public ImageResponse generateInpaintingImage(
            @RequestHeader("user-no") Long userId,
            @RequestBody InpaintingImageRequest request,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("maskFile") MultipartFile maskFile
    ) throws IOException{
        return promptService.generateInpaintingImage(
                userId,
                request.chatId(),
                request.promptContent(),
                imageFile,
                maskFile
        );
    }


}
