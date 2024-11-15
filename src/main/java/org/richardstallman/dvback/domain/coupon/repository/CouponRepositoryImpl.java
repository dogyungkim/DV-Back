package org.richardstallman.dvback.domain.coupon.repository;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.coupon.converter.CouponConverter;
import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

  private final CouponJpaRepository couponJpaRepository;
  private final CouponConverter couponConverter;

  @Override
  public CouponDomain save(CouponDomain couponDomain) {
    return couponConverter.fromEntityToDomain(
        couponJpaRepository.save(couponConverter.fromDomainToEntity(couponDomain)));
  }
}
