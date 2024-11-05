package org.richardstallman.dvback.client.s3.service;

import jakarta.annotation.Nullable;
import java.util.Map;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;

public interface S3Service {

  String createPreSignedURL(
      FileType fileType,
      String fileName,
      Long userId,
      @Nullable Long interviewId,
      @Nullable Map<String, String> metadata);

  String getDownloadURL(
      FileType fileType, String fileName, Long userId, @Nullable Long interviewId);
}
