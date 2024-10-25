package org.richardstallman.dvback.global.config.jobs;

import java.util.List;
import lombok.Data;
import org.richardstallman.dvback.global.config.yml.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource(
    value = "classpath:/properties/${spring.profiles.active}/initial_data.yml",
    factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "job")
public class JobsConfig {

  private List<Jobs> jobs;

  @Data
  public static class Jobs {

    private String jobName;
    private String jobDescription;
  }
}
