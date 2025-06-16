package _v3r.project.history.service;

import _v3r.project.category.domain.Category;
import _v3r.project.category.repository.CategoryRepository;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.history.domain.enumType.SortType;
import _v3r.project.history.dto.AllHistoryResponse;
import _v3r.project.history.dto.DetailHistoryResponse;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.prompt.spec.ChatSpecifications;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final PromptRepository promptRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<AllHistoryResponse> findHistory(Long userId, Paints paints, SortType sortType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Specification<Chat> spec = Specification.where(ChatSpecifications.hasUserId(userId));
        if (paints != null) {
            spec = spec.and(ChatSpecifications.hasPaints(paints));
        }

        Sort sort = (sortType == SortType.과거순)
                ? Sort.by(Sort.Direction.ASC, "createdAt")
                : Sort.by(Sort.Direction.DESC, "createdAt");

        List<Chat> chatList = chatRepository.findAll(spec, sort);

        return chatList.stream()
                .map(chat -> {

                    Prompt lastPrompt = promptRepository.findFirstByChatIdOrderByCreatedAtDesc(chat.getId())
                            .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

                    return new AllHistoryResponse(
                            chat.getId(),
                            chat.getChatTitle(),
                            chat.getCreatedAt(),
                            chat.getPaints(),
                            lastPrompt.getImageUrl()
                    );
                })
                .toList();
    }
    @Transactional(readOnly = true)
    public DetailHistoryResponse detailFindHistory(Long userId, Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<Prompt> prompts = promptRepository.findAllByChatIdOrderByCreatedAtAsc(chatId);
        if (prompts.isEmpty()) throw new EverException(ErrorCode.ENTITY_NOT_FOUND);

        List<DetailHistoryResponse.PromptHistory> promptHistories = prompts.stream()
                .map(prompt -> {
                    List<Category> categories = categoryRepository.findAllByPromptId(prompt.getId());

                    List<String> classifications = categories.stream()
                            .map(Category::getClassification)
                            .toList();

                    return new DetailHistoryResponse.PromptHistory(
                            prompt.getCreatedAt(),
                            prompt.getPromptContent(),
                            prompt.getImageUrl(),
                            classifications
                    );
                })
                .toList();

        Prompt lastPrompt = prompts.get(prompts.size() - 1);
        String lastImageUrl = lastPrompt.getImageUrl();
        Paints paints = chat.getPaints();

        return new DetailHistoryResponse(chatId, paints, promptHistories, lastImageUrl);
    }

}
