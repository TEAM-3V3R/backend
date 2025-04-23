package _v3r.project.prompt.controller;

import _v3r.project.prompt.dto.request.ChatRequest;
import _v3r.project.prompt.dto.response.ChatResponse;
import _v3r.project.prompt.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;
    @PostMapping("")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.getChatResponse(request.promptContent());
    }

}
