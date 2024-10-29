package org.richardstallman.dvback.domain.s3;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class S3ServiceTest {

  @Autowired private S3Service s3Service;

  @Test
  void getUploadUrl() {
    String fileName = "test";
    Map<String, String> metadata = Map.of("Content-type", "text/plain");
    Long interviewId = 0L;

    String url = s3Service.createPreSignedURL(fileName, interviewId, null);

    assertThat(url).isNotNull();
    assertThat(url).contains("files/");
  }

  @Test
  void getDownloadURL() {
    String fileName = "test";
    Long interviewId = 0L;

    String url = s3Service.getDownloadURL(fileName, interviewId);

    assertThat(url).isNotNull();
    assertThat(url).contains("files/");
  }
}
