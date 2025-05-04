package _v3r.project.prompt.controller;

import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.request.ChatRequest;
import _v3r.project.prompt.dto.response.ImageResponse;
import _v3r.project.prompt.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prompt")
public class PromptController {
    private final PromptService promptService;
    @PostMapping("/fish-image")
    public ImageResponse generateFishImage(@RequestHeader("user-no") Long userId,@RequestParam(name = "paints") Paints paints, @RequestBody ChatRequest request) {
        return promptService.generateFishImage(userId,paints.어해도,request.promptContent());
    }

    @PostMapping("/mountain-image")
    public ImageResponse generateMountainImage(@RequestHeader("user-no") Long userId,@RequestParam(name = "paints") Paints paints, @RequestBody ChatRequest request) {
        return promptService.generateMountainImage(userId,paints.산수도,request.promptContent());
    }

}
