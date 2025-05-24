package _v3r.project.imageflow.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.imageflow.service.ImageService;
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

//    @PostMapping("/segment")
//    public CustomApiResponse<?> segmentResultImage(
//            @RequestHeader("user-no") Long userId,
//            @RequestParam(name = "chatId") Long chatId
//    ) {
//
//    }

}
