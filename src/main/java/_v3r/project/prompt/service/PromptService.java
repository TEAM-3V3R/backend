package _v3r.project.prompt.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.dto.response.PromptResponse;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromptService {
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final FlaskService flaskService;

    @Transactional
    public PromptResponse sendAndSavePrompt(Long userId, String promptContent) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        flaskService.sendPromptToFlask(promptContent);

        Prompt prompt = Prompt.create(user, promptContent);

        promptRepository.save(prompt);

        return PromptResponse.of(prompt);
    }
}
