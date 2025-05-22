package _v3r.project.prompt.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.common.s3.MultipartInputStreamFileResource;
import _v3r.project.common.s3.S3Service;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.response.ImageResponse;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final FlaskService flaskService;
    private final RestTemplate restTemplate;
    private final ChatRepository chatRepository;
    private final S3Service s3Service;

    @Value("${chatgpt.api-key}")
    private String apiKey;

    @Transactional
    public Prompt sendAndSavePrompt(Long userId, Long chatId, String promptContent) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        flaskService.sendPromptToFlask(promptContent);

        Prompt prompt = Prompt.create(user, promptContent, chat);

        promptRepository.save(prompt);

        return prompt;
    }

    //TODO 어해도 인지 검증 필요
    @Transactional
    public ImageResponse generateFishImage(Long userId, Long chatId, Paints paints,
            String promptContent) {
        Prompt prompt = sendAndSavePrompt(userId, chatId, promptContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String styledPrompt = "Generate an artwork in the 어해도 (Korean Minhwa) style. "
                + "A traditional Korean Minhwa-style painting featuring seven freshwater fish of various sizes (carp, catfish, trout) arranged in a vertical, "
                + "flat composition with no perspective. The fish are calmly floating in pale water among soft, ink-brushed aquatic plants. On the left side,"
                + " a large natural rock is depicted with red flowers (resembling Chinese lantern flowers or bleeding hearts) blooming from its cracks."
                + " The painting uses soft, muted traditional colors like grey, brown, and pale yellow for the fish, with a light grayish background. "
                + "A splash of red from the flowers adds contrast. "
                + "The brushwork is delicate, the lines are sharp without any ink bleeding, evoking a serene, still atmosphere where nature and life coexist harmoniously in stillness. "
                + "The painting features vivid, saturated traditional " + promptContent;

        Map<String, Object> body = Map.of(
                "model", "dall-e-3",
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

        String ImageUrl = response.getBody().data().get(0).url();

        String s3ImageUrl = s3Service.uploadImageFromUrl(ImageUrl, "fish-paint", ".png", userId,
                chatId);

        prompt.updateImageUrl(s3ImageUrl);
        prompt.updateImage(false);
        promptRepository.save(prompt);

        return new ImageResponse(prompt.getId(), List.of(new ImageResponse.ImageData(s3ImageUrl)));
    }

    @Transactional
    public ImageResponse generateMountainImage(Long userId, Long chatId, Paints paints,
            String promptContent) {
        Prompt prompt = sendAndSavePrompt(userId, chatId, promptContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String styledPrompt =
                "Generate an artwork in the style of traditional Korean ink landscape painting (산수화, 山水畫). "
                        + "The scene should include: " + promptContent + ". "
                        + "Use ink wash techniques only, with soft gradients of black, gray, and a warm yellowed paper texture to simulate aged hanji.";

        Map<String, Object> body = Map.of(
                "model", "dall-e-3",
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

        String imageUrl = response.getBody().data().get(0).url();
        String s3ImageUrl = s3Service.uploadImageFromUrl(imageUrl, "mountain-paint", ".png",
                userId, chatId);

        prompt.updateImageUrl(s3ImageUrl);
        prompt.updateImage(false);
        promptRepository.save(prompt);

        return new ImageResponse(prompt.getId(), List.of(new ImageResponse.ImageData(s3ImageUrl)));
    }

    @Transactional
    public ImageResponse generateInpaintingImage(
            Long userId,
            Long chatId,
            String promptContent,
            MultipartFile imageFile,
            MultipartFile maskFile
    ) throws IOException {
        Prompt prompt = sendAndSavePrompt(userId, chatId, promptContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        //TODO 프롬프트 맞게 스타일링 할지는 테스트 후 결정
        String styledPrompt = promptContent;

//        String styledPrompt =
//                "Edit the image to reflect traditional Korean ink landscape painting (산수화, 山水畫) style. "
//                        + "Scene: " + promptContent + ". "
//                        + "Use ink wash techniques, soft black gradients, and simulate aged hanji paper.";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("prompt", styledPrompt);
        body.add("image", new MultipartInputStreamFileResource(imageFile.getInputStream(),
                imageFile.getOriginalFilename()));
        body.add("mask", new MultipartInputStreamFileResource(maskFile.getInputStream(),
                maskFile.getOriginalFilename()));
        body.add("n", "1");
        body.add("size", "1024x1024");
        body.add("response_format", "url");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "https://api.openai.com/v1/images/edits",
                HttpMethod.POST,
                requestEntity,
                JsonNode.class
        );

        String generatedImageUrl = response.getBody().get("data").get(0).get("url").asText();
        String originalS3Url = s3Service.uploadMultipartFile(imageFile, "inpainting/original",
                userId, chatId);
        String maskS3Url = s3Service.uploadMultipartFile(maskFile, "inpainting/mask", userId,
                chatId);
        String resultS3Url = s3Service.uploadImageFromUrl(generatedImageUrl, "inpainting/result",
                ".png", userId, chatId);

        prompt.updateImageUrl(resultS3Url);
        prompt.updateImage(true);
        promptRepository.save(prompt);

        return new ImageResponse(prompt.getId(), List.of(new ImageResponse.ImageData(resultS3Url)));
    }

}
