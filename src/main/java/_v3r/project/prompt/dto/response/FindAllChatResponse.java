package _v3r.project.prompt.dto.response;

public record FindAllChatResponse(
        Long chatId,
        String chatTitle,
        Boolean isFinished,
        String promptContent
) {

}
