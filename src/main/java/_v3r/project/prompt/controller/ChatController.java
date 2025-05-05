package _v3r.project.prompt.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.request.ChatRequest;
import _v3r.project.prompt.dto.response.CreateChatResponse;
import _v3r.project.prompt.dto.response.FindAllChatResponse;
import _v3r.project.prompt.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("")
    public String chat(@RequestHeader("user-no") Long userId,
            @RequestBody ChatRequest request) {
        return chatService.getChatResponse(userId, request.ChatId(),request.promptContent());
    }

    @PostMapping("/create")
    public CustomApiResponse<CreateChatResponse> createChat(@RequestHeader("user-no") Long userId,
            @RequestParam(name = "paints") Paints paints) {
        CreateChatResponse response = chatService.createChat(userId,paints);
        return CustomApiResponse.success(response,200,"채팅방 생성 성공");
    }

    @GetMapping("/find-all-chat")
    public CustomApiResponse<List<FindAllChatResponse>> findAllChats(@RequestHeader("user-no") Long userId) {
        List<FindAllChatResponse> response = chatService.findAllChats(userId);
        return CustomApiResponse.success(response,200,"전체 채팅방 조회 성공");
    }

}
