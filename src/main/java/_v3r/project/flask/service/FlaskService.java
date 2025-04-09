package _v3r.project.flask.service;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.prompt.dto.PromptRequest;
import _v3r.project.flask.dto.FlaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FlaskService {

    private final RestTemplate restTemplate;

    public CustomApiResponse<FlaskResponse> sendPromptToFlask(PromptRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PromptRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<FlaskResponse> response = restTemplate.exchange(
                "http://localhost:5000/prompt",
                HttpMethod.POST,
                entity,
                FlaskResponse.class
        );
        FlaskResponse flaskResponse = response.getBody();

        return CustomApiResponse.success(flaskResponse,200,"프롬프트 전송 성공");
    }

}
