package _v3r.project.prompt.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.response.ChatResponse;
import _v3r.project.prompt.dto.response.CreateChatResponse;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RestTemplate restTemplate;
    private final PromptService promptService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Value("${chatgpt.api-key}")
    private String apiKey;

    public String getChatResponse(Long userId,String promptContent) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        promptService.sendAndSavePrompt(userId, promptContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo-instruct",
                "prompt", promptContent,
                "max_tokens", 1000
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/completions",
                request,
                ChatResponse.class
        );

        return response.getBody().choices().get(0).text().trim();
    }

    public CreateChatResponse createChat(Paints paints) {
        Chat newChat = Chat.toEntity(paints);
        chatRepository.save(newChat);
        return CreateChatResponse.of(newChat);
    }
    //채팅방 조회

}
