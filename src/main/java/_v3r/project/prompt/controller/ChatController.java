package _v3r.project.prompt.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.request.ChatRequest;
import _v3r.project.prompt.dto.response.CreateChatResponse;
import _v3r.project.prompt.dto.response.FindAllChatResponse;
import _v3r.project.prompt.dto.response.FindChatResponse;
import _v3r.project.prompt.dto.response.UpdateChatTitleResponse;
import _v3r.project.prompt.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@Tag(name = "채팅방 컨트롤러", description = "채팅방 관련 API입니다.")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("")
    @Operation(summary = "채팅 가능 ex) 프롬프트 - 프롬프트 답변")
    public String chat(@RequestHeader("user-no") Long userId,
            @RequestBody ChatRequest request) {
        return chatService.getChatResponse(userId, request.chatId(),request.promptContent());
    }

    @PostMapping("/create")
    @Operation(summary = "채팅방 생성",description = "어해도 : 0, 산수도 : 1, 탱화 : 2")
    public CustomApiResponse<CreateChatResponse> createChat(@RequestHeader("user-no") Long userId,
            @RequestParam(name = "paints") Paints paints) {
        CreateChatResponse response = chatService.createChat(userId,paints);
        return CustomApiResponse.success(response,200,"채팅방 생성 성공");
    }

    @GetMapping("/find-all-chat")
    @Operation(summary = "모든 채팅방 조회기능")
    public CustomApiResponse<List<FindAllChatResponse>> findAllChats(@RequestHeader("user-no") Long userId) {
        List<FindAllChatResponse> response = chatService.findAllChats(userId);
        return CustomApiResponse.success(response,200,"전체 채팅방 조회 성공");
    }
    @GetMapping("/find-chat")
    @Operation(summary = "특정 채팅방 선택 후 채팅 내역 조회 기능")
    public CustomApiResponse<List<FindChatResponse>> findChat(
            @RequestHeader("user-no") Long userId,
            @RequestParam(name = "chatId") Long chatId
    ) {
        FindChatResponse response =chatService.findChat(userId, chatId);
        return CustomApiResponse.success(List.of(response),200,"특정 채팅방 조회 성공");
    }

    @PatchMapping("/{chatId}/title")
    @Operation(summary = "특정 채팅방 선택 채팅방 이름 수정 기능")
    public CustomApiResponse<UpdateChatTitleResponse> updateChatTitle(
            @RequestHeader("user-no") Long userId,
            @PathVariable("chatId") Long chatId,
            @RequestParam("chatTitle") String chatTitle) {

        UpdateChatTitleResponse response = chatService.updateChat(userId, chatId, chatTitle);
        return CustomApiResponse.success(response, 200, "채팅 제목 수정 완료");
    }

}
