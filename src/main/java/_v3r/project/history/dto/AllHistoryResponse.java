package _v3r.project.history.dto;

import _v3r.project.prompt.domain.enumtype.Paints;
import java.time.LocalDateTime;

public record AllHistoryResponse(
        Long chatId,
        String chatTitle,
        String promptContent,
        LocalDateTime createAt,
        Paints paints,
        String image_url
) {

}
