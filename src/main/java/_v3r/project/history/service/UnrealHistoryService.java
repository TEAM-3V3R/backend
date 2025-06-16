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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnrealHistoryService {

    private final UnrealHistoryRepository unrealHistoryRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

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
    public List<UnrealHistoryResponse> showUnrealHistory(Long userId, Long chatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findByUserIdAndId(userId,chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<UnrealHistory> historyList = unrealHistoryRepository
                .findAllByUserIdAndChat_IdOrderByTimestampAsc(userId, chatId);

        return historyList.stream()
                .map(UnrealHistoryResponse::of)
                .toList();
    }


}
