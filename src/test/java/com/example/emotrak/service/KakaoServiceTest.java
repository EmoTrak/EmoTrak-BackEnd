package com.example.emotrak.service;

import com.example.emotrak.dto.user.OauthUserInfoDto;
import com.example.emotrak.dto.user.TokenDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.jwt.TokenProvider;
import com.example.emotrak.jwt.Validation;
import com.example.emotrak.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KakaoServiceTest {
    @InjectMocks
    private KakaoService kakaoService;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private Validation validation;
    @Mock
    private RestTemplate restTemplate;

    private String testCode;
    private String testAccessToken;
    private OauthUserInfoDto testOauthUserInfo;
    private User testUser;
    private String responseBody;

    @BeforeEach
    void setUp() {
        testCode = "test_code";
        testAccessToken = "test_access_token";
        testOauthUserInfo = new OauthUserInfoDto("12345", "bambee@gmail.com", "bambee");
        testUser = new User("encoded_password", "bambee@gmail.com", "bambee",
                12345L, null, null, UserRoleEnum.USER);
        responseBody = "test_response_body";
    }

    @Nested
    @DisplayName("KakaoLogin")
    class kakaoLogin {
        @Test
        @DisplayName("정상적인 로그인")
        public void getTokenTest() throws JsonProcessingException {
//            // 테스트 데이터를 설정합니다.
//            String code = "testCode";
//
//            // 인가 코드로 받은 액세스 토큰과 사용자 정보를 미리 설정합니다.
//            String accessToken = "testAccessToken";
//            OauthUserInfoDto userInfo = new OauthUserInfoDto("12345", "testEmail", "testNickname");
//
//            // UserRepository가 중복된 사용자가 없는 경우를 가정합니다.
//            User newUser = new User("encodedPassword", "testEmail", "testNickname", 12345L, null, null, UserRoleEnum.USER);
//            when(userRepository.findByKakaoId(anyLong())).thenReturn(Optional.empty());
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//            when(userRepository.existsByNickname(anyString())).thenReturn(false);
//            when(userRepository.getUniqueNameSuffix(anyString())).thenReturn(0L);
//
//            // HttpServletResponse 객체를 생성합니다.
//            HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
//
//            // HttpServletRequest 객체를 생성합니다.
//            HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
//            when(httpServletRequest.getParameter("code")).thenReturn(code);
//
//            // 카카오 API 호출에 대한 Mock을 설정합니다.
//            HttpHeaders expectedHeaders = new HttpHeaders();
//            expectedHeaders.setBearerAuth(accessToken);
//            expectedHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            String expectedResponseBody = "{\"id\":12345,\"properties\":{\"nickname\":\"testNickname\"},\"kakao_account\":{\"email\":\"testEmail\"}}";
//            when(restTemplate.exchange(eq("https://kapi.kakao.com/v2/user/me"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
//                    .thenReturn(new ResponseEntity<>(expectedResponseBody, HttpStatus.OK));
//
//            // 카카오 로그인을 시도합니다.
//            kakaoService.kakaoLogin(code, httpServletResponse);
//
//
//            // 요청과 응답에 대한 Mock 검증을 수행합니다.
//            verify(restTemplate, times(1)).exchange(eq("https://kauth.kakao.com/oauth/token"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
//            verify(restTemplate, times(1)).exchange(eq("https://kapi.kakao.com/v2/user/me"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
//            verify(userRepository, times(1)).findByKakaoId(anyLong());
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(userRepository, times(1)).save(newUser);
//            verify(validation, times(1)).tokenToHeaders(any(TokenDto.class), eq(httpServletResponse));
        }


    @Test
        @DisplayName("카카오 API 호출 실패")
        public void testKakaoApiFailure() {

        }

        @Test
        @DisplayName("중복된 Kakao Id를 가진 사용자가 존재하는 경우")
        public void testExistingUserWithSameKakaoId() {

        }

        @Test
        @DisplayName("유효하지 않은 인가 코드")
        public void testInvalidCode() {

        }

        @Test
        @DisplayName("이메일이 없는 경우")
        public void testNoEmail() {

        }

        @Test
        @DisplayName("중복된 이메일 또는 닉네임이 이미 존재하는 경우")
        public void testDuplicateEmailAndNickname() {

        }


        @Test
        @DisplayName("액세스 토큰을 받지 못한 경우")
        public void testNoAccessToken() {

        }
    }


}