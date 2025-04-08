package _v3r.project.prompt.service;

import _v3r.project.common.annotation.AuthUser;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.dto.PromptRequest;
import _v3r.project.prompt.dto.PromptResponse;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromptService {
    private final PromptRepository promptRepository;
    private final FlaskService flaskService;

    public PromptResponse sendPrompt(@AuthUser User user, PromptRequest request) {

        flaskService.sendPromptToFlask(request);

        Prompt prompt = request.toEntity(user);

        promptRepository.save(prompt);

        return PromptResponse.of(prompt);
    }
}
