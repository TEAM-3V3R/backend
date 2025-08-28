package _v3r.project.common.s3;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {

    //TODO try-catch문 정리 필요

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String uploadImageFromUrl(String imageUrl, String directory, String fileExtension,
            Long userId, Long chatId) {
        try (var inputStream = new java.net.URL(imageUrl).openStream()) {
            String fileName = "user-" + userId + "/" + "chat-" + chatId + "/" + directory + "/"
                    + UUID.randomUUID() + fileExtension;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/" + fileExtension.replace(".", ""));

            amazonS3.putObject(bucket, fileName, inputStream, metadata);

            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new EverException(ErrorCode.BAD_REQUEST);
        }
    }


    @Override
    public File downloadMultipart(String prefix, String zipFileName) throws IOException {
        List<com.amazonaws.services.s3.model.S3ObjectSummary> objectSummaries =
                amazonS3.listObjectsV2(bucket, prefix).getObjectSummaries();

        if (objectSummaries.isEmpty()) {
            throw new EverException(ErrorCode.ENTITY_NOT_FOUND);
        }

        Path tempDir = Files.createTempDirectory("download-zip");
        Path zipPath = tempDir.resolve(zipFileName);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (com.amazonaws.services.s3.model.S3ObjectSummary summary : objectSummaries) {
                String key = summary.getKey();

                try (S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, key));
                        InputStream inputStream = s3Object.getObjectContent()) {

                    String fileName = key.replaceFirst(prefix, "");
                    zipOutputStream.putNextEntry(new ZipEntry(fileName));
                    inputStream.transferTo(zipOutputStream);
                    zipOutputStream.closeEntry();
                }
            }
        }

        return zipPath.toFile();
    }
    @Override
    public String uploadImageFromBase64(String base64Image, String directory, Long userId, Long chatId, String fileName) {
        try {
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Image);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(decodedBytes.length);
            metadata.setContentType("image/png");

            String key = "user-" + userId + "/chat-" + chatId + "/" + "result-image"+"/"+directory + "/" + fileName;

            try (InputStream inputStream = new java.io.ByteArrayInputStream(decodedBytes)) {
                amazonS3.putObject(bucket, key, inputStream, metadata);
            }

            return amazonS3.getUrl(bucket, key).toString();

        } catch (Exception e) {
            throw new EverException(ErrorCode.BAD_REQUEST);
        }
    }
    @Override
    public String generatePresignedUrl(String key, int expireMinutes) {
        var expiration = new java.util.Date(System.currentTimeMillis() + 1000L * 60 * expireMinutes);
        var req = new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        return amazonS3.generatePresignedUrl(req).toString();
    }
    @Override
    public String uploadJson(String jsonContent, String directory, Long userId, Long chatId, String fileName) {
        try {
            byte[] contentAsBytes = jsonContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentAsBytes.length);
            metadata.setContentType("application/json");

            String key = "user-" + userId + "/chat-" + chatId + "/result-json/"
                    + directory + "/" + fileName;

            try (InputStream inputStream = new java.io.ByteArrayInputStream(contentAsBytes)) {
                amazonS3.putObject(bucket, key, inputStream, metadata);
            }

            return generatePresignedUrl(key, 20);

        } catch (Exception e) {
            throw new EverException(ErrorCode.BAD_REQUEST);
        }
    }

    //TODO 코드 최적화 필요
    @Override
    public File downloadImageFile(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            throw new EverException(ErrorCode.ENTITY_NOT_FOUND);
        }
        String key = extractKeyFromUrl(s3Key);

        try (S3Object s3Object = amazonS3.getObject(bucket, key);
                InputStream inputStream = s3Object.getObjectContent()) {

            Path tempFile = Files.createTempFile("s3-", "-" + key.replaceAll("/", "_"));
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile.toFile();

        } catch (IOException | AmazonS3Exception e) {
            throw new EverException(ErrorCode.S3_DOWNLOAD_FAILED);
        }
    }

    private String extractKeyFromUrl(String s3Url) {
        if (s3Url == null || s3Url.isBlank()) {
            return null;
        }
        try {
            URI uri = new URI(s3Url);
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        } catch (URISyntaxException e) {
            return s3Url;
        }
    }

}
