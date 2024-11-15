package org.richardstallman.dvback.domain.coupon.service;

import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponCreateResponseDto;

public interface CouponService {

  CouponCreateResponseDto createCoupon(CouponCreateRequestDto couponCreateRequestDto);
}
