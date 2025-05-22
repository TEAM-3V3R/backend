package _v3r.project.prompt.dto.response;

public record FindAllChatResponse(
        Long chatId,
        Boolean isFinished,
        String promptContent
) {

}
