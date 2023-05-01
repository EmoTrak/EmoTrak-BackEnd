package com.example.emotrak.service;

import com.example.emotrak.dto.user.OauthUserInfoDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

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

        }

        @Test
        public void getKakaoUserInfoTest() throws JsonProcessingException {

        }

        @Test
        public void registerKakaoUserIfNeededTest_newUser() {
//        // Given
//        OauthUserInfoDto oauthUserInfo = new OauthUserInfoDto("12345", "email@example.com", "nickname");
//
//        when(userRepository.findByKakaoId(anyLong())).thenReturn(Optional.empty());
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0, User.class));
//
//        // When
//        User result = kakaoService.registerKakaoUserIfNeeded(oauthUserInfo);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(oauthUserInfo.getEmail(), result.getEmail());
//        assertEquals(Long.valueOf(oauthUserInfo.getId()), result.getKakaoId());
        }

        @Test
        public void registerKakaoUserIfNeededTest_existingUserWithoutKakaoId() {
//        // Given
//        OauthUserInfoDto oauthUserInfo = new OauthUserInfoDto("12345", "email@example.com", "nickname");
//        User existingUser = new User("password", "email@example.com", "nickname", null, null, null, UserRoleEnum.USER);
//
//        when(userRepository.findByKakaoId(anyLong())).thenReturn(Optional.empty());
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0, User.class));
//
//        // When
//        User result = kakaoService.registerKakaoUserIfNeeded(oauthUserInfo);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(oauthUserInfo.getEmail(), result.getEmail());
//        assertEquals(Long.valueOf(oauthUserInfo.getId()), result.getKakaoId());
        }

        @Test
        public void registerKakaoUserIfNeededTest_existingUserWithKakaoId() {
//        // Given
//        OauthUserInfoDto oauthUserInfo = new OauthUserInfoDto("12345", "email@example.com", "nickname");
//        User existingUser = new User("password", "email@example.com", "nickname", 12345L, null, null, UserRoleEnum.USER);
//
//        when(userRepository.findByKakaoId(anyLong())).thenReturn(Optional.of(existingUser));
//
//        // When
//        User result = kakaoService.registerKakaoUserIfNeeded(oauthUserInfo);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(oauthUserInfo.getEmail(), result.getEmail());
//        assertEquals(Long.valueOf(oauthUserInfo.getId()), result.getKakaoId());
        }

        @Test
        public void kakaoLoginTest() throws JsonProcessingException {

        }

    }
}