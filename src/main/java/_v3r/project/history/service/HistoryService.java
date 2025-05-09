package _v3r.project.history.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.history.dto.AllHistoryResponse;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final PromptRepository promptRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public List<AllHistoryResponse> findAllHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<Chat> chatList = chatRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return chatList.stream()
                .map(chat -> {
                    Prompt firstPrompt = promptRepository.findFirstByChatIdOrderByCreatedAtAsc(chat.getId())
                            .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

                    Prompt lastPrompt = promptRepository.findFirstByChatIdOrderByCreatedAtDesc(chat.getId())
                            .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

                    return new AllHistoryResponse(
                            chat.getId(),
                            chat.getCreatedAt(),
                            chat.getPaints(),
                            lastPrompt.getImageUrl(),
                            firstPrompt.getPromptContent()
                    );
                })
                .toList();
    }
}
