package org.richardstallman.dvback.global.jwt.refreshtoken.converter;

import org.richardstallman.dvback.global.security.jwt.domain.RefreshTokenDomain;
import org.richardstallman.dvback.global.security.jwt.entity.RefreshTokenEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenConverter {

  public RefreshTokenEntity toEntity(RefreshTokenDomain domain) {
    return new RefreshTokenEntity(domain.getToken(), domain.getUserId());
  }

  public RefreshTokenDomain toDomain(RefreshTokenEntity entity) {
    return RefreshTokenDomain.builder()
        .token(entity.getRefreshToken())
        .userId(entity.getUserId())
        .build();
  }
}
