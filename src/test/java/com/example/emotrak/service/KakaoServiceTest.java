package com.example.emotrak.service;

import com.example.emotrak.entity.User;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

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

    private User testUser;

    @Value("${kakao_client_id}")
    private String KakaoClientId;

    @Value("${kakao_admin_key}")
    private String KakaoAdminKey;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setKakaoId(123456789L);
        ReflectionTestUtils.setField(kakaoService, "KakaoClientId", KakaoClientId);
    }

    @Nested
    @DisplayName("KakaoLogin")
    class kakaoLogin {
        @Test
        @DisplayName("정상적인 로그인")
        void testKakaoLogin() throws JsonProcessingException {
//             RestTemplate rt = new RestTemplate();
//            MockRestServiceServer mockServer = MockRestServiceServer.bindTo(rt).build();
//
//            // 토큰 발급 요청에 대한 모킹 응답을 설정합니다.
//            String tokenResponse = "{\"access_token\":\"test-access-token\",\"token_type\":\"bearer\",\"refresh_token\":\"test-refresh-token\",\"expires_in\":43199,\"scope\":\"account_email\",\"refresh_token_expires_in\":25184000}";
//            mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
//                    .andExpect(method(HttpMethod.POST))
//                    .andRespond(withSuccess(tokenResponse, MediaType.APPLICATION_JSON));
//
//            // 사용자 정보 요청에 대한 모킹 응답을 설정합니다.
//            String userInfoResponse = "{\"id\":123456789,\"connected_at\":\"2021-05-01T12:00:00Z\",\"properties\":{\"nickname\":\"홍길동\"},\"kakao_account\":{\"email\":\"test@example.com\"}}";
//            mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
//                    .andExpect(method(HttpMethod.POST))
//                    .andRespond(withSuccess(userInfoResponse, MediaType.APPLICATION_JSON));
//
//            String code = "sample_code";
//            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
//
//            // When
//            kakaoService.kakaoLogin(code, response);
//
//            // Then
//            verify(validation).tokenToHeaders(any(TokenDto.class), any(HttpServletResponse.class));
        }

        @Test
        @DisplayName("getToken 메서드 테스트")
        void testGetToken() throws JsonProcessingException {
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
        }

        @Test
        @DisplayName("카카오 API 호출 실패")
        void testKakaoApiFailure() throws JsonProcessingException {
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
        }

        @Test
        @DisplayName("사용자 정보를 얻지 못하는 경우")
        void testKakaoUserInfoFailure() throws JsonProcessingException {
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
        }

        @Test
        @DisplayName("유효하지 않은 인가 코드")
        void testInvalidCode() throws JsonProcessingException {
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
        }

        @Test
        @DisplayName("액세스 토큰을 받지 못한 경우")
        void testNoAccessToken() throws JsonProcessingException {
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
        }

        @Test
        @DisplayName("사용자 저장 과정에서 실패")
        void testSaveUserFailure() throws JsonProcessingException {
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
        }

        @Test
        @DisplayName("토큰 생성 과정에서 실패")
        void testTokenGenerationFailure() throws JsonProcessingException {
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
        }

    }

    @Nested
    @DisplayName("KakaoUnlink")
    class kakaoUnlink {
        @Test
        @DisplayName("정상적인 연동해제")
        void testUnlinkKakao() {

        }

        @Test
        @DisplayName("카카오 연동 해제 실패")
        void testUnlinkKakaoFailure() {


        }


    }
}