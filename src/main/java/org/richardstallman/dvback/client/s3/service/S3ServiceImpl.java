package org.richardstallman.dvback.domain.s3.service;

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

  private final Duration urlDuration = Duration.ofMinutes(10);

  @Override
  public String createPreSignedURL(String fileName, Map<String, String> metadata) {
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(fileName).build();

    PresignedPutObjectRequest presignedPutObjectRequest =
        s3Presigner.presignPutObject(
            builder -> builder.signatureDuration(urlDuration).putObjectRequest(putObjectRequest));

    return presignedPutObjectRequest.url().toString();
  }

  @Override
  public String getDownloadURL(String fileName) {
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(fileName).build();

    PresignedGetObjectRequest presignedGetObjectRequest =
        s3Presigner.presignGetObject(
            builder -> builder.signatureDuration(urlDuration).getObjectRequest(getObjectRequest));

    return presignedGetObjectRequest.url().toString();
  }
}
