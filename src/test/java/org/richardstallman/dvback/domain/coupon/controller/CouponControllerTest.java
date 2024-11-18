package org.richardstallman.dvback.domain.coupon.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponUseRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponInfoResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponUseResponseDto;
import org.richardstallman.dvback.domain.coupon.service.CouponService;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionDetailResponseDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionResponseDto;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CouponControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

  @MockBean private CouponService couponService;

  @Test
  @WithMockUser
  @DisplayName("쿠폰 발급 테스트")
  void create_coupon() throws Exception {
    // given
    Long userId = 1L;
    int chargeAmount = 1;
    String couponName = "웰컴 쿠폰";
    InterviewAssetType interviewAssetType = InterviewAssetType.CHAT;
    InterviewMode interviewMode = InterviewMode.REAL;
    boolean isUsed = false;
    boolean isExpired = false;
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime expiredAt = createdAt.plusMonths(1).with(LocalTime.MAX);
    CouponCreateRequestDto couponCreateRequestDto =
        new CouponCreateRequestDto(
            userId, chargeAmount, couponName, interviewMode, interviewAssetType);

    Long couponId = 1L;
    CouponInfoResponseDto couponInfoResponseDto =
        new CouponInfoResponseDto(
            couponId,
            userId,
            chargeAmount,
            couponName,
            interviewMode,
            interviewMode.getKoreanName(),
            interviewAssetType,
            interviewAssetType.getKoreanName(),
            isUsed,
            isExpired,
            createdAt,
            null,
            expiredAt);

    when(couponService.createCoupon(any())).thenReturn(couponInfoResponseDto);

    ResultActions resultActions =
        mockMvc.perform(
            post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(couponCreateRequestDto)));
    // then
    resultActions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.couponId").value(couponId))
        .andExpect(jsonPath("data.userId").value(userId))
        .andExpect(jsonPath("data.chargeAmount").value(chargeAmount))
        .andExpect(jsonPath("data.couponName").value(couponName))
        .andExpect(jsonPath("data.interviewAssetType").value(interviewAssetType.name()));

    // restdocs
    resultActions.andDo(
        document(
            "쿠폰 발급 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Coupon API")
                    .summary("쿠폰 API")
                    .requestFields(
                        fieldWithPath("userId")
                            .type(JsonFieldType.NUMBER)
                            .description("쿠폰 발급 받을 회원 식별자"),
                        fieldWithPath("chargeAmount")
                            .type(JsonFieldType.NUMBER)
                            .description("쿠폰으로 충전 가능한 이용권 장 수"),
                        fieldWithPath("couponName").type(JsonFieldType.STRING).description("쿠폰 이름"),
                        fieldWithPath("interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 면접 유형: REAL(실전), GENERAL(모의)"),
                        fieldWithPath("interviewAssetType")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 유형"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.couponId")
                            .type(JsonFieldType.NUMBER)
                            .description("쿠폰 식별자"),
                        fieldWithPath("data.userId")
                            .type(JsonFieldType.NUMBER)
                            .description("쿠폰 발급 받은 회원 식별자"),
                        fieldWithPath("data.chargeAmount")
                            .type(JsonFieldType.NUMBER)
                            .description("쿠폰으로 충전 가능한 이용권 장 수"),
                        fieldWithPath("data.couponName")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 이름"),
                        fieldWithPath("data.interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 면접 유형: REAL(실전), GENERAL(모의)"),
                        fieldWithPath("data.interviewModeKorean")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 면접 유형 한글"),
                        fieldWithPath("data.interviewAssetType")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 유형"),
                        fieldWithPath("data.interviewAssetTypeKorean")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 유형 한글"),
                        fieldWithPath("data.isUsed")
                            .type(JsonFieldType.BOOLEAN)
                            .description("쿠폰 사용 여부: 사용(true), 미사용(false)"),
                        fieldWithPath("data.isExpired")
                            .type(JsonFieldType.BOOLEAN)
                            .description("쿠폰 만료 여부: 만료(true), 만료 전(false)"),
                        fieldWithPath("data.generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 생성 일시"),
                        fieldWithPath("data.usedAt")
                            .type(JsonFieldType.NULL)
                            .description("쿠폰 사용 일시"),
                        fieldWithPath("data.expireAt")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 만료 일시"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("쿠폰 사용 & 채팅 이용권 충전 테스트 - 성공")
  void use_coupon_and_charge_ticket() throws Exception {
    // given
    Long userId = 1L;
    Long couponId = 1L;
    int chargeAmount = 1;
    String couponName = "웰컴 쿠폰";
    InterviewAssetType interviewAssetType = InterviewAssetType.CHAT;
    InterviewMode interviewMode = InterviewMode.REAL;
    boolean isExpired = false;
    LocalDateTime expiredAt = LocalDateTime.now().plusMonths(1).with(LocalTime.MAX);
    int currentBalance = 5;
    int currentChatBalance = 2;
    int currentVoiceBalance = 3;
    Long ticketTransactionId = 1L;
    int amount = 1;
    TicketTransactionType ticketTransactionType = TicketTransactionType.CHARGE;
    TicketTransactionMethod ticketTransactionMethod = TicketTransactionMethod.COUPON;
    InterviewAssetType interviewAssetType2 = InterviewAssetType.CHAT;
    String descripton =
        ticketTransactionType.getKoreanName() + " " + ticketTransactionMethod.getKoreanName();
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime usedAt = LocalDateTime.now();
    LocalDateTime occurredAt = LocalDateTime.now();
    String accessToken = jwtUtil.generateAccessToken(userId);

    CouponUseRequestDto couponUseRequestDto = new CouponUseRequestDto(1L);

    CouponInfoResponseDto couponInfoResponseDto =
        new CouponInfoResponseDto(
            couponId,
            userId,
            chargeAmount,
            couponName,
            interviewMode,
            interviewMode.getKoreanName(),
            interviewAssetType,
            interviewAssetType.getKoreanName(),
            true,
            isExpired,
            createdAt,
            usedAt,
            expiredAt);

    TicketTransactionDetailResponseDto ticketTransactionDetailResponseDto =
        new TicketTransactionDetailResponseDto(
            ticketTransactionId,
            amount,
            ticketTransactionType,
            ticketTransactionType.getKoreanName(),
            ticketTransactionMethod,
            ticketTransactionMethod.getKoreanName(),
            interviewAssetType2,
            interviewAssetType2.getKoreanName(),
            descripton,
            occurredAt);

    TicketTransactionResponseDto ticketTransactionResponseDto =
        new TicketTransactionResponseDto(
            currentBalance,
            currentChatBalance,
            currentVoiceBalance,
            ticketTransactionDetailResponseDto);

    CouponUseResponseDto couponUseResponseDto =
        new CouponUseResponseDto(couponInfoResponseDto, ticketTransactionResponseDto);

    MockCookie authCookie = new MockCookie("access_token", accessToken);

    String content = objectMapper.writeValueAsString(couponUseRequestDto);

    when(couponService.useCoupon(any(), eq(userId))).thenReturn(couponUseResponseDto);
    ResultActions resultActions =
        mockMvc.perform(
            post("/coupon/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.usedCouponInfo.couponId").value(couponId))
        .andExpect(jsonPath("data.usedCouponInfo.userId").value(userId))
        .andExpect(jsonPath("data.usedCouponInfo.chargeAmount").value(couponId))
        .andExpect(jsonPath("data.usedCouponInfo.couponName").value(couponName))
        .andExpect(
            jsonPath("data.usedCouponInfo.interviewAssetType").value(interviewAssetType.name()))
        .andExpect(
            jsonPath("data.usedCouponInfo.interviewAssetTypeKorean")
                .value(interviewAssetType.getKoreanName()))
        .andExpect(
            jsonPath("data.chargedTicketTransactionInfo.currentBalance").value(currentBalance))
        .andExpect(
            jsonPath(
                    "data.chargedTicketTransactionInfo.ticketTransactionDetail.ticketTransactionId")
                .value(ticketTransactionId))
        .andExpect(
            jsonPath("data.chargedTicketTransactionInfo.ticketTransactionDetail.amount")
                .value(amount))
        .andExpect(
            jsonPath(
                    "data.chargedTicketTransactionInfo.ticketTransactionDetail.ticketTransactionType")
                .value(ticketTransactionType.name()))
        .andExpect(
            jsonPath(
                    "data.chargedTicketTransactionInfo.ticketTransactionDetail.ticketTransactionTypeKorean")
                .value(ticketTransactionType.getKoreanName()))
        .andExpect(
            jsonPath(
                    "data.chargedTicketTransactionInfo.ticketTransactionDetail.ticketTransactionMethod")
                .value(ticketTransactionMethod.name()))
        .andExpect(
            jsonPath(
                    "data.chargedTicketTransactionInfo.ticketTransactionDetail.ticketTransactionMethodKorean")
                .value(ticketTransactionMethod.getKoreanName()))
        .andExpect(
            jsonPath("data.chargedTicketTransactionInfo.ticketTransactionDetail.description")
                .value(descripton));

    // restdocs
    resultActions.andDo(
        document(
            "쿠폰 사용 & 이용권 충전 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Coupon API")
                    .summary("쿠폰 API")
                    .requestFields(
                        fieldWithPath("couponId")
                            .type(JsonFieldType.NUMBER)
                            .description("사용하고자 하는 쿠폰 식별자"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.usedCouponInfo.couponId")
                            .type(JsonFieldType.NUMBER)
                            .description("사용된 쿠폰 식별자"),
                        fieldWithPath("data.usedCouponInfo.userId")
                            .type(JsonFieldType.NUMBER)
                            .description("쿠폰을 사용한 회원 식별자"),
                        fieldWithPath("data.usedCouponInfo.chargeAmount")
                            .type(JsonFieldType.NUMBER)
                            .description("쿠폰으로 충전된 이용권 장 수"),
                        fieldWithPath("data.usedCouponInfo.couponName")
                            .type(JsonFieldType.STRING)
                            .description("사용된 쿠폰 이름"),
                        fieldWithPath("data.usedCouponInfo.interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("사용된 쿠폰 면접 모드: REAL(실전), GENERAL(모의)"),
                        fieldWithPath("data.usedCouponInfo.interviewModeKorean")
                            .type(JsonFieldType.STRING)
                            .description("사용된 쿠폰 면접 모드 한글"),
                        fieldWithPath("data.usedCouponInfo.interviewAssetType")
                            .type(JsonFieldType.STRING)
                            .description("사용된 쿠폰 유형: CHAT(채팅), VOICE(음성)"),
                        fieldWithPath("data.usedCouponInfo.interviewAssetTypeKorean")
                            .type(JsonFieldType.STRING)
                            .description("사용된 쿠폰 유형 한글"),
                        fieldWithPath("data.usedCouponInfo.isUsed")
                            .type(JsonFieldType.BOOLEAN)
                            .description("쿠폰 사용 여부: 사용(true), 미사용(false)"),
                        fieldWithPath("data.usedCouponInfo.isExpired")
                            .type(JsonFieldType.BOOLEAN)
                            .description("쿠폰 만료 여부: 만료(true), 만료 전(false)"),
                        fieldWithPath("data.usedCouponInfo.generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 생성 일시"),
                        fieldWithPath("data.usedCouponInfo.usedAt")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 사용 일시"),
                        fieldWithPath("data.usedCouponInfo.expireAt")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 만료 일시"),
                        fieldWithPath("data.chargedTicketTransactionInfo.currentBalance")
                            .type(JsonFieldType.NUMBER)
                            .description("회원이 현재 보유한 이용권 장 수"),
                        fieldWithPath("data.chargedTicketTransactionInfo.currentChatBalance")
                            .type(JsonFieldType.NUMBER)
                            .description("회원이 현재 보유한 채팅 이용권 장 수"),
                        fieldWithPath("data.chargedTicketTransactionInfo.currentVoiceBalance")
                            .type(JsonFieldType.NUMBER)
                            .description("회원이 현재 보유한 음성 이용권 장 수"),
                        fieldWithPath(
                                "data.chargedTicketTransactionInfo.ticketTransactionDetail.ticketTransactionId")
                            .type(JsonFieldType.NUMBER)
                            .description("이용권 내역 식별자"),
                        fieldWithPath(
                                "data.chargedTicketTransactionInfo.ticketTransactionDetail.amount")
                            .type(JsonFieldType.NUMBER)
                            .description("충전된 이용권 장 수"),
                        fieldWithPath(
                                "data.chargedTicketTransactionInfo.ticketTransactionDetail.ticketTransactionType")
                            .type(JsonFieldType.STRING)
                            .description("이용권 내역 유형: CHARGE(충전), USE(사용)"),
                        fieldWithPath(
                                "data.chargedTicketTransactionInfo.ticketTransactionDetail.ticketTransactionTypeKorean")
                            .type(JsonFieldType.STRING)
                            .description("이용권 내역 유형 한글"),
                        fieldWithPath(
                                "data.chargedTicketTransactionInfo.ticketTransactionDetail.ticketTransactionMethod")
                            .type(JsonFieldType.STRING)
                            .description("이용권 충전 방법: COUPON(쿠폰), EVENT(이벤트)"),
                        fieldWithPath(
                                "data.chargedTicketTransactionInfo.ticketTransactionDetail.ticketTransactionMethodKorean")
                            .type(JsonFieldType.STRING)
                            .description("이용권 충전 방법 한글"),
                        fieldWithPath(
                                "data.chargedTicketTransactionInfo.ticketTransactionDetail.interviewAssetType")
                            .type(JsonFieldType.STRING)
                            .description("이용권 유형: CHAT(채팅), VOICE(음성)"),
                        fieldWithPath(
                                "data.chargedTicketTransactionInfo.ticketTransactionDetail.interviewAssetTypeKorean")
                            .type(JsonFieldType.STRING)
                            .description("이용권 유형 한글"),
                        fieldWithPath(
                                "data.chargedTicketTransactionInfo.ticketTransactionDetail.description")
                            .type(JsonFieldType.STRING)
                            .description("이용권 상세 설명"),
                        fieldWithPath(
                                "data.chargedTicketTransactionInfo.ticketTransactionDetail.generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("이용권 내역 발생 일시"))
                    .build())));
  }
}
