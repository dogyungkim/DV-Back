package org.richardstallman.dvback.domain.coupon.service;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.coupon.converter.CouponConverter;
import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponCreateResponseDto;
import org.richardstallman.dvback.domain.coupon.repository.CouponRepository;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

  private final CouponConverter couponConverter;
  private final CouponRepository couponRepository;
  private final UserService userService;
  private final UserConverter userConverter;

  @Override
  public CouponCreateResponseDto createCoupon(CouponCreateRequestDto couponCreateRequestDto) {
    UserDomain userDomain =
        userConverter.fromResponseDtoToDomain(
            userService.getUserInfo(couponCreateRequestDto.userId()));
    CouponDomain couponDomain =
        couponConverter.fromCreateRequestDtoToDomain(couponCreateRequestDto, userDomain);
    return couponConverter.fromDomainToCreateResponseDto(couponRepository.save(couponDomain));
  }
}
