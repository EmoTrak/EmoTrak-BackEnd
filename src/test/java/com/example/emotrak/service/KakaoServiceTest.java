//package com.example.emotrak.service;
//
//import com.example.emotrak.dto.user.OauthUserInfoDto;
//import com.example.emotrak.dto.user.TokenDto;
//import com.example.emotrak.entity.User;
//import com.example.emotrak.entity.UserRoleEnum;
//import com.example.emotrak.jwt.TokenProvider;
//import com.example.emotrak.jwt.Validation;
//import com.example.emotrak.repository.UserRepository;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.*;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import javax.servlet.http.HttpServletResponse;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class KakaoServiceTest {
//    @InjectMocks
//    private KakaoService kakaoService;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private TokenProvider tokenProvider;
//    @Mock
//    private Validation validation;
//    @Mock
//    private RestTemplate restTemplate;
//
//    private User testUser;
//    private String KakaoClientId = "test_client_id";
//
//    @BeforeEach
//    void setUp() {
//        passwordEncoder = new BCryptPasswordEncoder();
//        testUser = new User();
//        testUser.setKakaoId(123456789L);
//        ReflectionTestUtils.setField(kakaoService, "KakaoClientId", KakaoClientId);
//    }
//
//    // private protected 로 변경해야 해서 테스트 보류..
//    @Nested
//    @DisplayName("KakaoLogin")
//    class kakaoLogin {
//        @Test
//        @DisplayName("정상적인 로그인")
//        void testKakaoLogin() throws JsonProcessingException {
//            // Given
//            String code = "test_auth_code";
//            HttpServletResponse response = mock(HttpServletResponse.class);
//            String accessToken = "test_access_token";
//            OauthUserInfoDto oauthUserInfoDto = new OauthUserInfoDto("12345", "test@example.com", "test_nickname");
//
//            KakaoService kakaoServiceSpy = spy(kakaoService);
//            doReturn(accessToken).when(kakaoServiceSpy).getToken(code);
//            doReturn(oauthUserInfoDto).when(kakaoServiceSpy).getKakaoUserInfo(accessToken);
//
//            when(userRepository.findByKakaoId(anyLong())).thenReturn(Optional.empty());
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//            when(userRepository.save(any(User.class))).thenReturn(new User());
//            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenReturn(new TokenDto());
//            doNothing().when(validation).tokenToHeaders(any(TokenDto.class), any(HttpServletResponse.class));
//
//            // When
//            kakaoServiceSpy.kakaoLogin(code, response);
//
//            // Then
//            verify(kakaoServiceSpy, times(1)).getToken(code);
//            verify(kakaoServiceSpy, times(1)).getKakaoUserInfo(accessToken);
//            verify(userRepository, times(1)).findByKakaoId(anyLong());
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(userRepository, times(1)).save(any(User.class));
//            verify(tokenProvider, times(1)).generateTokenDto(any(User.class), any(UserRoleEnum.class));
//            verify(validation, times(1)).tokenToHeaders(any(TokenDto.class), any(HttpServletResponse.class));
//        }
//
//        @Test
//        @DisplayName("getToken 메서드 테스트")
//        void testGetToken() throws JsonProcessingException {
//            // 테스트 데이터 준비
//            String code = "code";
//            String sampleResponse = "{\"access_token\":\"sample_access_token\"}";
//
//            // RestTemplate 모킹
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//            body.add("grant_type", "authorization_code");
//            body.add("client_id", KakaoClientId);
//            body.add("redirect_uri", "https://emotrak.vercel.app/oauth/kakao");
//            body.add("code", code);
//            HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
//
//            ResponseEntity<String> mockedResponse = new ResponseEntity<>(sampleResponse, HttpStatus.OK);
//            when(restTemplate.postForEntity(
//                    eq("https://kauth.kakao.com/oauth/token"),
//                    eq(kakaoTokenRequest),
//                    eq(String.class)
//            )).thenReturn(mockedResponse);
//
//            // 메서드 호출
//            String result = kakaoService.getToken(code);
//
//            // 검증
//            assertEquals("sample_access_token", result);
//        }
//
//        @Test
//        @DisplayName("카카오 API 호출 실패")
//        void testKakaoApiFailure() throws JsonProcessingException {
//            // Given
//            String code = "test_auth_code";
//            HttpServletResponse response = mock(HttpServletResponse.class);
//
//            // Mock
//            KakaoService kakaoServiceSpy = spy(kakaoService);
//            doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
//                    .when(kakaoServiceSpy).getToken(code);
//
//            // When
//            assertThrows(HttpClientErrorException.class, () -> {kakaoServiceSpy.kakaoLogin(code, response);});
//
//            // Then
//            verify(kakaoServiceSpy, times(1)).getToken(code);
//            verify(kakaoServiceSpy, times(0)).getKakaoUserInfo(anyString());
//        }
//
//        @Test
//        @DisplayName("사용자 정보를 얻지 못하는 경우")
//        void testKakaoUserInfoFailure() throws JsonProcessingException {
//            // Given
//            String code = "test_auth_code";
//            HttpServletResponse response = mock(HttpServletResponse.class);
//
//            // Mock
//            KakaoService kakaoServiceSpy = spy(kakaoService);
//            doReturn("test_access_token").when(kakaoServiceSpy).getToken(code);
//            doThrow(new RuntimeException("사용자 정보 얻지 못함"))
//                    .when(kakaoServiceSpy).getKakaoUserInfo("test_access_token");
//
//            assertThrows(RuntimeException.class, () -> kakaoServiceSpy.kakaoLogin(code, response));
//        }
//
//        @Test
//        @DisplayName("유효하지 않은 인가 코드")
//        void testInvalidCode() throws JsonProcessingException {
//            // Given
//            String invalidCode = "invalid_auth_code";
//            HttpServletResponse response = mock(HttpServletResponse.class);
//
//            // Mock
//            KakaoService kakaoServiceSpy = spy(kakaoService);
//            doThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED))
//                    .when(kakaoServiceSpy).getToken(invalidCode);
//
//            // When
//            assertThrows(HttpClientErrorException.class, () -> {kakaoServiceSpy.kakaoLogin(invalidCode, response);});
//
//            // Then
//            verify(kakaoServiceSpy, times(1)).getToken(invalidCode);
//            verify(kakaoServiceSpy, times(0)).getKakaoUserInfo(anyString());
//        }
//
//        @Test
//        @DisplayName("액세스 토큰을 받지 못한 경우")
//        void testNoAccessToken() throws JsonProcessingException {
//            // Given
//            String code = "test_auth_code";
//            HttpServletResponse response = mock(HttpServletResponse.class);
//
//            // Mock
//            KakaoService kakaoServiceSpy = spy(kakaoService);
//            doThrow(new RuntimeException("엑세스토큰을 받지 못함"))
//                    .when(kakaoServiceSpy).getToken(code);
//
//            // Expect an exception to be thrown
//            assertThrows(RuntimeException.class, () -> kakaoServiceSpy.kakaoLogin(code, response));
//        }
//
//        @Test
//        @DisplayName("사용자 저장 과정에서 실패")
//        void testSaveUserFailure() throws JsonProcessingException {
//            // Given
//            String code = "test_auth_code";
//            HttpServletResponse response = mock(HttpServletResponse.class);
//
//            // Mock
//            KakaoService kakaoServiceSpy = spy(kakaoService);
//            doReturn("test_access_token").when(kakaoServiceSpy).getToken(code);
//            doReturn(new OauthUserInfoDto("12345", "test@example.com", "test_nickname"))
//                    .when(kakaoServiceSpy).getKakaoUserInfo("test_access_token");
//
//            when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("사용자 저장 실패"));
//
//            assertThrows(RuntimeException.class, () -> kakaoServiceSpy.kakaoLogin(code, response));
//
//            // Then
//            verify(kakaoServiceSpy, times(1)).getToken(code);
//            verify(kakaoServiceSpy, times(1)).getKakaoUserInfo("test_access_token");
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("토큰 생성 과정에서 실패")
//        void testTokenGenerationFailure() throws JsonProcessingException {
//            // Given
//            String code = "test_auth_code";
//            HttpServletResponse response = mock(HttpServletResponse.class);
//
//            // Mock
//            KakaoService kakaoServiceSpy = spy(kakaoService);
//            doReturn("test_access_token").when(kakaoServiceSpy).getToken(code);
//            doReturn(new OauthUserInfoDto("12345", "test@example.com", "test_nickname"))
//                    .when(kakaoServiceSpy).getKakaoUserInfo("test_access_token");
//
//            when(userRepository.findByKakaoId(anyLong())).thenReturn(Optional.empty());
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//            when(userRepository.save(any(User.class))).thenReturn(new User());
//            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenThrow(new RuntimeException("토큰 생성 실패"));
//
//            assertThrows(RuntimeException.class, () -> kakaoServiceSpy.kakaoLogin(code, response));
//
//            verify(kakaoServiceSpy, times(1)).getToken(code);
//            verify(kakaoServiceSpy, times(1)).getKakaoUserInfo("test_access_token");
//            verify(userRepository, times(1)).save(any(User.class));
//            verify(tokenProvider, times(1)).generateTokenDto(any(User.class), any(UserRoleEnum.class));
//        }
//
//        @Test
//        @DisplayName("사용자 비밀번호 인코딩 테스트")
//        void testPasswordEncoding() {
//            // Given
//            String email = "test@example.com";
//            String rawPassword = "test_password";
//            User newUser = new User();
//            newUser.setEmail(email);
//            newUser.setPassword(rawPassword);
//
//            // When
//            newUser.setPassword(passwordEncoder.encode(rawPassword));
//
//            // Then
//            assertTrue(passwordEncoder.matches(rawPassword, newUser.getPassword()));
//        }
//
//        @Test
//        @DisplayName("비밀번호 불일치 테스트")
//        void testPasswordMismatch() {
//            // Given
//            String email = "test@example.com";
//            String rawPassword = "test_password";
//            String wrongPassword = "wrong_password";
//            User newUser = new User();
//            newUser.setEmail(email);
//            newUser.setPassword(rawPassword);
//
//            // When
//            newUser.setPassword(passwordEncoder.encode(rawPassword));
//
//            // Then
//            assertFalse(passwordEncoder.matches(wrongPassword, newUser.getPassword()));
//        }
//
//    }
//
//    @Nested
//    @DisplayName("KakaoUnlink")
//    class kakaoUnlink {
//        @Test
//        @DisplayName("정상적인 연동해제")
//        void testUnlinkKakao() {
//
//        }
//
//        @Test
//        @DisplayName("카카오 연동 해제 실패")
//        void testUnlinkKakaoFailure() {
//
//
//        }
//
//
//    }
//}