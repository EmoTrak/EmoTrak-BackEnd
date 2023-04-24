package com.example.emotrak.users.service;

import com.example.emotrak.Service.UserService;
import com.example.emotrak.dto.user.LoginRequestDto;
import com.example.emotrak.dto.user.SignupRequestDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.jwt.TokenProvider;
import com.example.emotrak.jwt.Validation;
import com.example.emotrak.repository.RefreshTokenRepository;
import com.example.emotrak.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.util.*;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    private User user;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private Validation validation;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        user = new User("qwer1234!", "user12312@gmail.com", "비둘기5", UserRoleEnum.USER);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

        @DisplayName("일반 회원 가입")
        @Test
        void signup() {
            //given
            SignupRequestDto requestDto = SignupRequestDto.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .nickname(user.getNickname())
                    .build();

            Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
            Mockito.when(userRepository.existsByNickname(user.getNickname())).thenReturn(false);
            //when
            userService.signup(requestDto);
            //then
            verify(userRepository, times(1)).saveAndFlush(Mockito.any(User.class));
        }

        @DisplayName("로그인")
        @Test
        void login() {
            //given
            HttpServletResponse response = mock(HttpServletResponse.class);

            User loginUser = new User("qqqqqq11111", "user12356789@naver.com", "비둘기야111", UserRoleEnum.USER);

            LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                    .email(loginUser.getEmail())
                    .password(loginUser.getPassword())
                    .build();

            String encodePassword = user.getPassword();
            //when
            Mockito.when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.ofNullable(user));
            //when
            Mockito.when(!passwordEncoder.matches(loginRequestDto.getPassword(), encodePassword)).thenReturn(true);
            //then
            userService.login(loginRequestDto, response);
        }

//        @DisplayName("리프레시토큰 저장 성공")
//        @Test
//        void refreshToken() {
//            //given
//            HttpServletResponse response = mock(HttpServletResponse.class);
//            LoginRequestDto loginRequestDto = LoginRequestDto.builder()
//                    .email("123@123.com")
//                    .password("Qwerqwer1")
//                    .build();
//            User saveUser = User.builder()
//                    .email("123@123.com")
//                    .password(passwordEncoder.encode("1234"))
//                    .nickname("루피")
//                    .role(UserRoleEnum.USER)
//                    .build();
//
//
//            TokenDto tokenDto = TokenDto.builder()
//                    .accessToken("fake")
//                    .refreshToken("fakeRefresh")
//                    .build();
//
//            RefreshToken refreshToken = RefreshToken.builder()
//                    .value(tokenDto.getRefreshToken())
//                    .user(saveUser)
//                    .build();
//            Mockito.when(userRepository.findByEmail("123@123.com")).thenReturn(Optional.of(saveUser));
//            Mockito.when(tokenProvider.generateTokenDto(saveUser,UserRoleEnum.USER)).thenReturn(tokenDto);
//            Mockito.when(refreshTokenRepository.findByValue("fakeRefresh")).thenReturn(Optional.of(refreshToken));
//            Mockito.when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when

        //then
//            assertThat(saveUser).isNotNull();
//            assertThat("로그인 성공").isEqualTo(result);

//            //???????????????
//            ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
//            Mockito.verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
//            RefreshToken updatedRefreshToken = refreshTokenCaptor.getValue();
//
//            assertThat(updatedRefreshToken).isNotNull();
//            assertThat(updatedRefreshToken.getEmail()).isEqualTo(refreshToken.getEmail());
//            assertThat(updatedRefreshToken.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());

    }

//        @DisplayName("새로운 엑세스토큰 발급 성공")
//        @Test
//        void newAccessToken() {
//            //given
//            HttpServletRequest request = mock(HttpServletRequest.class);
//            HttpServletResponse response = mock(HttpServletResponse.class);
//            String refreshToken = "fakeRefresh";
//            String email = "123@123";
//            String newAccessToken = "fakeNewAccess";
////
//            User saveUser = User.builder()
//                    .email("123@123")
//                    .password(passwordEncoder.encode("1234"))
//                    .nickname("루피")
//                    .role(UserRoleEnum.USER)
//                    .build();
//
//            TokenDto tokenDto = TokenDto.builder()
//                    .accessToken(newAccessToken)
//                    .refreshToken(refreshToken)
//                    .build();
//
//            when(tokenProvider.generateAccessTokenDto(saveUser, UserRoleEnum.USER)).thenReturn(tokenDto);
//            when(tokenProvider.validateToken(newAccessToken)).thenReturn(false);
//
//            //when
//            userService.refreshToken(request, response);
//
//            //then
//            assertThat(newAccessToken).isNotNull();
//
//    }
}