package _v3r.project.common.s3;


import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadImageFromUrl(String imageUrl, String directory, String fileExtension,Long userId,Long chatId);

    String uploadMultipartFile(MultipartFile file, String directory, Long userId,Long chatId);
}
