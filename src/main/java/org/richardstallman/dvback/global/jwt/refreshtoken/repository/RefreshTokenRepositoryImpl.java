package org.richardstallman.dvback.global.jwt.refreshtoken.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.global.jwt.refreshtoken.converter.RefreshTokenConverter;
import org.richardstallman.dvback.global.jwt.refreshtoken.domain.RefreshTokenDomain;
import org.richardstallman.dvback.global.jwt.refreshtoken.entity.RefreshTokenEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

  private final RefreshTokenJpaRepository refreshTokenJpaRepository;
  private final RefreshTokenConverter refreshTokenConverter;

  @Override
  public RefreshTokenDomain save(RefreshTokenDomain refreshTokenDomain) {
    RefreshTokenEntity entity = refreshTokenConverter.toEntity(refreshTokenDomain);
    return refreshTokenConverter.toDomain(refreshTokenJpaRepository.save(entity));
  }

  @Override
  public Optional<RefreshTokenDomain> findByToken(String token) {
    return refreshTokenJpaRepository.findById(token)
        .map(refreshTokenConverter::toDomain);
  }

  @Override
  public void deleteByToken(String token) {
    refreshTokenJpaRepository.deleteById(token);
  }
}
