package org.richardstallman.dvback.domain.coupon.domain;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;

@Builder
@Getter
public class CouponDomain {

  private final Long couponId;
  private final UserDomain userDomain;
  private final int chargeAmount;
  private final String couponName;
  private String couponType;
  private boolean isUsed;
}
