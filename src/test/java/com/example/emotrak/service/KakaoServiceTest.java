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
import org.mockito.ArgumentCaptor;
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
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class KakaoServiceTest {
    @Autowired
    private KakaoService kakaoService;

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
    @DisplayName("KakaoLogin")
    class kakaoLogin {
        @Test
        @DisplayName("정상적인 로그인")
        void KakaoLoginTest() throws JsonProcessingException {
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
        @DisplayName("카카오유저가 존재하는 경우")
        void ExistingUser() throws JsonProcessingException {
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

            // UserRepository Mocking
            User existingUser = new User("encodedPassword", "existing_user_email", "existing_user_nickname", 1234L, null, null, UserRoleEnum.USER);
            when(userRepository.findByKakaoId(1234L)).thenReturn(Optional.of(existingUser));

            // Mock TokenDto
            TokenDto mockTokenDto = new TokenDto("Bearer","mock_access_token", "mock_refresh_token", 123L);
            when(tokenProvider.generateTokenDto(existingUser, existingUser.getRole())).thenReturn(mockTokenDto);

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

            // verify userRepository.findByKakaoId 호출 검증
            verify(userRepository, times(1)).findByKakaoId(1234L);

            // verify tokenProvider.generateTokenDto 호출 검증
            verify(tokenProvider, times(1)).generateTokenDto(existingUser, existingUser.getRole());
        }

        @Test
        @DisplayName("중복된 이메일을 가진 사용자가 있는 경우")
        void ExistingUserWithSameEmail() throws JsonProcessingException {
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

            // verify userRepository.findByKakaoId 호출 검증
            verify(userRepository, times(1)).findByKakaoId(1234L);

            // verify userRepository.save 호출 검증
            verify(userRepository, times(1)).save(any(User.class));

            // verify tokenProvider.generateTokenDto 호출 검증
            verify(tokenProvider, times(1)).generateTokenDto(any(User.class), any(UserRoleEnum.class));
        }

        @Test
        @DisplayName("중복된 이메일을 가진 사용자가 없는 경우")
        void NoExistingUserWithSameEmail() throws JsonProcessingException {
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
            when(userRepository.findByKakaoId(1234L)).thenReturn(Optional.empty());
            when(userRepository.findByEmail("mock@email.com")).thenReturn(Optional.empty());
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

            // verify userRepository.findByKakaoId 호출 검증
            verify(userRepository, times(1)).findByKakaoId(1234L);

            // verify userRepository.save 호출 검증
            verify(userRepository, times(1)).save(any(User.class));

            // verify tokenProvider.generateTokenDto 호출 검증
            verify(tokenProvider, times(1)).generateTokenDto(any(User.class), any(UserRoleEnum.class));
        }

        @Test
        @DisplayName("중복된 닉네임이 존재할 경우")
        void testHasNickname() throws JsonProcessingException  {
            // Access Token을 반환하는 Mock Response 설정
            ResponseEntity<String> mockTokenResponse = new ResponseEntity<>(
                    "{\"access_token\":\"mock_token\"}",
                    HttpStatus.OK
            );

            // User Info를 반환하는 Mock Response 설정
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"id\":1234, \"properties\":{\"nickname\":\"mocknickname\"}, \"kakao_account\":{\"email\":\"mock@email.com\"}}",
                    HttpStatus.OK
            );

            // Mock User
            User mockUser = new User("encodedPassword", "mock@email.com", "mocknickname", 1234L, null, null, UserRoleEnum.USER);

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
            when(userRepository.findByKakaoId(1234L)).thenReturn(Optional.empty());
            when(userRepository.findByEmail("mock@email.com")).thenReturn(Optional.empty());
            when(userRepository.existsByNickname("mocknickname")).thenReturn(true);
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

            // verify userRepository.getUniqueNameSuffix 호출 검증
            verify(userRepository, times(1)).getUniqueNameSuffix("mocknickname");

            // verify userRepository.save 호출 검증
            verify(userRepository, times(1)).save(any(User.class));

            // ArgumentCaptor를 이용해 userRepository.save()에 전달되는 User 객체 캡쳐
            ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(argument.capture());

            // 캡쳐된 User 객체의 닉네임 접미사 검증
            assertEquals("mocknickname_0", argument.getValue().getNickname());
        }

        @Test
        @DisplayName("중복된 닉네임이 존재하지 않을 경우")
        void testNoNickname() throws JsonProcessingException {
            // Access Token을 반환하는 Mock Response 설정
            ResponseEntity<String> mockTokenResponse = new ResponseEntity<>(
                    "{\"access_token\":\"mock_token\"}",
                    HttpStatus.OK
            );
            // User Info를 반환하는 Mock Response 설정
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"id\":1234, \"properties\":{\"nickname\":\"mocknickname\"}, \"kakao_account\":{\"email\":\"mock@email.com\"}}",
                    HttpStatus.OK
            );

            // Mock User
            User mockUser = new User("encodedPassword", "mock@email.com", "mocknickname", 1234L, null, null, UserRoleEnum.USER);

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
            when(userRepository.findByKakaoId(1234L)).thenReturn(Optional.empty());
            when(userRepository.findByEmail("mock@email.com")).thenReturn(Optional.empty());
            when(userRepository.existsByNickname("mocknickname")).thenReturn(false);
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

            // verify userRepository.getUniqueNameSuffix 호출 검증
            verify(userRepository, times(0)).getUniqueNameSuffix("mocknickname");

            // verify userRepository.save 호출 검증
            verify(userRepository, times(1)).save(any(User.class));

            // ArgumentCaptor를 이용해 userRepository.save()에 전달되는 User 객체 캡쳐
            ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(argument.capture());

            // 캡쳐된 User 객체의 닉네임 접미사 검증
            assertEquals("mocknickname", argument.getValue().getNickname());
        }
    }

    @Nested
    @DisplayName("KakaoUnlink")
    class kakaoUnlink {
        @Test
        @DisplayName("정상적인 연동해제")
        void UnlinkKakaoTest() {
            // Mock User
            User mockUser = new User("encodedPassword", "mock@email.com", "mock_nickname", 1234L, null, null, UserRoleEnum.USER);

            // 카카오 API 연동해제 Mocking
            ResponseEntity<String> mockUnlinkResponse = new ResponseEntity<>("{\"id\":1234}", HttpStatus.OK);
            when(rt.exchange(
                    eq("https://kapi.kakao.com/v1/user/unlink"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUnlinkResponse);

            // 테스트 수행
            assertDoesNotThrow(() -> kakaoService.unlinkKakao(mockUser));

            // verify restTemplate.exchange 호출 검증
            verify(rt, times(1)).exchange(
                    eq("https://kapi.kakao.com/v1/user/unlink"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            // verify restTemplate.exchange 호출 검증
            verify(rt, times(1)).exchange(
                    eq("https://kapi.kakao.com/v1/user/unlink"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );
        }

        @Test
        @DisplayName("카카오 연동 해제 실패")
        void UnlinkKakaoFailureTest() {
            // Mock User
            User mockUser = new User("encodedPassword", "mock@email.com", "mock_nickname", 1234L, null, null, UserRoleEnum.USER);

            // 카카오 API 연동해제 Mocking
            ResponseEntity<String> mockUnlinkResponse = new ResponseEntity<>("{\"id\":1234}", HttpStatus.OK);
            when(rt.exchange(
                    eq("https://kapi.kakao.com/v1/user/unlink"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUnlinkResponse);

            // 테스트 수행
            assertDoesNotThrow(() -> kakaoService.unlinkKakao(mockUser));

            // verify restTemplate.exchange 호출 검증
            verify(rt, times(1)).exchange(
                    eq("https://kapi.kakao.com/v1/user/unlink"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            // CustomException이 던져졌는지 검증
            when(rt.exchange(
                    eq("https://kapi.kakao.com/v1/user/unlink"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(new ResponseEntity<>("{\"msg\":\"Bad Request\", \"code\":400}", HttpStatus.BAD_REQUEST));

            assertThrows(CustomException.class, () -> kakaoService.unlinkKakao(mockUser));
        }
    }

}