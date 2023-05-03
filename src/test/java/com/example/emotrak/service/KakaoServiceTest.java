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
        public void testKakaoLogin() {

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