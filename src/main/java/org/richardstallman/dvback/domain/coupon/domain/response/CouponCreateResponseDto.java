package org.richardstallman.dvback.domain.coupon.domain.response;

import jakarta.validation.constraints.NotNull;

public record CouponCreateResponseDto(
    @NotNull Long couponId,
    @NotNull Long userId,
    @NotNull int chargeAmount,
    @NotNull String couponName,
    @NotNull String couponType) {}
