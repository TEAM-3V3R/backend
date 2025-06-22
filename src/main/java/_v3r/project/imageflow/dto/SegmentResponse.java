package _v3r.project.imageflow.dto;

public record SegmentResponse(
        Long userId,
        Long chatId,
        String uuid,
        String base64Image) {

}
