package _v3r.project.history.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.history.domain.UnrealHistory;
import _v3r.project.history.dto.UnrealHistoryResponse;
import _v3r.project.history.repository.UnrealHistoryRepository;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnrealHistoryService {

    private UnrealHistoryRepository unrealHistoryRepository;
    private UserRepository userRepository;
    private ChatRepository chatRepository;

    @Transactional
    public void saveUnrealHistory(Long userId,Long chatId,UnrealHistoryResponse request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findByUserIdAndId(userId,chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        UnrealHistory history = request.toEntity(user,chat);
        unrealHistoryRepository.save(history);
    }


    @Transactional(readOnly = true)
    public UnrealHistoryResponse showUnrealHistory(Long userId, Long chatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findByUserIdAndId(userId,chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        UnrealHistory unrealHistory = unrealHistoryRepository.findByUserIdAndChat_Id(userId, chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        return UnrealHistoryResponse.of(unrealHistory);

    }

}
