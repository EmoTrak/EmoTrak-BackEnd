package com.example.emotrak.service;

import com.example.emotrak.dto.user.*;
import com.example.emotrak.entity.RefreshToken;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.exception.CustomException;
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
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    private User user;
    private User user2;
    private User loginUser;
    private NicknameRequestDto nicknameRequestDto;
    private SignupRequestDto signupRequestDto;
    private LoginRequestDto loginRequestDto;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private Validation validation;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        user = new User("qwer1234!", "user12312@gmail.com", "비둘기5", UserRoleEnum.USER);
        user2 = new User("qwer1234!", "user123567@naver.com", "비둘기야13", UserRoleEnum.USER);
        user2.setId(54L);
        nicknameRequestDto = new NicknameRequestDto("비둘비둘");

        signupRequestDto = new SignupRequestDto(user.getEmail(), user.getPassword(), user.getNickname());

        loginUser = new User("qqqqqq11111", "user12356789@naver.com", "비둘기야111", UserRoleEnum.USER);

        loginRequestDto = new LoginRequestDto(loginUser.getEmail(), loginUser.getPassword());

        RefreshToken refreshToken = new RefreshToken(54L,user2,"eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODIwNzQ0MjV9.U2DSWcYpGnVvjU8RSZFLG23168bV0qjLAkKxCHtYEF0",new Date());

    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

        @DisplayName("일반 회원 가입")
        @Test
        void signup() {
            //given
            assertEquals(user.getEmail().equals(""), false);
            assertEquals(user.getPassword().equals(""), false);
            assertEquals(user.getNickname().equals(""), false);

            when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
            when(userRepository.existsByNickname(user.getNickname())).thenReturn(false);

            //when
            userService.signup(signupRequestDto);
            //assertThrows(CustomException.class, () -> userService.signup(signupRequestDto));

            //then
            verify(userRepository, times(1)).saveAndFlush(Mockito.any(User.class));
        }

        @DisplayName("로그인")
        @Test
        void login() {
            //given
            HttpServletResponse response = mock(HttpServletResponse.class);

            String encodePassword = user.getPassword();
            //when
            when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.ofNullable(user));
            //when
            when(!passwordEncoder.matches(loginRequestDto.getPassword(), encodePassword)).thenReturn(true);
            //then
            userService.login(loginRequestDto, response);
        }
        @DisplayName("가입시 닉네임 체크")
        @Test
        void signupNicknameCheck() {

            CheckNicknameRequestDto checkNicknameRequestDto = new CheckNicknameRequestDto("비둘비둘");

//            boolean usercheck = user != null;
//            assertEquals("유저있음", usercheck);
//            boolean checknick = checkNicknameRequestDto.getNickname().equals(user.getNickname());
//            assertEquals("false", checknick);

            when(userRepository.existsByNickname(checkNicknameRequestDto.getNickname())).thenReturn(false);
            //when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

            userService.signupNicknameCheck(checkNicknameRequestDto, user);
        }

        @DisplayName("마이페이지 입장")
        @Test
        void userMypage() {

            //when
            when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user));

            userService.userMypage(user2);
        }
        @DisplayName("닉네임 변경")
        @Test
        public void nicknameUpdate() {
            //when
            when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            when(userRepository.existsByNickname(nicknameRequestDto.getNickname())).thenReturn(false);

            userService.nicknameUpdate(nicknameRequestDto,user2);
            //then
            verify(userRepository, times(1)).save(Mockito.any(User.class));
        }
        @DisplayName("비밀번호 변경")
        @Test
        public void passwordUpdate() {
            PasswordRequestDto passwordRequestDto = new PasswordRequestDto("qwerasdf12");
            //when
            when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            userService.passwordUpdate(passwordRequestDto,user2);

            //then
            verify(userRepository, times(1)).save(Mockito.any(User.class));
        }
//        @DisplayName("유저 회원탈퇴")
//        @Test
//        public void deleteUser() {
//
//        }
//
//        @DisplayName("토큰 재발급")
//        @Test
//        public void refreshToken() {
//            HttpServletRequest request = mock(HttpServletRequest.class);
//            HttpServletResponse response = mock(HttpServletResponse.class);
//
//            TokenDto tokenDto = tokenProvider.generateTokenDto(user2,UserRoleEnum.USER);
//
//            String refreshTokenValue = request.getHeader("Refresh-Token");
//            //then
//            verify(refreshTokenRepository,times(1)).save(Mockito.any(RefreshToken.class));
//
//            tokenProvider.validateToken(refreshTokenValue);
//
//            RefreshToken refreshToken = refreshTokenRepository.findByValue(refreshTokenValue).orElse(null);
//            //when
//            when(refreshTokenRepository.findByValue(refreshTokenValue)).thenReturn(Optional.ofNullable(refreshToken));
//            when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
//
//
//
//            userService.refreshToken(request,response);
//        }
    }
    @Nested
    @DisplayName("실패 케이스")
    class getDailyFail {
        @Test
        @DisplayName("닉네임 중복 체크")
        public void nicknameCheck() {
            signupRequestDto.setNickname("비둘기야밥먹자");
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signup(signupRequestDto);
                ;
            });
            // then
            assertEquals("중복된 닉네임이 존재합니다.", customException.getErrorCode().getMessage());
        }
    }
}