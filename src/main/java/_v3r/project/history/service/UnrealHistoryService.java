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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnrealHistoryService {

    private final UnrealHistoryRepository unrealHistoryRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;


    @Cacheable(cacheNames = "optionLists")
    @Transactional
    public void saveAll(Long userId, int chatId, List<UnrealHistoryResponse> requestList) {
        Long longUserId = userId;
        Long longChatId = (long) chatId;

        User user = userRepository.findById(longUserId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findByUser_UserIdAndChatId(longUserId, longChatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<UnrealHistory> histories = requestList.stream()
                .map(r -> r.toEntity(user, chat))
                .toList();

        unrealHistoryRepository.saveAll(histories);
    }



    @Cacheable(cacheNames = "optionLists")
    @Transactional(readOnly = true)
    public List<UnrealHistoryResponse> showUnrealHistory(Long userId, Long chatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findByUser_UserIdAndChatId(userId,chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<UnrealHistory> historyList = unrealHistoryRepository
                .findAllByUser_UserIdAndChat_ChatIdOrderByTimestampAsc(userId, chatId);

        return historyList.stream()
                .map(UnrealHistoryResponse::of)
                .toList();
    }


}
