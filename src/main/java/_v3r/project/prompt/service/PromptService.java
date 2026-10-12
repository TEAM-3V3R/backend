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
import _v3r.project.user.repository.UserRepository;
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
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;
    private final RestTemplate restTemplate;
    private final ChatRepository chatRepository;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final TransactionTemplate transactionTemplate;

    @Value("${chatgpt.api-key}")
    private String apiKey;
    /**
     * 트랜잭션 분리: DB 조회·검증(트랜잭션) → OpenAI 호출·S3 업로드(트랜잭션 밖) → DB 저장(트랜잭션).
     * 외부 응답을 기다리는 동안 DB 커넥션을 점유하지 않는다.
     */
    public ImageResponse generateImage(Long userId, Long chatId, Paints paints,
            String promptContent) {

        transactionTemplate.executeWithoutResult(status -> {
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
        });

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

        try {
            // ponytail: 검증~저장 사이에 채팅방 상태가 바뀌는 경합은 재검증하지 않음. 필요하면 저장 트랜잭션에서 isFinished 재확인.
            return transactionTemplate.execute(status -> {
                Prompt prompt = Prompt.toEntity(
                        userRepository.getReferenceById(userId),
                        promptContent,
                        chatRepository.getReferenceById(chatId));
                prompt.updateImageUrl(s3ImageUrl);
                prompt.updateImage(false);
                promptRepository.save(prompt);

                return new ImageResponse(prompt.getPromptId(), List.of(new ImageResponse.ImageData(s3ImageUrl)));
            });
        } catch (RuntimeException e) {
            // DB 저장 실패 시 S3 고아 파일 정리
            s3Service.delete(s3ImageUrl);
            throw e;
        }
    }
}
