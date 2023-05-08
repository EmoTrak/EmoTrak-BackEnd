package com.example.emotrak.service;

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

@ExtendWith(MockitoExtension.class)
class GoogleServiceTest {
    @InjectMocks
    private GoogleService googleService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private Validation validation;

    @BeforeEach
    public void setUp() {

    }

    @Nested
    @DisplayName("GoogleLogin")
    class googleLogin {
        @Test
        @DisplayName("정상적인 로그인")
        void testGoogleLogin() throws JsonProcessingException {
//            // Given
//            String code = "test_auth_code";
//            String scope = "test_auth_scope";
//            HttpServletResponse response = mock(HttpServletResponse.class);
//            Map<String, String> accessTokenMap = new HashMap<>();
//            accessTokenMap.put("access_token", "test_access_token");
//            OauthUserInfoDto oauthUserInfoDto = new OauthUserInfoDto("12345", "test@example.com", "test_nickname");
//
//            GoogleService googleServiceSpy = spy(googleService);
//            doReturn(accessTokenMap).when(googleServiceSpy).getToken(code, scope);
//            doReturn(oauthUserInfoDto).when(googleServiceSpy).getGoogleUserInfo(accessTokenMap.get("access_token"));
//
//            when(userRepository.findByGoogleId(anyString())).thenReturn(Optional.empty());
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//            when(userRepository.save(any(User.class))).thenReturn(new User());
//            when(tokenProvider.generateTokenDto(any(User.class), any(UserRoleEnum.class))).thenReturn(new TokenDto());
//            doNothing().when(validation).tokenToHeaders(any(TokenDto.class), any(HttpServletResponse.class));
//
//            // When
//            googleServiceSpy.googleLogin(code, scope, response);
//
//            // Then
//            verify(googleServiceSpy, times(1)).getToken(code, scope);
//            verify(googleServiceSpy, times(1)).getGoogleUserInfo(accessTokenMap.get("access_token"));
//            verify(userRepository, times(1)).findByGoogleId(anyString());
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(userRepository, times(1)).save(any(User.class));
//            verify(tokenProvider, times(1)).generateTokenDto(any(User.class), any(UserRoleEnum.class));
//            verify(validation, times(1)).tokenToHeaders(any(TokenDto.class), any(HttpServletResponse.class));
        }


    }



}