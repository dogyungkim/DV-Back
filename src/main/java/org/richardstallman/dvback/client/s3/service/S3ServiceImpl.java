package org.richardstallman.dvback.client.s3.service;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

  @Autowired
  private final S3Presigner s3Presigner;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  @Value("${cloud.aws.s3.baseFilePath}")
  private String baseFilePath;

  @Value("${cloud.aws.s3.urlDurationMinutes}")
  private long urlDurationMinutes;

  private Duration urlDuration;

  @PostConstruct
  private void init() {
    this.urlDuration = Duration.ofMinutes(urlDurationMinutes);
  }

  @Override
  public String createPreSignedURL(
      FileType fileType,
      String fileName,
      Long userId,
      @Nullable Long interviewId,
      @Nullable Map<String, String> metadata) {
    String filePathKey = makeS3FilePath(fileType, fileName, userId, interviewId);

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(filePathKey).build();

    PresignedPutObjectRequest presignedPutObjectRequest =
        s3Presigner.presignPutObject(
            builder -> builder.signatureDuration(urlDuration).putObjectRequest(putObjectRequest));

    return presignedPutObjectRequest.url().toString();
  }

  @Override
  public String getDownloadURL(
      FileType fileType, String fileName, Long userId, @Nullable Long interviewId) {
    String filePathKey = makeS3FilePath(fileType, fileName, userId, interviewId);

    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(filePathKey).build();

    PresignedGetObjectRequest presignedGetObjectRequest =
        s3Presigner.presignGetObject(
            builder -> builder.signatureDuration(urlDuration).getObjectRequest(getObjectRequest));

    return presignedGetObjectRequest.url().toString();
  }

  private String makeS3FilePath(
      FileType fileType, String fileName, Long userId, @Nullable Long interviewId) {
    String timestamp = String.valueOf(System.currentTimeMillis());

    if (interviewId != null) {
      // 면접 정보 입력 시 업로드 경로
      return String.format(
          "%s/%d/%d/%s/%s", fileType.getFolderName(), userId, interviewId, timestamp, fileName);
    } else {
      // 미리 업로드 경로
      return String.format("%s/%d/%s/%s", fileType.getFolderName(), userId, timestamp, fileName);
    }
  }
}
