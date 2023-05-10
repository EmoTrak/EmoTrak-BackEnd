package com.example.emotrak.service;

import com.example.emotrak.dto.user.TokenDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.jwt.TokenProvider;
import com.example.emotrak.jwt.Validation;
import com.example.emotrak.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NaverServiceTest {
    @Autowired
    private NaverService naverService;

    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private Validation validation;
    @MockBean
    private RestTemplate rt;

    @Nested
    @DisplayName("NaverLogin")
    class naverLogin {
        @Test
        @DisplayName("정상적인 로그인")
        void NaverLoginTest() throws JsonProcessingException {
            // 네이버 OAuth2 인증 응답을 Mocking
            ResponseEntity<String> mockTokenResponse = new ResponseEntity<>(
                    "{\"access_token\":\"mock_token\", \"refresh_token\":\"mock_refresh\"}",
                    HttpStatus.OK
            );

            // 사용자 정보에 대한 네이버 API 응답을 Mocking
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"response\":{\"id\":\"1234\",\"name\":\"mock_name\",\"email\":\"mock_email\",\"nickname\":\"mock_nickname\"}}",
                    HttpStatus.OK
            );


            // 인증 코드로 액세스 토큰 요청을 Mocking
            when(rt.exchange(
                    eq("https://nid.naver.com/oauth2.0/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockTokenResponse);

            // 액세스 토큰으로 사용자 정보 요청을 Mocking
            when(rt.exchange(
                    eq("https://openapi.naver.com/v1/nid/me"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUserInfoResponse);

            // TokenDto Mocking
            TokenDto mockTokenDto = new TokenDto("Bearer", "mock_access_token", "mock_refresh_token", 123L);
            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenReturn(mockTokenDto);

            // 로그인 메서드 호출
            naverService.naverLogin("mock_auth_code", "mock_auth_state", new MockHttpServletResponse());

            // 액세스 토큰 요청과 사용자 정보 요청이 각각 한 번씩 호출되었음을 검증
            verify(rt, times(1)).exchange(
                    eq("https://nid.naver.com/oauth2.0/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            verify(rt, times(1)).exchange(
                    eq("https://openapi.naver.com/v1/nid/me"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );
        }

//        @Test
//        @DisplayName("")
//        void ㄹ() {
//
//        }


    }

    @Nested
    @DisplayName("NaverUnlink")
    class naverUnlink {
        @Test
        @DisplayName("정상적인 연동해제")
        void UnlinkNaverTest() {
            // Mock User
            User mockUser = new User();
            mockUser.setNaverRefresh("mock_refresh");

            // 네이버 OAuth2 리프레시 토큰 응답을 Mocking
            ResponseEntity<String> mockRefreshTokenResponse = new ResponseEntity<>(
                    "{\"refresh_token\":\"mock_refresh\", \"access_token\":\"mock_token\"}",
                    HttpStatus.OK
            );

            // 네이버 OAuth2 연동 해제 응답을 Mocking
            ResponseEntity<String> mockUnlinkResponse = new ResponseEntity<>(
                    "{\"result\":\"success\", \"access_token\":\"mock_token\"}",
                    HttpStatus.OK
            );

            // 리프레시 토큰 요청을 Mocking
            when(rt.exchange(
                    eq("https://nid.naver.com/oauth2.0/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockRefreshTokenResponse);

            // 연동 해제 요청을 Mocking
            when(rt.exchange(
                    eq("https://nid.naver.com/oauth2.0/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUnlinkResponse);

            // unlinkNaver 메소드 호출
            naverService.unlinkNaver(mockUser);

            // rt.exchange 두 번 호출되었는지 검증
            verify(rt, times(2)).exchange(
                    eq("https://nid.naver.com/oauth2.0/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );
        }

        @Test
        @DisplayName("네이버 연동 해제 실패-리프레시 토큰이 유효하지 않은 경우")
        void InvalidRefreshToken() {
            // Mock User
            User mockUser = new User();
            mockUser.setNaverRefresh("mock_refresh");

            // 네이버 OAuth2 리프레시 토큰 응답을 Mocking
            ResponseEntity<String> mockRefreshTokenResponse = new ResponseEntity<>(
                    "{\"refresh_token\":\"mock_refresh\"}",
                    HttpStatus.OK
            );

            // 리프레시 토큰 요청을 Mocking
            when(rt.exchange(
                    eq("https://nid.naver.com/oauth2.0/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockRefreshTokenResponse);

            // CustomException 이 던져졌는지 검증
            assertThrows(CustomException.class, () -> naverService.unlinkNaver(mockUser));
        }

        @Test
        @DisplayName("네이버 연동 해제 실패-액세스 토큰이 유효하지 않아 연동 해제가 실패하는 경우")
       void UnlinkFailed() {
            // Mock User
            User mockUser = new User();
            mockUser.setNaverRefresh("mock_refresh");

            // 네이버 OAuth2 리프레시 토큰 응답을 Mocking
            ResponseEntity<String> mockRefreshTokenResponse = new ResponseEntity<>(
                    "{\"result\":\"fail\"}",
                    HttpStatus.OK
            );

            // 네이버 OAuth2 연동 해제 응답을 Mocking
            HttpClientErrorException mockException = new HttpClientErrorException(HttpStatus.UNAUTHORIZED);

            // 리프레시 토큰 요청을 Mocking
            when(rt.exchange(
                    eq("https://nid.naver.com/oauth2.0/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockRefreshTokenResponse);

            // 연동 해제 요청을 Mocking
            when(rt.exchange(
                    eq("https://nid.naver.com/oauth2.0/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenThrow(mockException);

            // CustomException 이 던져졌는지 검증
            assertThrows(CustomException.class, () -> naverService.unlinkNaver(mockUser));
        }
    }

}