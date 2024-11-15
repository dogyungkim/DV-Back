package org.richardstallman.dvback.domain.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponCreateResponseDto;
import org.richardstallman.dvback.domain.coupon.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/coupon", produces = MediaType.APPLICATION_JSON_VALUE)
public class CouponController {

  private final CouponService couponService;

  @PostMapping
  public ResponseEntity<DvApiResponse<CouponCreateResponseDto>> createCoupon(
      @Valid @RequestBody final CouponCreateRequestDto couponCreateRequestDto) {
    final CouponCreateResponseDto couponCreateResponseDto =
        couponService.createCoupon(couponCreateRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(DvApiResponse.of(couponCreateResponseDto));
  }
}
