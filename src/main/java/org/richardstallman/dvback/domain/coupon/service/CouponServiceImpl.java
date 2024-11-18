package org.richardstallman.dvback.domain.coupon.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.domain.coupon.converter.CouponConverter;
import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponUseRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponInfoResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponUseResponseDto;
import org.richardstallman.dvback.domain.coupon.repository.CouponRepository;
import org.richardstallman.dvback.domain.ticket.domain.request.TicketTransactionRequestDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionResponseDto;
import org.richardstallman.dvback.domain.ticket.service.TicketService;
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
  private final TicketService ticketService;

  @Override
  public CouponInfoResponseDto createCoupon(CouponCreateRequestDto couponCreateRequestDto) {
    UserDomain userDomain =
        userConverter.fromResponseDtoToDomain(
            userService.getUserInfo(couponCreateRequestDto.userId()));
    LocalDateTime now = getCurrentDateTime();
    LocalDateTime expireAt = generateExpirationDateTime(now);
    CouponDomain couponDomain =
        couponConverter.fromCreateRequestDtoToDomain(
            couponCreateRequestDto, userDomain, now, expireAt);
    return couponConverter.fromDomainToInfoResponseDto(couponRepository.save(couponDomain));
  }

  private LocalDateTime generateExpirationDateTime(LocalDateTime now) {
    return now.plusMonths(1).with(LocalTime.MAX);
  }

  @Override
  @Transactional
  public CouponUseResponseDto useCoupon(CouponUseRequestDto couponUseRequestDto, Long userId) {
    CouponDomain couponDomain = couponRepository.findById(couponUseRequestDto.couponId());

    validateCoupon(couponDomain);

    couponDomain = couponConverter.fromUnUsedToUsed(couponDomain, getCurrentDateTime());

    TicketTransactionRequestDto ticketTransactionRequestDto =
        new TicketTransactionRequestDto(
            couponDomain.getChargeAmount(),
            TicketTransactionType.CHARGE,
            TicketTransactionMethod.COUPON,
            couponDomain.getInterviewAssetType(),
            "");

    CouponDomain usedCoupon = couponRepository.save(couponDomain);

    TicketTransactionResponseDto ticketTransactionResponseDto =
        ticketService.chargeTicket(ticketTransactionRequestDto, userId);

    return couponConverter.generateUseResponseDto(
        couponConverter.fromDomainToInfoResponseDto(usedCoupon), ticketTransactionResponseDto);
  }

  private void validateCoupon(CouponDomain couponDomain) {
    if (couponDomain.getExpireAt().isBefore(LocalDateTime.now())) {
      couponDomain = couponRepository.save(couponConverter.fromUnExpiredToExpired(couponDomain));
    }
    if (couponDomain.isUsed()) {
      throw new IllegalStateException("Coupon (" + couponDomain.getCouponId() + ") already used");
    }
    if (couponDomain.isExpired()) {
      throw new IllegalStateException(
          "Coupon ("
              + couponDomain.getCouponId()
              + ") expired at ("
              + couponDomain.getExpireAt()
              + ")");
    }
  }

  private LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now();
  }
}
