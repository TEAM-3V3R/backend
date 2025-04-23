package _v3r.project.prompt.service;

import _v3r.project.prompt.dto.response.ChatResponse;
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

    @Value("${chatgpt.api-key}")
    private String apiKey;

    public ChatResponse getChatResponse(String promptContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo-instruct",
                "prompt", promptContent,
                "max_tokens", 100
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/completions",
                request,
                ChatResponse.class
        );

        return response.getBody();  // ✅ 전체 구조 반환
    }
}
