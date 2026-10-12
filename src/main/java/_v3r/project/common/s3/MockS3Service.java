package _v3r.project.common.s3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/** 부하 테스트용(profile=mock): S3 호출을 mock.latency.s3-ms 만큼 지연시킨 뒤 가짜 결과를 반환한다. */
@Service
@Profile("mock")
public class MockS3Service implements S3Service {

    @Value("${mock.latency.s3-ms:300}")
    private long latencyMs;

    private void delay() {
        try {
            Thread.sleep(latencyMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String url(String key) {
        return "https://mock-s3.local/" + key;
    }

    @Override
    public String uploadImageFromUrl(String imageUrl, String directory, String fileExtension, Long userId, Long chatId) {
        delay();
        return url("user-" + userId + "/chat-" + chatId + "/" + directory + "/mock" + fileExtension);
    }

    @Override
    public String generatePresignedUrl(String s3Key, int expirationInMinutes) {
        return url(s3Key) + "?mock-presigned";
    }

    @Override
    public File downloadMultipart(String prefix, String zipFileName) throws IOException {
        delay();
        return Files.createTempFile("mock-", "-" + zipFileName).toFile();
    }

    @Override
    public String uploadImageFromBase64(String base64Image, String directory, Long userId, Long chatId, String fileName) {
        delay();
        return url("user-" + userId + "/chat-" + chatId + "/result-image/" + directory + "/" + fileName);
    }

    @Override
    public String uploadJson(String jsonContent, String directory, Long userId, Long chatId, String fileName) {
        delay();
        return url("user-" + userId + "/chat-" + chatId + "/result-json/" + directory + "/" + fileName);
    }

    @Override
    public File downloadImageFile(String s3Key) {
        delay();
        try {
            return Files.createTempFile("mock-", ".png").toFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void delete(String s3Url) {
        delay();
    }
}
