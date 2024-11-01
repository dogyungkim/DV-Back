package org.richardstallman.dvback.global.jwt.refreshtoken.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Entity
@Getter
@NoArgsConstructor
@RedisHash("refreshToken")
public class RefreshTokenEntity {

  @Id
  private String refreshToken;
  private Long userId;

  public RefreshTokenEntity(String refreshToken, Long userId) {
    this.refreshToken = refreshToken;
    this.userId = userId;
  }
}
