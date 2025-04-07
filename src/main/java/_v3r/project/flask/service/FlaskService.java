package _v3r.project.flask.service;

import java.nio.charset.Charset;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import _v3r.project.flask.dto.FlaskRequest;
import _v3r.project.flask.dto.FlaskResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FlaskService {
    //TODO RestTemplate 이용해 서버간 통신시작
    private final ObjectMapper objectMapper = new ObjectMapper();
    public FlaskResponse getAbstractive(FlaskRequest flaskRequest) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        String params2 = objectMapper.writeValueAsString(flaskRequest);

        HttpEntity entity = new HttpEntity(params2,httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:5000/test",
                HttpMethod.POST,entity, String.class);

        System.out.println(responseEntity.getStatusCode());
        System.out.println(responseEntity.getBody());

        FlaskResponse response = objectMapper.readValue(
                responseEntity.getBody(), FlaskResponse.class
        );
        return response;
    }
}
