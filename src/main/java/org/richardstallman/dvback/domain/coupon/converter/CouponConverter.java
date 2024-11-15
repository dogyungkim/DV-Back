package org.richardstallman.dvback.domain.coupon.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponCreateResponseDto;
import org.richardstallman.dvback.domain.coupon.entity.CouponEntity;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponConverter {

  private final UserConverter userConverter;

  public CouponDomain fromEntityToDomain(CouponEntity couponEntity) {
    return CouponDomain.builder()
        .couponId(couponEntity.getCouponId())
        .userDomain(userConverter.fromEntityToDomain(couponEntity.getUser()))
        .couponName(couponEntity.getCouponName())
        .couponType(couponEntity.getCouponType())
        .isUsed(couponEntity.isUsed())
        .build();
  }

  public CouponEntity fromDomainToEntity(CouponDomain couponDomain) {
    return new CouponEntity(
        couponDomain.getCouponId(), userConverter.fromDomainToEntity(couponDomain.getUserDomain()),
        couponDomain.getChargeAmount(), couponDomain.getCouponName(),
        couponDomain.getCouponType(), couponDomain.isUsed());
  }

  public CouponDomain fromCreateRequestDtoToDomain(
      CouponCreateRequestDto couponCreateRequestDto, UserDomain userDomain) {
    return CouponDomain.builder()
        .userDomain(userDomain)
        .chargeAmount(couponCreateRequestDto.chargeAmount())
        .couponName(couponCreateRequestDto.couponName())
        .couponType(couponCreateRequestDto.couponType())
        .isUsed(false)
        .build();
  }

  public CouponCreateResponseDto fromDomainToCreateResponseDto(CouponDomain couponDomain) {
    return new CouponCreateResponseDto(
        couponDomain.getCouponId(),
        couponDomain.getUserDomain().getId(),
        couponDomain.getChargeAmount(),
        couponDomain.getCouponName(),
        couponDomain.getCouponType());
  }
}
