package _v3r.project.prompt.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.response.ChatResponse;
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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RestTemplate restTemplate;
    private final PromptService promptService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final PromptRepository promptRepository;

    @Value("${chatgpt.api-key}")
    private String apiKey;

    public String getChatResponse(Long userId,Long chatId,String promptContent) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        promptService.sendAndSavePrompt(userId,chatId,promptContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo-instruct",
                "prompt", promptContent,
                "max_tokens", 1000
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/completions",
                request,
                ChatResponse.class
        );

        return response.getBody().choices().get(0).text().trim();
    }
    @Transactional
    public CreateChatResponse createChat(Long userId,Paints paints) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat newChat = Chat.toEntity(paints);
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

                    return new FindAllChatResponse(chatRoom.getId(),chatRoom.getIsFinished(),
                            prompt != null ? prompt.getPromptContent() : null);
                })
                .toList();
    }
    @Transactional(readOnly = true)
    public FindChatResponse findChat(Long userId,Long chatId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Chat chat = chatRepository.findByUserIdAndId(userId, chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        List<Prompt> prompts = promptRepository.findAllByChatIdOrderByCreatedAtAsc(chatId);

        List<FindChatResponse.PromptItem> promptItems = prompts.stream()
                .map(p -> new FindChatResponse.PromptItem(p.getInpaintingImage(),p.getPromptContent(), p.getImageUrl()))
                .toList();

        return new FindChatResponse(chat.getId(),chat.getIsFinished() ,chat.getPaints(), promptItems);
    }

    @Transactional
    public void finishChat(Long userId, Long chatId) {
        Chat chat = chatRepository.findByUserIdAndId(userId, chatId)
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
        Chat chat = chatRepository.findByUserIdAndId(userId, chatId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        chat.updateChatTitle(chatTitle);

        return UpdateChatTitleResponse.of(chatId,chatTitle);
    }


}
