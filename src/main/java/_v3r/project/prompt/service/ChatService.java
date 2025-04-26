package _v3r.project.prompt.service;

import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.response.ChatResponse;
import _v3r.project.prompt.dto.response.ImageResponse;
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

    public String getChatResponse(String promptContent) {
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

    //TODO 어해도 인지 검증 필요
    public ImageResponse generateFishImage(Paints paints,String promptContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String styledPrompt = "Generate an artwork in the 어해도 (Korean Minhwa) style. "
                + "A traditional Korean Minhwa-style painting featuring seven freshwater fish of various sizes (carp, catfish, trout) arranged in a vertical, "
                + "flat composition with no perspective. The fish are calmly floating in pale water among soft, ink-brushed aquatic plants. On the left side,"
                + " a large natural rock is depicted with red flowers (resembling Chinese lantern flowers or bleeding hearts) blooming from its cracks."
                + " The painting uses soft, muted traditional colors like grey, brown, and pale yellow for the fish, with a light grayish background. "
                + "A splash of red from the flowers adds contrast."
                + " The brushwork is delicate, the lines are sharp without any ink bleeding, evoking a serene, still atmosphere where nature and life coexist harmoniously in stillness. "
                + "The painting features vivid, saturated traditional" + promptContent;

        Map<String, Object> body = Map.of(
                "model","dall-e-3",
                "prompt", styledPrompt,
                "n", 1,
                "size", "1024x1024"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<ImageResponse> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/images/generations",
                request,
                ImageResponse.class
        );

        return response.getBody();
    }

    public ImageResponse generateMountainImage(Paints paints,String promptContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String styledPrompt = "Generate an artwork in the style of traditional Korean ink landscape painting (산수화, 山水畫). "
                + "The scene should include: " + promptContent + ". "
                + "Use ink wash techniques only, with soft gradients of black, gray, and a warm yellowed paper texture to simulate aged hanji.";



        Map<String, Object> body = Map.of(
                "model","dall-e-3",
                "prompt", styledPrompt,
                "n", 1,
                "size", "1024x1024"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<ImageResponse> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/images/generations",
                request,
                ImageResponse.class
        );

        return response.getBody();
    }

}
