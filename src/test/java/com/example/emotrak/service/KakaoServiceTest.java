package com.example.emotrak.service;

import com.example.emotrak.dto.user.TokenDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.jwt.TokenProvider;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class KakaoServiceTest {
    @Autowired
    private KakaoService kakaoService;

    @MockBean
    private RestTemplate rt;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TokenProvider tokenProvider;


    @Nested
    @DisplayName("KakaoLogin")
    class kakaoLogin {
        @Test
        @DisplayName("정상적인 로그인")
        void testKakaoLoginTest() throws JsonProcessingException {
            // Access Token을 반환하는 Mock Response 설정
            ResponseEntity<String> mockTokenResponse = new ResponseEntity<>(
                    "{\"access_token\":\"mock_token\"}",
                    HttpStatus.OK
            );

            // User Info를 반환하는 Mock Response 설정
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"id\":1234, \"properties\":{\"nickname\":\"mock_nickname\"}, \"kakao_account\":{\"email\":\"mock@email.com\"}}",
                    HttpStatus.OK
            );

            // "인가 코드"로 "액세스 토큰" 요청에 대한 Mocking
            when(rt.exchange(
                    eq("https://kauth.kakao.com/oauth/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockTokenResponse);

            // 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기에 대한 Mocking
            when(rt.exchange(
                    eq("https://kapi.kakao.com/v2/user/me"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUserInfoResponse);

            // Mock TokenDto
            TokenDto mockTokenDto = new TokenDto("Bearer","mock_access_token", "mock_refresh_token", 123L);

            // TokenProvider Mocking
            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenReturn(mockTokenDto);


            // 테스트 수행
            kakaoService.kakaoLogin("mock_auth_code", new MockHttpServletResponse());

            // Then
            verify(rt, times(1)).exchange(
                    eq("https://kauth.kakao.com/oauth/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            verify(rt, times(1)).exchange(
                    eq("https://kapi.kakao.com/v2/user/me"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );
        }

        @Test
        @DisplayName("User 객체가 null인 경우")
        void kakaoLoginTest_ExistingUserWithSameEmail() throws JsonProcessingException {
            // Access Token을 반환하는 Mock Response 설정
            ResponseEntity<String> mockTokenResponse = new ResponseEntity<>(
                    "{\"access_token\":\"mock_token\"}",
                    HttpStatus.OK
            );

            // User Info를 반환하는 Mock Response 설정
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"id\":1234, \"properties\":{\"nickname\":\"mock_nickname\"}, \"kakao_account\":{\"email\":\"mock@email.com\"}}",
                    HttpStatus.OK
            );

            // Mock User
            User mockUser = new User("encodedPassword", "mock@email.com", "mock_nickname", 1234L, null, null, UserRoleEnum.USER);

            // "인가 코드"로 "액세스 토큰" 요청에 대한 Mocking
            when(rt.exchange(
                    eq("https://kauth.kakao.com/oauth/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockTokenResponse);

            // 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기에 대한 Mocking
            when(rt.exchange(
                    eq("https://kapi.kakao.com/v2/user/me"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUserInfoResponse);

            // UserRepository Mocking
            when(userRepository.findByEmail("mock@email.com")).thenReturn(Optional.of(mockUser));
            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            // Mock TokenDto
            TokenDto mockTokenDto = new TokenDto("Bearer","mock_access_token", "mock_refresh_token", 123L);

            // TokenProvider Mocking
            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenReturn(mockTokenDto);


            // 테스트 수행
            kakaoService.kakaoLogin("mock_auth_code", new MockHttpServletResponse());

            // verify restTemplate.exchange 호출 검증
            verify(rt, times(1)).exchange(
                    eq("https://kauth.kakao.com/oauth/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            verify(rt, times(1)).exchange(
                    eq("https://kapi.kakao.com/v2/user/me"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            // verify userRepository.findByEmail 호출 검증
            verify(userRepository, times(1)).findByEmail("mock@email.com");

            // verify userRepository.save 호출 검증
            verify(userRepository, times(1)).save(any(User.class));

            // verify tokenProvider.generateTokenDto 호출 검증
            verify(tokenProvider, times(1)).generateTokenDto(any(User.class), any(UserRoleEnum.class));
        }

        @Test
        @DisplayName("User 객체가 null이 아닌 경우")
        void testKakaoApiFailure() throws JsonProcessingException {
        }

        @Test
        @DisplayName("중복된 닉게임이 존재할 경우")
        void testNicknameSuffixWhenHasNickname() {

        }

        @Test
        @DisplayName("중복된 닉네임이 존재하지 않을 경우")
        void testNicknameSuffixWhenNoNickname() {

        }

    }

    @Nested
    @DisplayName("KakaoUnlink")
    class kakaoUnlink {
        @Test
        @DisplayName("정상적인 연동해제")
        void UnlinkKakaoTest() {

        }

        @Test
        @DisplayName("카카오 연동 해제 실패")
        void UnlinkKakaoFailureTest() {


        }
    }

}