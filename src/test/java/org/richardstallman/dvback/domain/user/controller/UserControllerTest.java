package org.richardstallman.dvback.domain.user.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.common.constant.CommonConstants;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.service.UserPostService;
import org.richardstallman.dvback.domain.user.service.UserService;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtUtil jwtUtil;

  @MockBean private UserService userService;
  @MockBean private UserPostService userPostService;
  @MockBean private RefreshTokenRepository refreshTokenRepository;

  @Test
  @WithMockUser
  @DisplayName("유저 정보 업데이트 - 성공")
  void update_user_info_success() throws Exception {
    // given
    UserRequestDto userRequestDto =
        new UserRequestDto(
            "김수현", "suhyun", "dodam", LocalDate.of(1990, 1, 1), CommonConstants.Gender.WOMAN);

    String userInfoJson = objectMapper.writeValueAsString(userRequestDto);
    MockMultipartFile userInfoPart =
        new MockMultipartFile(
            "userInfo",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            userInfoJson.getBytes(StandardCharsets.UTF_8));
    MockMultipartFile profileImage =
        new MockMultipartFile(
            "profileImage",
            "image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test-image".getBytes(StandardCharsets.UTF_8));

    UserResponseDto userResponseDto =
        new UserResponseDto(
            1L,
            "12345",
            "example@test.com",
            "suhyun",
            "김수현",
            "dodam",
            "https://example.com/image.jpg",
            false,
            CommonConstants.Gender.WOMAN,
            LocalDate.of(1990, 1, 1));

    when(userPostService.updateUserInfo(
            any(Long.class), any(UserRequestDto.class), any(MultipartFile.class)))
        .thenReturn(userResponseDto);

    String accessToken = jwtUtil.generateAccessToken(1L);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            multipart("/user/info")
                .file(userInfoPart)
                .file(profileImage)
                .cookie(authCookie)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data.userId").value(1L))
        .andExpect(jsonPath("$.data.nickname").value("dodam"))
        .andExpect(jsonPath("$.data.email").value("example@test.com"));

    // restdocs
    resultActions.andDo(
        document(
            "유저 정보 업데이트 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("유저 정보 업데이트 API")
                    .responseFields(
                        fieldWithPath("userInfo").description("사용자의 JSON 정보"),
                        fieldWithPath("profileImage").description("사용자의 프로필 이미지 파일"))
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.userId").description("유저 ID"),
                        fieldWithPath("data.socialId").description("소셜 ID"),
                        fieldWithPath("data.email").description("이메일"),
                        fieldWithPath("data.username").description("유저네임"),
                        fieldWithPath("data.name").description("유저 이름"),
                        fieldWithPath("data.nickname").description("유저 닉네임"),
                        fieldWithPath("data.s3ProfileImageUrl").description("프로필 이미지 URL"),
                        fieldWithPath("data.leave").description("탈퇴 여부"),
                        fieldWithPath("data.gender").description("성별"),
                        fieldWithPath("data.birthdate").description("생년월일"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("로그아웃 - 성공")
  void logout_success() throws Exception {
    // Mocked Cookie
    String accessToken = jwtUtil.generateAccessToken(1L);
    String refreshToken = jwtUtil.generateRefreshToken(1L);
    MockCookie accessTokenCookie = new MockCookie("access_token", accessToken);
    MockCookie refreshTokenCookie = new MockCookie("refresh_token", refreshToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            post("/user/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(accessTokenCookie, refreshTokenCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Logged out successfully"));

    // restdocs
    resultActions.andDo(
        document("로그아웃 - 성공", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
  }

  @Test
  @WithMockUser
  @DisplayName("유저 정보 조회 - 성공")
  void get_user_info_success() throws Exception {
    // given
    Long userId = 1L;
    UserResponseDto userResponseDto =
        new UserResponseDto(
            userId,
            "12345",
            "example@test.com",
            "suhyun",
            "김수현",
            "왕감자",
            "https://example.com/image.jpg",
            false,
            CommonConstants.Gender.WOMAN,
            LocalDate.of(1990, 1, 1));

    when(userService.getUserInfo(userId)).thenReturn(userResponseDto);

    // Mocked Cookie for authorization
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/user/info").contentType(MediaType.APPLICATION_JSON).cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data.userId").value(userId))
        .andExpect(jsonPath("$.data.nickname").value("왕감자"))
        .andExpect(jsonPath("$.data.email").value("example@test.com"));

    // restdocs
    resultActions.andDo(
        document(
            "유저 정보 조회 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("유저 API")
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.userId").description("유저 ID"),
                        fieldWithPath("data.socialId").description("소셜 ID"),
                        fieldWithPath("data.email").description("이메일"),
                        fieldWithPath("data.username").description("유저 네임"),
                        fieldWithPath("data.name").description("유저 이름"),
                        fieldWithPath("data.nickname").description("유저 닉네임"),
                        fieldWithPath("data.s3ProfileImageUrl").description("프로필 이미지 URL"),
                        fieldWithPath("data.leave").description("탈퇴 여부"),
                        fieldWithPath("data.gender").description("성별"),
                        fieldWithPath("data.birthdate").description("생년월일"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("인증 상태 확인 성공")
  void isAuthenticated_success() throws Exception {
    // given
    Long userId = 1L; // Mock user ID
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/user/authenticated").contentType(MediaType.APPLICATION_JSON).cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data").value(true));

    // restdocs
    resultActions.andDo(
        document(
            "로그인 여부 확인 - 인증 상태 확인 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("현재 사용자 인증 상태 확인")
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("인증 여부 (true: 인증됨, false: 인증되지 않음)"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("토큰 없음으로 인증 실패")
  void isAuthenticated_failure_noToken() throws Exception {
    // when
    ResultActions resultActions =
        mockMvc.perform(get("/user/authenticated").contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data").value(false));

    // restdocs
    resultActions.andDo(
        document(
            "로그인 여부 확인 - 인증 실패 (토큰 없음)",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("현재 사용자 인증 상태 확인")
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("인증 여부 (true: 인증됨, false: 인증되지 않음)"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("유저네임 중복 확인 - 중복있음")
  void validate_username_success() throws Exception {
    // given
    String username = "suhyun";
    when(userService.existsByUsername(username)).thenReturn(true);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/user/validate-username")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data").value(true));

    // restdocs
    resultActions.andDo(
        document(
            "유저네임 중복 확인 - 중복있음",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("유저네임 중복 확인 API")
                    .description("주어진 유저네임이 중복인지 확인합니다.")
                    .queryParameters(parameterWithName("username").description("중복 확인할 유저네임"))
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("유저네임 중복 여부 (true: 중복, false: 사용 가능)"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("유저네임 중복 확인 - 중복 없음")
  void validate_username_no_duplicate() throws Exception {
    // given
    String username = "newUser";
    when(userService.existsByUsername(username)).thenReturn(false);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/user/validate-username")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data").value(false));

    // restdocs
    resultActions.andDo(
        document(
            "유저네임 중복 확인 - 중복 없음",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("유저네임 중복 확인 API")
                    .description("주어진 유저네임이 중복인지 확인합니다.")
                    .queryParameters(parameterWithName("username").description("중복 확인할 유저네임"))
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("유저네임 중복 여부 (true: 중복, false: 사용 가능)"))
                    .build())));
  }
}
