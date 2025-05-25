package _v3r.project.prompt.dto.response;

import java.util.List;

public record ChatResponse(
        String id,
        String object,
        long created,
        String model,
        List<Choice> choices
) {
    public record Choice(
            String text,
            int index,
            String finish_reason
    ) {}
}
