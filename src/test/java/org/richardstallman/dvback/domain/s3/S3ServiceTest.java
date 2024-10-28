package org.richardstallman.dvback.domain.s3;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class S3ServiceTest {

    @Autowired
    private S3Service s3Service;

    @Test
    void getUploadUrl(){
        String fileName = "test";
        Map<String, String> metadata = Map.of("fileType", "txt");

        String url = s3Service.createPreSignedURL(fileName, metadata);

        Assertions.assertThat(url).isNotNull();
    }

    @Test
    void getDownloadURL(){
        String fileName = "test";

        String url = s3Service.getDownloadURL(fileName);

        Assertions.assertThat(url).isNotNull();
    }   
}
