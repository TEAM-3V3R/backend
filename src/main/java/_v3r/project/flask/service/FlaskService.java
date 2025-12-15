package _v3r.project.flask.service;

import _v3r.project.category.dto.response.ReceiveCategoryResponse;
import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.imageflow.dto.SegmentResponse;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.report.dto.ReportResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FlaskService {
    //TODO customApiResponse 컨트롤러 단으로 책임 분리하기

    private final RestTemplate restTemplate;
    private final PromptRepository promptRepository;
    private final ChatRepository chatRepository;

    public CustomApiResponse<List<ReceiveCategoryResponse>> receiveCategory(Long promptId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        String promptContent = prompt.getPromptContent();

        Map<String, Object> request = new HashMap<>();
        request.put("promptContent", promptContent);
        request.put("promptId", promptId);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://cf10725067a1.ngrok-free.app/category/predict",
                HttpMethod.POST,
                entity,
                String.class
        );

        try {
            String responseBody = response.getBody();

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode rootNode = objectMapper.readTree(responseBody);
            List<ReceiveCategoryResponse> results = new ArrayList<>();

            if (rootNode.has("results")) {
                for (JsonNode node : rootNode.get("results")) {
                    String text = node.get("text").asText();
                    String classification = node.has("classification") ? node.get("classification").asText() : null;

                    results.add(ReceiveCategoryResponse.of(text, classification));
                }
            }

            return CustomApiResponse.success(results, 200, "카테고리 수신 성공");

        } catch (Exception e) {
            throw new EverException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<SegmentResponse> sendResultImageToFlask(String finalImage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("resultImage", finalImage);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<List<SegmentResponse>> response = restTemplate.exchange(
                "https://cf10725067a1.ngrok-free.app/sam",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<SegmentResponse>>() {}
        );

        return response.getBody();
    }

    public CustomApiResponse<ReportResponse> receiveReport(Long chatId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<String> promptContents = promptRepository.findByChat_ChatId(chatId).stream()
                .map(Prompt::getPromptContent)
                .toList();

        Map<String, Object> request = new HashMap<>();
        request.put("chatId", chatId);
        request.put("promptContents", promptContents);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<ReportResponse> response = restTemplate.exchange(
                    "https://cf10725067a1.ngrok-free.app/analyzer/analyze",
                    HttpMethod.POST,
                    entity,
                    ReportResponse.class
            );

            ReportResponse flaskResponse = response.getBody();
            return CustomApiResponse.success(flaskResponse, 200, "리포트 수신 성공");

        } catch (Exception e) {
            throw new EverException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
