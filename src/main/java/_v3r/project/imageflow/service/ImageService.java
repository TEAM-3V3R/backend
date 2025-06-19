package _v3r.project.imageflow.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.common.s3.S3Service;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final FlaskService flaskService;
    private final S3Service s3Service;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public File segmentResultImage(Long userId, Long chatId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        String resultImage = promptRepository.findLastResultImageUrlByChatIdNative(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<SegmentResponse> segmentListFromFlask = flaskService.sendResultImageToFlask(resultImage);

        List<SegmentResponse> segmentListWithChatId = segmentListFromFlask.stream()
                .map(segment -> new SegmentResponse(chatId, segment.uuid(), segment.base64Image()))
                .toList();

        for (SegmentResponse segment : segmentListWithChatId) {
            String uuid = segment.uuid();
            String base64Image = segment.base64Image();
            s3Service.uploadImageFromBase64(base64Image, "segments-image", userId, chatId, uuid + ".png");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(segmentListWithChatId);
            s3Service.uploadJson(jsonContent, "data-list", userId, chatId, "segments.json");

        } catch (JsonProcessingException e) {
            throw new EverException(ErrorCode.JSON_PROCESSING_ERROR);
        }

        String prefix = "user-" + userId + "/chat-" + chatId + "/result-image/";
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

        return s3Service.downloadImageFile(s3Key);
    }



}
