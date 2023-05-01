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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    private String testCode;
    private String testAccessToken;
    private OauthUserInfoDto testOauthUserInfo;
    private User testUser;

    @BeforeEach
    void setUp() {
        testCode = "test_code";
        testAccessToken = "test_access_token";
        testOauthUserInfo = new OauthUserInfoDto("12345", "bambee@gmail.com", "bambee");
        testUser = new User("encoded_password", "bambee@gmail.com", "bambee",
                12345L, null, null, UserRoleEnum.USER);
    }


    @Test
    void kakaoLoginTest() throws JsonProcessingException {
//        // given
//        when(kakaoService.getToken(testCode)).thenReturn(testAccessToken);
//        when(kakaoService.getKakaoUserInfo(testAccessToken)).thenReturn(testOauthUserInfo);
//        when(userRepository.findByKakaoId(Long.parseLong(testOauthUserInfo.getId()))).thenReturn(Optional.empty());
//        when(userRepository.findByEmail(testOauthUserInfo.getEmail())).thenReturn(Optional.of(testUser));
//        when(tokenProvider.generateTokenDto(testUser, testUser.getRole())).thenReturn(new TokenDto("test_token", "Bearer", 3600L));
//
//        // when
//        kakaoService.kakaoLogin(testCode, mock(HttpServletResponse.class));
//
//        // then
//        verify(userRepository).findByKakaoId(Long.parseLong(testOauthUserInfo.getId()));
//        verify(userRepository).findByEmail(testOauthUserInfo.getEmail());
//        verify(userRepository, times(0)).save(any(User.class));
//        verify(tokenProvider).generateTokenDto(testUser, testUser.getRole());
//        verify(validation).tokenToHeaders(any(TokenDto.class), any(HttpServletResponse.class));
    }

}