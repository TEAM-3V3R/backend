package _v3r.project.prompt.dto.request;


public record InpaintingImageRequest(
        Long chatId,
        String promptContent,
        String imageFileUrl,
        String maskFile
) {}

