package org.richardstallman.dvback.client.firebase.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

  @Value("${firebase.config-path}")
  private String firebaseConfigPath;

  @PostConstruct
  public void init() {
    try {
      InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();
      FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();
      FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
