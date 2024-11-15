package org.richardstallman.dvback.domain.coupon.repository;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.coupon.converter.CouponConverter;
import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;
import org.richardstallman.dvback.domain.coupon.entity.CouponEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

  private final CouponJpaRepository couponJpaRepository;
  private final CouponConverter couponConverter;

  @Override
  public CouponDomain save(CouponDomain couponDomain) {
    CouponEntity couponEntity =
        couponJpaRepository.save(couponConverter.fromDomainToEntity(couponDomain));

    return couponConverter.fromEntityToDomain(couponEntity);
  }

  @Override
  public CouponDomain findById(Long couponId) {
    CouponEntity couponEntity = couponJpaRepository.findById(couponId).orElse(null);
    return couponConverter.fromEntityToDomain(
        Objects.requireNonNull(couponJpaRepository.findById(couponId).orElse(null)));
  }
}
