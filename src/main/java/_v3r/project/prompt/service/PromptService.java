package _v3r.project.prompt.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.common.s3.MultipartInputStreamFileResource;
import _v3r.project.common.s3.S3Service;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.request.InpaintingImageRequest;
import _v3r.project.prompt.dto.response.ImageResponse;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
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
import org.springframework.web.bind.annotation.CrossOrigin;
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

    @Transactional
    public ImageResponse generateFishImage(Long userId, Long chatId, Paints paints,
            String promptContent) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        if (Boolean.TRUE.equals(chat.getIsFinished())) {
            throw new EverException(ErrorCode.ALREADY_FINISHED);
        }

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

        return new ImageResponse(prompt.getPromptId(), List.of(new ImageResponse.ImageData(s3ImageUrl)));
    }

    @Transactional
    public ImageResponse generateMountainImage(Long userId, Long chatId, Paints paints,
            String promptContent) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        if (Boolean.TRUE.equals(chat.getIsFinished())) {
            throw new EverException(ErrorCode.ALREADY_FINISHED);
        }

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

        return new ImageResponse(prompt.getPromptId(), List.of(new ImageResponse.ImageData(s3ImageUrl)));
    }
    @Transactional
    public ImageResponse generatePeopleImage(Long userId, Long chatId, Paints paints,
            String promptContent) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        if (Boolean.TRUE.equals(chat.getIsFinished())) {
            throw new EverException(ErrorCode.ALREADY_FINISHED);
        }

        Prompt prompt = sendAndSavePrompt(userId, chatId, promptContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String styledPrompt = "A traditional Korean Buddhist painting (Shinjung Taenghwa) enshrined on the left and right walls of the temple's central hall. "
                + "It features a vivid fusion of native folk deities and Buddhist guardian gods, showcasing a uniquely Korean character blended with elements of folk belief. "
                + "The painting expands from 39 to 104 deities, reflecting a diversification of spiritual functions. "
                + "There are four compositional types: "
                + "1) Dominated by the Great Vajra Deity (Dae-yejeok Geumgangsin), occupying one-third of the canvas with Jeseokcheon to the left, Daebeomcheon to the right, and Dongjin Bosal below; surrounded by star lords, wrathful kings, and celestial maidens. "
                + "2) Focused on Jeseokcheon, Daebeomcheon, and Dongjin Bosal, arranged in dual layers of heavenly and guardian figures. "
                + "3) Centered around Jeseokcheon with all deities surrounding him, including both unarmed bodhisattvas and armored warrior deities. "
                + "4) Centered on Dongjin Bosal with only guardian figures like the Eight Generals and Twelve Zodiac Generals. "
                + "The painting reflects a three-tier cosmology of heaven, earth, and the underworld, and uses vivid, traditional Buddhist art style. "
                + promptContent;


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

        String s3ImageUrl = s3Service.uploadImageFromUrl(ImageUrl, "people-paint", ".png", userId,
                chatId);

        prompt.updateImageUrl(s3ImageUrl);
        prompt.updateImage(false);
        promptRepository.save(prompt);

        return new ImageResponse(prompt.getPromptId(), List.of(new ImageResponse.ImageData(s3ImageUrl)));
    }

    @Transactional
    public ImageResponse generateInpaintingImage(
            Long userId, InpaintingImageRequest request) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(request.chatId())
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        if (Boolean.TRUE.equals(chat.getIsFinished())) {
            throw new EverException(ErrorCode.ALREADY_FINISHED);
        }


        Prompt prompt = sendAndSavePrompt(userId, request.chatId(), request.promptContent());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        Paints paints = chat.getPaints();
        String styledPrompt = null;
        if (paints == Paints.어해도) {
            styledPrompt = "Generate an artwork in the 어해도 (Korean Minhwa) style. "
                    + "A traditional Korean Minhwa-style painting featuring seven freshwater fish of various sizes (carp, catfish, trout) arranged in a vertical, "
                    + "flat composition with no perspective. The fish are calmly floating in pale water among soft, ink-brushed aquatic plants. On the left side,"
                    + " a large natural rock is depicted with red flowers (resembling Chinese lantern flowers or bleeding hearts) blooming from its cracks."
                    + " The painting uses soft, muted traditional colors like grey, brown, and pale yellow for the fish, with a light grayish background. "
                    + "A splash of red from the flowers adds contrast. "
                    + "The brushwork is delicate, the lines are sharp without any ink bleeding, evoking a serene, still atmosphere where nature and life coexist harmoniously in stillness. "
                    + "The painting features vivid, saturated traditional " + request.promptContent();
        } else if (paints == Paints.산수도) {
            styledPrompt =
                    "Generate an artwork in the style of traditional Korean ink landscape painting (산수화, 山水畫). "
                            + "The scene should include: " + request.promptContent() + ". "
                            + "Use ink wash techniques only, with soft gradients of black, gray, and a warm yellowed paper texture to simulate aged hanji.";

        } else if (paints == Paints.탱화) {
             styledPrompt = "A traditional Korean Buddhist painting (Shinjung Taenghwa) enshrined on the left and right walls of the temple's central hall. "
                    + "It features a vivid fusion of native folk deities and Buddhist guardian gods, showcasing a uniquely Korean character blended with elements of folk belief. "
                    + "The painting expands from 39 to 104 deities, reflecting a diversification of spiritual functions. "
                    + "There are four compositional types: "
                    + "1) Dominated by the Great Vajra Deity (Dae-yejeok Geumgangsin), occupying one-third of the canvas with Jeseokcheon to the left, Daebeomcheon to the right, and Dongjin Bosal below; surrounded by star lords, wrathful kings, and celestial maidens. "
                    + "2) Focused on Jeseokcheon, Daebeomcheon, and Dongjin Bosal, arranged in dual layers of heavenly and guardian figures. "
                    + "3) Centered around Jeseokcheon with all deities surrounding him, including both unarmed bodhisattvas and armored warrior deities. "
                    + "4) Centered on Dongjin Bosal with only guardian figures like the Eight Generals and Twelve Zodiac Generals. "
                    + "The painting reflects a three-tier cosmology of heaven, earth, and the underworld, and uses vivid, traditional Buddhist art style. "
                    + request.promptContent();
        } else {
            throw new EverException(ErrorCode.BAD_REQUEST);
        }

        byte[] decodedMask = Base64.getDecoder().decode(request.maskFile());
        ByteArrayInputStream maskInputStream = new ByteArrayInputStream(decodedMask);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("prompt",styledPrompt);
        body.add("image", new MultipartInputStreamFileResource(new URL(request.imageFileUrl()).openStream(), "image.png"));
        body.add("mask", new MultipartInputStreamFileResource(maskInputStream, "mask.png"));
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

        String resultUrl = response.getBody().get("data").get(0).get("url").asText();

        String resultS3Url = s3Service.uploadImageFromUrl(resultUrl, "inpainting/result", ".png", userId, request.chatId());

        prompt.updateImageUrl(resultS3Url);
        prompt.updateImage(true);
        promptRepository.save(prompt);

        return new ImageResponse(prompt.getPromptId(), List.of(new ImageResponse.ImageData(resultS3Url)));
    }
}