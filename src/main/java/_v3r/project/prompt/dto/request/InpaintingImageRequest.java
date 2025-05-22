package _v3r.project.prompt.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record InpaintingImageRequest(
        Long chatId,
        String promptContent,
        MultipartFile imageFileUrl,
        MultipartFile maskFile
) {}

