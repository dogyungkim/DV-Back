package org.richardstallman.dvback.domain.coupon.converter;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponInfoResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponUseResponseDto;
import org.richardstallman.dvback.domain.coupon.entity.CouponEntity;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionResponseDto;
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
        .chargeAmount(couponEntity.getChargeAmount())
        .userDomain(userConverter.fromEntityToDomain(couponEntity.getUser()))
        .couponName(couponEntity.getCouponName())
        .interviewAssetType(couponEntity.getInterviewAssetType())
        .isUsed(couponEntity.isUsed())
        .generatedAt(couponEntity.getGeneratedAt())
        .usedAt(couponEntity.isUsed() ? couponEntity.getUsedAt() : null)
        .build();
  }

  public CouponEntity fromDomainToEntity(CouponDomain couponDomain) {
    return new CouponEntity(
        couponDomain.getCouponId(),
        userConverter.fromDomainToEntity(couponDomain.getUserDomain()),
        couponDomain.getChargeAmount(),
        couponDomain.getCouponName(),
        couponDomain.getInterviewAssetType(),
        couponDomain.isUsed(),
        couponDomain.getGeneratedAt(),
        couponDomain.isUsed() ? couponDomain.getUsedAt() : null);
  }

  public CouponDomain fromCreateRequestDtoToDomain(
      CouponCreateRequestDto couponCreateRequestDto,
      UserDomain userDomain,
      LocalDateTime generatedAt) {
    return CouponDomain.builder()
        .userDomain(userDomain)
        .chargeAmount(couponCreateRequestDto.chargeAmount())
        .couponName(couponCreateRequestDto.couponName())
        .interviewAssetType(couponCreateRequestDto.interviewAssetType())
        .isUsed(false)
        .generatedAt(generatedAt)
        .build();
  }

  public CouponInfoResponseDto fromDomainToInfoResponseDto(CouponDomain couponDomain) {
    return new CouponInfoResponseDto(
        couponDomain.getCouponId(),
        couponDomain.getUserDomain().getId(),
        couponDomain.getChargeAmount(),
        couponDomain.getCouponName(),
        couponDomain.getInterviewAssetType(),
        couponDomain.getInterviewAssetType().getKoreanName(),
        couponDomain.isUsed(),
        couponDomain.getGeneratedAt(),
        couponDomain.isUsed() ? couponDomain.getUsedAt() : null);
  }

  public CouponUseResponseDto generateUseResponseDto(
      CouponInfoResponseDto couponInfoResponseDto,
      TicketTransactionResponseDto ticketTransactionResponseDto) {
    return new CouponUseResponseDto(couponInfoResponseDto, ticketTransactionResponseDto);
  }

  public CouponDomain fromUnUsedToUsed(CouponDomain couponDomain, LocalDateTime usedAt) {
    return CouponDomain.builder()
        .couponId(couponDomain.getCouponId())
        .userDomain(couponDomain.getUserDomain())
        .chargeAmount(couponDomain.getChargeAmount())
        .couponName(couponDomain.getCouponName())
        .interviewAssetType(couponDomain.getInterviewAssetType())
        .isUsed(true)
        .generatedAt(couponDomain.getGeneratedAt())
        .usedAt(usedAt)
        .build();
  }
}
