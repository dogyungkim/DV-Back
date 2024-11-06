package org.richardstallman.dvback.domain.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.domain.file.domain.response.PreSignedUrlResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class S3ServiceTest {

  @Autowired private S3Service s3Service;

  @Test
  void getUploadUrl() {
    FileType fileType = FileType.COVER_LETTER;
    String fileName = "test";
    Map<String, String> metadata = Map.of("Content-type", "text/plain");
    Long interviewId = 1L;
    Long userId = 1L;

    PreSignedUrlResponseDto url =
        s3Service.createPreSignedURL(fileType, fileName, userId, interviewId, null);

    assertThat(url.preSignedUrl()).isNotNull();
    assertThat(url.preSignedUrl()).contains("cover-letter/");
  }

  @Test
  void getDownloadURL() {
    FileType fileType = FileType.COVER_LETTER;
    String fileName = "test";
    Long userId = 1L;
    Long interviewId = 0L;

    PreSignedUrlResponseDto url = s3Service.getDownloadURL(fileType, fileName, userId, interviewId);

    assertThat(url.preSignedUrl()).isNotNull();
    assertThat(url.preSignedUrl()).contains("cover-letter/");
  }
}
