package org.richardstallman.dvback.client.s3.service;

import jakarta.annotation.Nullable;
import java.util.Map;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.domain.file.domain.response.PreSignedUrlResponseDto;

public interface S3Service {

  PreSignedUrlResponseDto getPreSignedUrlForImage(String fileName, Long userId);

  PreSignedUrlResponseDto getDownloadUrlForImage(Long userId);

  PreSignedUrlResponseDto createPreSignedURLForInterview(
      FileType fileType,
      String fileName,
      Long userId,
      @Nullable Long interviewId,
      @Nullable Map<String, String> metadata);

  PreSignedUrlResponseDto getDownloadURLForInterview(
      String filePath, Long userId, @Nullable Long interviewId);
}
