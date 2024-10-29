package org.richardstallman.dvback.client.s3.service;

import jakarta.annotation.Nullable;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  private final String baseFilePath = "files/";
  private final Duration urlDuration = Duration.ofMinutes(10);

  @Override
  public String createPreSignedURL(
      String fileName, Long interviewId, @Nullable Map<String, String> metadata) {
    String filePathKey = makeS3FilePath(fileName, interviewId);

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(filePathKey).build();

    PresignedPutObjectRequest presignedPutObjectRequest =
        s3Presigner.presignPutObject(
            builder -> builder.signatureDuration(urlDuration).putObjectRequest(putObjectRequest));

    return presignedPutObjectRequest.url().toString();
  }

  @Override
  public String getDownloadURL(String fileName, Long interviewId) {
    String filePathKey = makeS3FilePath(fileName, interviewId);

    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(filePathKey).build();

    PresignedGetObjectRequest presignedGetObjectRequest =
        s3Presigner.presignGetObject(
            builder -> builder.signatureDuration(urlDuration).getObjectRequest(getObjectRequest));

    return presignedGetObjectRequest.url().toString();
  }

  private String makeS3FilePath(String fileName, Long interviewId) {
    // files/interview번호/파일이름
    return baseFilePath + interviewId.toString() + fileName;
  }
}
