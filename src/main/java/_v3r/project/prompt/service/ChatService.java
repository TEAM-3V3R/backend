package _v3r.project.prompt.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.response.CreateChatResponse;
import _v3r.project.prompt.dto.response.FindAllChatResponse;
import _v3r.project.prompt.dto.response.FindChatResponse;
import _v3r.project.prompt.dto.response.UpdateChatTitleResponse;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final PromptRepository promptRepository;

    @Value("${chatgpt.api-key}")
    private String apiKey;

    @Transactional
    public CreateChatResponse createChat(Long userId,Paints paints) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat newChat = Chat.toEntity(user,paints);
        newChat.updateChat(false);
        chatRepository.save(newChat);
        return CreateChatResponse.of(newChat);
    }

    @Transactional(readOnly = true)
    public List<FindAllChatResponse> findAllChats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<Chat> chats = chatRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return chats.stream()
                .map(chatRoom -> {
                    Prompt prompt = promptRepository.findFirstByChatIdOrderByCreatedAtAsc(chatRoom.getId())
                            .orElse(null);

                    return new FindAllChatResponse(
                            chatRoom.getId(),
                            chatRoom.getChatTitle(),
                            chatRoom.getIsFinished(),
                            prompt != null ? prompt.getPromptContent() : null
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public FindChatResponse findChat(Long userId,Long chatId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<Prompt> prompts = promptRepository.findAllByChatIdOrderByCreatedAtAsc(chatId);

        List<FindChatResponse.PromptItem> promptItems = prompts.stream()
                .map(p -> new FindChatResponse.PromptItem(p.getInpaintingImage(),p.getPromptContent(), p.getImageUrl()))
                .toList();

        return new FindChatResponse(chat.getId(),chat.getIsFinished() ,chat.getPaints(), promptItems);
    }

    @Transactional
    public void finishChat(Long userId, Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        if (Boolean.TRUE.equals(chat.getIsFinished())) {
            throw new EverException(ErrorCode.ALREADY_FINISHED);
        }

        chat.updateChat(true);
        chatRepository.save(chat);
    }
    @Transactional
    public UpdateChatTitleResponse updateChat(Long userId, Long chatId, String chatTitle) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        chat.updateChatTitle(chatTitle);

        return UpdateChatTitleResponse.of(chatId,chatTitle);
    }


}
