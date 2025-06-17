package _v3r.project.imageflow.controller;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import org.springframework.core.io.Resource;
import _v3r.project.imageflow.DownloadType;
import _v3r.project.imageflow.service.ImageService;
import _v3r.project.prompt.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> segmentResultImage(
            @RequestHeader("user-no") Long userId,
            @RequestParam(name = "chatId") Long chatId,
            @RequestParam(name = "downlaod-type") DownloadType downloadType
    ) {
        if (downloadType == DownloadType.최종_이미지_저장) {
            File file = imageService.downloadResultImage(userId, chatId);
            try {
                Resource resource = new InputStreamResource(new FileInputStream(file));
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(file.length())
                        .body(resource);
            } catch (FileNotFoundException e) {
                throw new EverException(ErrorCode.ENTITY_NOT_FOUND);
            }
        } else if (downloadType == DownloadType.채팅방_종료) {
            chatService.finishChat(userId, chatId);
            return ResponseEntity.ok().body("채팅방 종료 완료");
        } else {
            imageService.segmentResultImage(userId, chatId);
            return ResponseEntity.ok().body("요소분리 이미지 다운로드 완료");
        }
    }


}
