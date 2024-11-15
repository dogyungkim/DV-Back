package org.richardstallman.dvback.domain.coupon.repository;

import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;

public interface CouponRepository {

  CouponDomain save(CouponDomain couponDomain);
}
