package _v3r.project.common.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadFile(MultipartFile multipartFile, String url);
    boolean delete(String fileUrl);
}
