package _v3r.project.flask.service;

import java.nio.charset.Charset;

import _v3r.project.flask.dto.FlaskRequest;
import _v3r.project.flask.dto.FlaskResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FlaskService {

//TODO RestTemplate 이용해 서버간 통신 시작
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public FlaskResponse getAbstractive(FlaskRequest flaskRequest) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        String params2 = objectMapper.writeValueAsString(flaskRequest);
        System.out.println(params2);
        HttpEntity<String> entity = new HttpEntity<>(params2, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:8080", // 내부 포트로 요청가면 바로 에러터짐 수정 필요
                HttpMethod.POST,
                entity,
                String.class
        );

        return objectMapper.readValue(responseEntity.getBody(), FlaskResponse.class);
    }
}
