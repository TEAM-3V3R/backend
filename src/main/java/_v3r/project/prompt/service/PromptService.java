package _v3r.project.prompt.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.common.s3.S3Service;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.response.ImageResponse;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;
    private final RestTemplate restTemplate;
    private final ChatRepository chatRepository;
    private final S3Service s3Service;

    @Value("${chatgpt.api-key}")
    private String apiKey;
    // TODO: 외부 API 호출 분리 (비동기 or 트랜잭션 외부)
    @Transactional
    public ImageResponse generateImage(Long userId, Long chatId, Paints paints,
            String promptContent) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        if (!chat.getUser().getUserId().equals(userId)) {
            throw new EverException(ErrorCode.FORBIDDEN);
        }
        if (chat.getPaints() != paints) {
            throw new EverException(ErrorCode.BAD_REQUEST);
        }

        if (Boolean.TRUE.equals(chat.getIsFinished())) {
            throw new EverException(ErrorCode.ALREADY_FINISHED);
        }

        Prompt prompt = Prompt.toEntity(chat.getUser(), promptContent, chat);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String styledPrompt = paints.getTemplate().build(promptContent);

        Map<String, Object> body = Map.of(
                "model", "gpt-image-1",
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

        String base64Image = response.getBody().data().get(0).base64();

        String fileName = UUID.randomUUID() + ".png";

        String s3ImageUrl = s3Service.uploadImageFromBase64(
                base64Image,
                paints.getS3Directory(),
                userId,
                chatId,
                fileName
        );

        prompt.updateImageUrl(s3ImageUrl);
        prompt.updateImage(false);
        promptRepository.save(prompt);

        return new ImageResponse(prompt.getPromptId(), List.of(new ImageResponse.ImageData(s3ImageUrl)));
    }
      }