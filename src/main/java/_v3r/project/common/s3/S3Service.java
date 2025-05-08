package _v3r.project.common.s3;


public interface S3Service {
    String uploadImageFromUrl(String imageUrl, String directory, String fileExtension,Long userId);
    boolean delete(String fileUrl);
}
