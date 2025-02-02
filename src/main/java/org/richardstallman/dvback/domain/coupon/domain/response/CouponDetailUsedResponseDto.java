package org.richardstallman.dvback.domain.coupon.domain.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CouponDetailUsedResponseDto(
    @NotNull long couponId,
    @NotNull int chargeAmount,
    @NotNull String couponName,
    @NotNull String interviewModeKorean,
    @NotNull String interviewAssetTypeKorean,
    @NotNull LocalDateTime usedAt) {}
