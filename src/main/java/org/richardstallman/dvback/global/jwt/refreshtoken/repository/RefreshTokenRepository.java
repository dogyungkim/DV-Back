package org.richardstallman.dvback.global.jwt.refreshtoken.repository;

import java.util.Optional;
import org.richardstallman.dvback.global.security.jwt.domain.RefreshTokenDomain;

public interface RefreshTokenRepository {

  RefreshTokenDomain save(RefreshTokenDomain refreshTokenDomain);

  Optional<RefreshTokenDomain> findByToken(String token);

  void deleteByToken(String token);
}