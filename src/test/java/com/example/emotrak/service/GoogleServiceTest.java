package com.example.emotrak.service;

import com.example.emotrak.dto.user.TokenDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.exception.CustomErrorCode;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class GoogleServiceTest {
    @Autowired
    private GoogleService googleService;

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
    @DisplayName("GoogleLogin")
    class googleLogin {
        @Test
        @DisplayName("정상적인 로그인")
        void GoogleLoginTest() throws JsonProcessingException {
            // 구글 OAuth2 인증 응답을 Mocking
            ResponseEntity<String> mockTokenResponse = new ResponseEntity<>(
                    "{\"access_token\":\"mock_token\", \"refresh_token\":\"mock_refresh\"}",
                    HttpStatus.OK
            );

            // 사용자 정보에 대한 구글 API 응답을 Mocking
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"sub\":\"1234\",\"email\":\"mock_email\",\"name\":\"mock_name\"}",
                    HttpStatus.OK
            );

            // 인증 코드로 액세스 토큰 요청을 Mocking
            when(rt.exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockTokenResponse);

            // 액세스 토큰으로 사용자 정보 요청을 Mocking
            when(rt.exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUserInfoResponse);

            // TokenDto Mocking
            TokenDto mockTokenDto = new TokenDto("Bearer", "mock_access_token", "mock_refresh_token", 123L);
            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenReturn(mockTokenDto);

            // 로그인 메서드 호출
            googleService.googleLogin("mock_auth_code", "mock_scope", new MockHttpServletResponse());

            // 액세스 토큰 요청과 사용자 정보 요청이 각각 한 번씩 호출되었음을 검증
            verify(rt, times(1)).exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            verify(rt, times(1)).exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );
        }

        @Test
        @DisplayName("구글유저가 존재하는 경우")
        void ExistingUser() throws JsonProcessingException {
            // Mock Responses
            ResponseEntity<String> mockTokenResponse = new ResponseEntity<>(
                    "{\"access_token\":\"mock_token\", \"refresh_token\":\"mock_refresh_token\"}",
                    HttpStatus.OK
            );
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"sub\":\"1234\", \"email\":\"mock@email.com\", \"name\":\"mocknickname\"}",
                    HttpStatus.OK
            );

            // Mock User
            User mockUser = new User(passwordEncoder.encode("randomPassword"), "mock@email.com", "mocknickname", null, null, "1234", UserRoleEnum.USER);

            // Mocking
            when(rt.exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockTokenResponse);

            when(rt.exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUserInfoResponse);

            when(userRepository.findByGoogleId("1234")).thenReturn(Optional.of(mockUser));

            TokenDto mockTokenDto = new TokenDto("Bearer", "mock_access_token", "mock_refresh_token", 123L);

            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenReturn(mockTokenDto);

            // 테스트 수행
            googleService.googleLogin("mock_code", "mock_scope", new MockHttpServletResponse());

            // verify restTemplate.exchange 호출 검증
            verify(rt, times(1)).exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            verify(rt, times(1)).exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            // verify userRepository.findByGoogleId 호출 검증
            verify(userRepository, times(1)).findByGoogleId("1234");

            // verify userRepository.save 호출 검증
            verify(userRepository, times(0)).save(any(User.class)); // User should not be saved again as it already exists
        }

        @Test
        @DisplayName("중복된 이메일을 가진 사용자가 있는 경우")
        void ExistingUserWithSameEmail() throws JsonProcessingException {
            // Access Token을 반환하는 Mock Response 설정
            ResponseEntity<String> mockTokenResponse = new ResponseEntity<>(
                    "{\"access_token\":\"mock_token\", \"refresh_token\":\"mock_refresh_token\"}",
                    HttpStatus.OK
            );

            // User Info를 반환하는 Mock Response 설정
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"sub\":\"mock_id\", \"email\":\"mock@email.com\", \"name\":\"mock_nickname\"}",
                    HttpStatus.OK
            );

            // Mock User
            User mockUser = new User(passwordEncoder.encode("randomPassword"), "mock@email.com", "mock_nickname", null, null, "mock_id", UserRoleEnum.USER);

            // "인가 코드"로 "액세스 토큰" 요청에 대한 Mocking
            when(rt.exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockTokenResponse);

            // 토큰으로 Google API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기에 대한 Mocking
            when(rt.exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
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
            googleService.googleLogin("mock_auth_code", "mock_scope", new MockHttpServletResponse());

            // verify restTemplate.exchange 호출 검증
            verify(rt, times(1)).exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            verify(rt, times(1)).exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            // verify userRepository.findByEmail 호출 검증
            verify(userRepository, times(1)).findByEmail("mock@email.com");

            // verify userRepository.findByGoogleId 호출 검증
            verify(userRepository, times(1)).findByGoogleId("mock_id");

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
                    "{\"access_token\":\"mock_token\", \"refresh_token\":\"mock_refresh_token\"}",
                    HttpStatus.OK
            );

            // User Info를 반환하는 Mock Response 설정
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"sub\":\"mock_id\", \"email\":\"mock@email.com\", \"name\":\"mock_nickname\"}",
                    HttpStatus.OK
            );

            // Mock User
            User mockUser = new User(passwordEncoder.encode("randomPassword"), "mock@email.com", "mock_nickname", null, null, "mock_id", UserRoleEnum.USER);

            // "인가 코드"로 "액세스 토큰" 요청에 대한 Mocking
            when(rt.exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockTokenResponse);

            // 토큰으로 Google API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기에 대한 Mocking
            when(rt.exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUserInfoResponse);

            // UserRepository Mocking
            when(userRepository.findByEmail("mock@email.com")).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            // Mock TokenDto
            TokenDto mockTokenDto = new TokenDto("Bearer","mock_access_token", "mock_refresh_token", 123L);

            // TokenProvider Mocking
            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenReturn(mockTokenDto);

            // 테스트 수행
            googleService.googleLogin("mock_auth_code", "mock_scope", new MockHttpServletResponse());

            // verify restTemplate.exchange 호출 검증
            verify(rt, times(1)).exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            verify(rt, times(1)).exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            // verify userRepository.findByEmail 호출 검증
            verify(userRepository, times(1)).findByEmail("mock@email.com");

            // verify userRepository.findByGoogleId 호출 검증
            verify(userRepository, times(1)).findByGoogleId("mock_id");

            // verify userRepository.save 호출 검증
            verify(userRepository, times(1)).save(any(User.class));

            // verify tokenProvider.generateTokenDto 호출 검증
            verify(tokenProvider, times(1)).generateTokenDto(any(User.class), any(UserRoleEnum.class));
        }

        @Test
        @DisplayName("중복된 닉네임이 존재할 경우")
        void testHasNickname() throws JsonProcessingException  {
            // Mock Responses
            ResponseEntity<String> mockTokenResponse = new ResponseEntity<>(
                    "{\"access_token\":\"mock_token\", \"refresh_token\":\"mock_refresh_token\"}",
                    HttpStatus.OK
            );
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"sub\":\"1234\", \"email\":\"mock@email.com\", \"name\":\"mocknickname\"}",
                    HttpStatus.OK
            );

            // Mock User
            User mockUser = new User(passwordEncoder.encode("randomPassword"), "mock@email.com", "mocknickname", null, null, "1234", UserRoleEnum.USER);

            // Mocking
            when(rt.exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockTokenResponse);

            when(rt.exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUserInfoResponse);

            when(userRepository.findByGoogleId("1234")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("mock@email.com")).thenReturn(Optional.empty());
            when(userRepository.existsByNickname("mocknickname")).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            TokenDto mockTokenDto = new TokenDto("Bearer", "mock_access_token", "mock_refresh_token", 123L);

            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenReturn(mockTokenDto);

            // 테스트 수행
            googleService.googleLogin("mock_code", "mock_state", new MockHttpServletResponse());

            // verify restTemplate.exchange 호출 검증
            verify(rt, times(1)).exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            verify(rt, times(1)).exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
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
                    "{\"access_token\":\"mock_token\", \"refresh_token\":\"mock_refresh_token\"}",
                    HttpStatus.OK
            );

            // User Info를 반환하는 Mock Response 설정
            ResponseEntity<String> mockUserInfoResponse = new ResponseEntity<>(
                    "{\"sub\":\"1234\", \"email\":\"mock@email.com\", \"name\":\"mocknickname\"}",
                    HttpStatus.OK
            );

            // Mock User
            User mockUser = new User("encodedPassword", "mock@email.com", "mocknickname", null, null, "1234", UserRoleEnum.USER);

            // "인가 코드"로 "액세스 토큰" 요청에 대한 Mocking
            when(rt.exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockTokenResponse);

            // 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기에 대한 Mocking
            when(rt.exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockUserInfoResponse);

            // UserRepository Mocking
            when(userRepository.findByGoogleId("1234")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("mock@email.com")).thenReturn(Optional.empty());
            when(userRepository.existsByNickname("mocknickname")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            // Mock TokenDto
            TokenDto mockTokenDto = new TokenDto("Bearer","mock_access_token", "mock_refresh_token", 123L);

            // TokenProvider Mocking
            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenReturn(mockTokenDto);

            // 테스트 수행
            googleService.googleLogin("mock_auth_code", "mock_state", new MockHttpServletResponse());

            // verify restTemplate.exchange 호출 검증
            verify(rt, times(1)).exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            );

            verify(rt, times(1)).exchange(
                    eq("https://www.googleapis.com/oauth2/v3/userinfo"),
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

            // 캡쳐된 User 객체의 닉네임 검증
            assertEquals("mocknickname", argument.getValue().getNickname());
        }
        @Test
        @DisplayName("닉네임이 null 인경우")
        void testNullNickname() throws JsonProcessingException {

        }

        @Test
        @DisplayName("리프레시토큰이 null 인경우")
        void testNoRefreshToken() throws JsonProcessingException {

        }
    }

        @Nested
        @DisplayName("GoogleUnlink")
        class googleUnlink {
            @Test
            @DisplayName("정상적인 연동해제")
            void UnlinkGoogleTest() {
                // Mock User
                User mockUser = new User();
                mockUser.setGoogleRefresh("mock_refresh");

                // 구글 OAuth2 리프레시 토큰 응답을 Mocking
                ResponseEntity<String> mockRefreshTokenResponse = new ResponseEntity<>(
                        "{\"refresh_token\":\"mock_refresh\", \"access_token\":\"mock_token\"}",
                        HttpStatus.OK
                );

                // 구글 OAuth2 연동 해제 응답을 Mocking
                ResponseEntity<String> mockUnlinkResponse = new ResponseEntity<>(
                        "{\"result\":\"success\", \"access_token\":\"mock_token\"}",
                        HttpStatus.OK
                );

                // 리프레시 토큰 요청을 Mocking
                when(rt.exchange(
                        eq("https://oauth2.googleapis.com/token"),
                        eq(HttpMethod.POST),
                        any(HttpEntity.class),
                        eq(String.class)
                )).thenReturn(mockRefreshTokenResponse);

                // 연동 해제 요청을 Mocking
                when(rt.exchange(
                        anyString(),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(String.class)
                )).thenReturn(mockUnlinkResponse);

                // unlinkGoogle 메소드 호출
                googleService.unlinkGoogle(mockUser);

                // rt.exchange 두 번 호출되었는지 검증
                verify(rt, times(2)).exchange(
                        anyString(),
                        any(HttpMethod.class),
                        any(HttpEntity.class),
                        eq(String.class)
                );
            }

            @Test
            @DisplayName("구글 연동 해제 실패-리프레시 토큰이 유효하지 않은 경우")
            void InvalidRefreshToken() {
                // Mock User
                User mockUser = new User();
                mockUser.setGoogleRefresh("mock_refresh");

                // 구글 OAuth2 리프레시 토큰 응답을 Mocking
                ResponseEntity<String> mockRefreshTokenResponse = new ResponseEntity<>(
                        "{\"error\":\"invalid_grant\"}",
                        HttpStatus.BAD_REQUEST
                );

                // 리프레시 토큰 요청을 Mocking
                when(rt.exchange(
                        eq("https://oauth2.googleapis.com/token"),
                        eq(HttpMethod.POST),
                        any(HttpEntity.class),
                        eq(String.class)
                )).thenReturn(mockRefreshTokenResponse);

                // CustomException 이 던져졌는지 검증
                assertThrows(CustomException.class, () -> googleService.unlinkGoogle(mockUser));
            }


            @Test
            @DisplayName("구글 연동 해제 실패-연동 해제가 실패하는 경우")
            void UnlinkFailed() {
                // Mock User
                User mockUser = new User();
                mockUser.setGoogleRefresh("mock_refresh");

                // 구글 OAuth2 리프레시 토큰 응답을 Mocking
                ResponseEntity<String> mockRefreshTokenResponse = new ResponseEntity<>(
                        "{\"result\":\"fail\"}",
                        HttpStatus.OK
                );

                // 구글 OAuth2 연동 해제 응답을 Mocking
                HttpClientErrorException mockException = new HttpClientErrorException(HttpStatus.UNAUTHORIZED);

                // 리프레시 토큰 요청을 Mocking
                when(rt.exchange(
                        eq("https://oauth2.googleapis.com/token"),
                        eq(HttpMethod.POST),
                        any(HttpEntity.class),
                        eq(String.class)
                )).thenReturn(mockRefreshTokenResponse);

                // 연동 해제 요청을 Mocking
                when(rt.exchange(
                        anyString(),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(String.class)
                )).thenThrow(mockException);

                // CustomException 이 던져졌는지 검증
                assertThrows(CustomException.class, () -> googleService.unlinkGoogle(mockUser));
            }

            @Test
            @DisplayName("구글 연동 해제 실패-연동 해제가 실패하는 경우(OAUTH_UNLINK_FAILED)")
            void UnlinkFailed2() {
                // Mock User
                User mockUser = new User();
                mockUser.setGoogleRefresh("mock_refresh");

                // 구글 OAuth2 리프레시 토큰 응답을 Mocking
                ResponseEntity<String> mockRefreshTokenResponse = new ResponseEntity<>(
                        "{\"refresh_token\":\"mock_refresh\", \"access_token\":\"mock_token\"}",
                        HttpStatus.OK
                );

                // 구글 OAuth2 연동 해제 응답을 Mocking
                HttpClientErrorException mockException = new HttpClientErrorException(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);

                // 리프레시 토큰 요청을 Mocking
                when(rt.exchange(
                        eq("https://oauth2.googleapis.com/token"),
                        eq(HttpMethod.POST),
                        any(HttpEntity.class),
                        eq(String.class)
                )).thenReturn(mockRefreshTokenResponse);

                // 연동 해제 요청을 Mocking
                when(rt.exchange(
                        anyString(),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(String.class)
                )).thenThrow(mockException);

                // CustomException 이 던져졌는지 검증
                CustomException exception = assertThrows(CustomException.class, () -> googleService.unlinkGoogle(mockUser));
                assertEquals(CustomErrorCode.OAUTH_UNLINK_FAILED, exception.getErrorCode());
            }
        }

    }