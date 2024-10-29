package org.richardstallman.dvback.client.s3.service;

import jakarta.annotation.Nullable;
import java.util.Map;

public interface S3Service {

  String createPreSignedURL(
      String fileName, Long interviewId, @Nullable Map<String, String> metadata);

  String getDownloadURL(String fileName, Long interviewId);
}
