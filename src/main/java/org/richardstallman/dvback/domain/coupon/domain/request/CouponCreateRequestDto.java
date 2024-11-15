package org.richardstallman.dvback.domain.coupon.domain.request;

import jakarta.validation.constraints.NotNull;

public record CouponCreateRequestDto(
    @NotNull Long userId,
    @NotNull int chargeAmount,
    @NotNull String couponName,
    @NotNull String couponType) {}
