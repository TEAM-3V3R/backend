package _v3r.project.common.s3;


import java.io.File;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadImageFromUrl(String imageUrl, String directory, String fileExtension,Long userId,Long chatId);

    String uploadMultipartFile(MultipartFile file, String directory, Long userId,Long chatId);

    File downloadMultipart(String prefix, String zipFileName) throws IOException;
    String uploadImageFromBase64(String base64Image, String directory, Long userId, Long chatId, String fileName);
    String uploadJson(String jsonContent, String directory, Long userId, Long chatId, String fileName);
    File downloadImageFile(String s3Key);

}
