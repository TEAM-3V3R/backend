package _v3r.project.common.s3;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    //TODO 유저/키워드 경로로 자동저장되도록 경로 고정시키기
    public String uploadImageFromUrl(String imageUrl, String directory, String fileExtension,Long userId) {
        try (var inputStream = new java.net.URL(imageUrl).openStream()) {
            String fileName = "user-" + userId + "/" + directory + "/" + UUID.randomUUID() + fileExtension;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/" + fileExtension.replace(".", ""));

            amazonS3.putObject(bucket, fileName, inputStream, metadata);

            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new EverException(ErrorCode.BAD_REQUEST);
        }
    }


    @Override
    public boolean delete(String fileUrl) {
        try {
            String[] temp = fileUrl.split(".com/");
            String fileKey = temp[1];
            amazonS3.deleteObject(bucket, fileKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
