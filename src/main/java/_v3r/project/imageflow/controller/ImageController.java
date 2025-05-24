package _v3r.project.imageflow.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.imageflow.DownloadType;
import _v3r.project.imageflow.service.ImageService;
import _v3r.project.prompt.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
@Tag(name = "이미지 요소분리 컨트롤러", description = "이미지 요소분리 관련 API입니다.")
public class ImageController {

    private final ImageService imageService;
    private final ChatService chatService;

    @PostMapping("/download")
    public CustomApiResponse<?> segmentResultImage(
            @RequestHeader("user-no") Long userId,
            @RequestParam(name = "chatId") Long chatId,
            @RequestParam(name = "downlaod-type")DownloadType downloadType
    ) {
        if(downloadType == downloadType.최종_이미지_저장) {
            imageService.downloadResultImage(userId, chatId);
            return CustomApiResponse.success(null,200,"최종 이미지 다운로드완료");
        } else if (downloadType == downloadType.채팅방_종료) {
            chatService.finishChat(userId, chatId);
            return CustomApiResponse.success(null,200,"채팅방 종료 완료");
        } else {
            imageService.segmentResultImage(userId, chatId);
            return CustomApiResponse.success(null,200,"요소분리 이미지 다운로드 완료");
        }
    }

}
