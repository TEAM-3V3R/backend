package _v3r.project.common.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

/**
 * 부하 테스트용(profile=mock): OpenAI·Flask 호출을 실제 네트워크 없이 지정한 시간만큼 지연시킨 뒤 고정 응답으로 대체한다.
 * 지연 시간은 실측값에 맞춰 mock.latency.* 로 조정한다.
 */
@Configuration
@Profile("mock")
public class MockExternalApiConfig {

    // 1x1 PNG
    private static final String PNG_B64 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";

    @Bean
    public RestTemplate restTemplate(
            @Value("${mock.latency.openai-ms:2500}") long openaiMs,
            @Value("${mock.latency.flask-ms:5000}") long flaskMs) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            String path = request.getURI().getPath();
            boolean openai = path.contains("/images/generations");
            try {
                Thread.sleep(openai ? openaiMs : flaskMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return json(responseFor(path));
        });
        return restTemplate;
    }

    private static String responseFor(String path) {
        if (path.contains("/images/generations")) {
            return "{\"data\":[{\"b64_json\":\"" + PNG_B64 + "\"}]}";
        }
        if (path.contains("/category/predict")) {
            return "{\"results\":[{\"text\":\"mock\",\"classification\":\"mock\"}]}";
        }
        if (path.contains("/sam")) {
            return "[{\"uuid\":\"mock-1\",\"base64Image\":\"" + PNG_B64 + "\"},"
                    + "{\"uuid\":\"mock-2\",\"base64Image\":\"" + PNG_B64 + "\"}]";
        }
        if (path.contains("/analyzer/analyze")) {
            return "{\"fluency\":1.0,\"persistence\":1.0,\"creativity\":1.0,"
                    + "\"fluencySkc\":[1.0,1.0,1.0],\"persistenceSrf\":[1.0,1.0,1.0]}";
        }
        throw new IllegalStateException("mock 응답이 정의되지 않은 경로: " + path);
    }

    private static ClientHttpResponse json(String body) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ClientHttpResponse() {
            public HttpStatusCode getStatusCode() { return HttpStatus.OK; }
            public String getStatusText() { return "OK"; }
            public void close() {}
            public InputStream getBody() { return new ByteArrayInputStream(bytes); }
            public HttpHeaders getHeaders() { return headers; }
        };
    }
}
