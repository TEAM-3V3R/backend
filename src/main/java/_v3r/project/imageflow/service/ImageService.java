package _v3r.project.imageflow.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final FlaskService flaskService;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Transactional
    //TODO flask로 이미지 결과 보내고 response로 요소분리 결과 받아야해서 일단 보내는 로직만 추가함 -> 응답받아서 response로 바꿔서 s3저장로직 해야함

    public void sendResultImage(Long userId,Long chatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        String resultImage = promptRepository.findLastResultImageUrlByChatId(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        flaskService.sendResultImageToFlask(resultImage);

    }
}
