package _v3r.project.flask.service;

import _v3r.project.category.dto.response.ReceiveCategoryResponse;
import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.imageflow.dto.SegmentResponse;
import _v3r.project.morpheme.dto.response.MorphemeResponse;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.flask.dto.FlaskResponse;
import _v3r.project.prompt.repository.PromptRepository;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FlaskService {
    //TODO customApiResponse 컨트롤러 단으로 책임 분리하기

    private final RestTemplate restTemplate;
    private final PromptRepository promptRepository;

    public CustomApiResponse<FlaskResponse> sendPromptToFlask(String promptContent) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("promptContent", promptContent);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<FlaskResponse> response = restTemplate.exchange(
                "http://3.37.172.79:80/prompt",
                HttpMethod.POST,
                entity,
                FlaskResponse.class
        );

        FlaskResponse flaskResponse = response.getBody();

        return CustomApiResponse.success(flaskResponse, 200, "프롬프트 전송 성공");
    }

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
                "http://3.37.172.79:80/category/predict",
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




    public CustomApiResponse<MorphemeResponse> receiveMorpheme(Long promptId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        String promptContent = prompt.getPromptContent();

        Map<String, String> request = new HashMap<>();
        request.put("promptContent", promptContent);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<MorphemeResponse> response = restTemplate.exchange(
                "http://3.37.172.79:80",
                HttpMethod.POST,
                entity,
                MorphemeResponse.class
        );

        MorphemeResponse flaskResponse = response.getBody();
        return CustomApiResponse.success(flaskResponse, 200, "형태소분석 수신 성공");
    }

    public List<SegmentResponse> sendResultImageToFlask(String finalImage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("resultImage", finalImage);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<List<SegmentResponse>> response = restTemplate.exchange(
                "https://0798-118-32-120-228.ngrok-free.app/sam",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<SegmentResponse>>() {}
        );

        return response.getBody();
    }

}
