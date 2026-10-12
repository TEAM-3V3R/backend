package _v3r.project.imageflow.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.common.s3.S3Service;
import _v3r.project.common.websocket.WsSessionRegistry;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.imageflow.dto.SegmentResponse;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final FlaskService flaskService;
    private final S3Service s3Service;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final WsSessionRegistry wsSessionRegistry;
    private final TransactionTemplate transactionTemplate;

    /**
     * 트랜잭션 분리: DB 조회(트랜잭션) → Flask 호출·S3 업로드(트랜잭션 밖) → DB 저장(트랜잭션).
     * 외부 응답을 기다리는 동안 DB 커넥션을 점유하지 않는다.
     */
    public File segmentResultImage(Long userId, Long chatId) {

        String resultImage = transactionTemplate.execute(status -> {
            userRepository.findById(userId)
                    .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

            chatRepository.findById(chatId)
                    .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

            return promptRepository.findLastResultImageUrlByChatIdNative(chatId)
                    .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));
        });

        List<SegmentResponse> segmentListFromFlask = flaskService.sendResultImageToFlask(resultImage);

        List<SegmentResponse> segmentListWithChatId = segmentListFromFlask.stream()
                .map(segment -> new SegmentResponse(userId, chatId, segment.uuid(), segment.base64Image()))
                .toList();

        List<String> uploadedUrls = new ArrayList<>();
        try {
            for (SegmentResponse segment : segmentListWithChatId) {
                uploadedUrls.add(s3Service.uploadImageFromBase64(
                        segment.base64Image(),
                        "segments-image", userId, chatId,
                        segment.uuid() + ".png"
                ));
            }

            String jsonContent;
            try {
                jsonContent = new ObjectMapper().writeValueAsString(segmentListWithChatId);
            } catch (JsonProcessingException e) {
                throw new EverException(ErrorCode.JSON_PROCESSING_ERROR);
            }
            uploadedUrls.add(s3Service.uploadJson(jsonContent, "data-list", userId, chatId, "segments.json"));

            transactionTemplate.executeWithoutResult(status -> {
                Chat chat = chatRepository.findById(chatId)
                        .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));
                chat.updateChat(true);
                chatRepository.save(chat);
            });
        } catch (RuntimeException e) {
            // DB 저장 실패 등으로 중단되면 이번 요청이 올린 S3 파일 정리
            uploadedUrls.forEach(s3Service::delete);
            throw e;
        }

        String presignedUrl = s3Service.generatePresignedUrl(
                "user-" + userId + "/chat-" + chatId + "/result-image/data-list/segments.json",
                20
        );
        String payload = """
        {
          "url": "%s"
        }
        """.formatted(presignedUrl);

        wsSessionRegistry.sendTo(payload);

        String prefix = "user-" + userId + "/chat-" + chatId;
        String zipFileName = "segments.zip";

        try {
            return s3Service.downloadMultipart(prefix, zipFileName);
        } catch (IOException e) {
            throw new EverException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }


    public File downloadResultImage(Long userId,Long chatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        String s3Key = promptRepository.findLastResultImageUrlByChatIdNative(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        chat.updateChat(true);
        chatRepository.save(chat);

        return s3Service.downloadImageFile(s3Key);
    }

}
