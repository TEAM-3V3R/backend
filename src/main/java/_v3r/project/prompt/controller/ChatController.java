package _v3r.project.prompt.controller;

import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.request.ChatRequest;
import _v3r.project.prompt.dto.response.ImageResponse;
import _v3r.project.prompt.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;
    @PostMapping("")
    public String chat(@RequestBody ChatRequest request) {
        return chatService.getChatResponse(request.promptContent());
    }

    @PostMapping("/fish-image")
    public ImageResponse generateFishImage(@RequestParam(name = "paints") Paints paints, @RequestBody ChatRequest request) {
        return chatService.generateFishImage(paints.어해도,request.promptContent());
    }

    @PostMapping("/mountain-image")
    public ImageResponse generateMountainImage(@RequestParam(name = "paints") Paints paints, @RequestBody ChatRequest request) {
        return chatService.generateMountainImage(paints.산수도,request.promptContent());
    }


}
