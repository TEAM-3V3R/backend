package _v3r.project.imageflow.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.imageflow.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
@Tag(name = "이미지 요소분리 컨트롤러", description = "이미지 요소분리 관련 API입니다.")
public class ImageController {

    private final ImageService imageService;

    //TODO 요소분리 컨트롤러로 한개 api로 만들기 - 서비스 로직 구체화되면 추가


}
