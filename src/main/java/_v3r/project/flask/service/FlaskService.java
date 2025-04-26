package _v3r.project.flask.service;

import _v3r.project.category.dto.response.ReceiveCategoryResonse;
import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.morpheme.dto.response.MorphemeResponse;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.dto.request.PromptRequest;
import _v3r.project.flask.dto.FlaskResponse;
import _v3r.project.prompt.repository.PromptRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FlaskService {

    private final RestTemplate restTemplate;
    private final PromptRepository promptRepository;

    public CustomApiResponse<FlaskResponse> sendPromptToFlask(PromptRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PromptRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<FlaskResponse> response = restTemplate.exchange(
                "https://fd62-115-139-95-92.ngrok-free.app/prompt",//TODO 후에 배포된 url로 경로 지정 예정
                HttpMethod.POST,
                entity,
                FlaskResponse.class
        );
        FlaskResponse flaskResponse = response.getBody();

        return CustomApiResponse.success(flaskResponse,200,"프롬프트 전송 성공");
    }

    public CustomApiResponse<ReceiveCategoryResonse> receiveCategory(Long promptId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        String promptContent = prompt.getPromptContent();

        Map<String, String> request = new HashMap<>();
        request.put("promptContent", promptContent);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ReceiveCategoryResonse> response = restTemplate.exchange(
                "https://fd62-115-139-95-92.ngrok-free.app/category",
                HttpMethod.POST,
                entity,
                ReceiveCategoryResonse.class
        );

        ReceiveCategoryResonse flaskResponse = response.getBody();

        return CustomApiResponse.success(flaskResponse, 200, "카테고리 수신 성공");
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
                "https://18d4-115-139-95-92.ngrok-free.app/morpheme",
                HttpMethod.POST,
                entity,
                MorphemeResponse.class
        );

        MorphemeResponse flaskResponse = response.getBody();
        return CustomApiResponse.success(flaskResponse, 200, "형태소분석, 동음이의어 여부 수신 성공");
    }


}
