package _v3r.project.prompt.service;

import _v3r.project.common.annotation.AuthUser;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.dto.PromptRequest;
import _v3r.project.prompt.dto.PromptResponse;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromptService {
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final FlaskService flaskService;

    public PromptResponse sendPrompt(Long userId, PromptRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        flaskService.sendPromptToFlask(request);

        Prompt prompt = request.toEntity(user);

        promptRepository.save(prompt);

        return PromptResponse.of(prompt);
    }
}
