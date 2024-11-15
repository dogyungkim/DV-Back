package org.richardstallman.dvback.domain.coupon.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponCreateResponseDto;
import org.richardstallman.dvback.domain.coupon.service.CouponService;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
    String couponType = "EVENT"; // 뭘 해야 할 지 모르겠음..근데 필요할 것 같아서 넣어 둠
    CouponCreateRequestDto couponCreateRequestDto =
        new CouponCreateRequestDto(userId, chargeAmount, couponName, couponType);

    Long couponId = 1L;
    CouponCreateResponseDto couponCreateResponseDto =
        new CouponCreateResponseDto(couponId, userId, chargeAmount, couponName, couponType);

    when(couponService.createCoupon(any())).thenReturn(couponCreateResponseDto);

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
        .andExpect(jsonPath("data.couponType").value(couponType));

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
                        fieldWithPath("couponType").type(JsonFieldType.STRING).description("쿠폰 유형"))
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
                        fieldWithPath("data.couponType")
                            .type(JsonFieldType.STRING)
                            .description("쿠폰 유형"))
                    .build())));
  }
}
