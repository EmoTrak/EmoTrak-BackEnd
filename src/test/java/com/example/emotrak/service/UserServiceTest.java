package com.example.emotrak.service;

import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.*;
import com.example.emotrak.service.UserService;
import com.example.emotrak.dto.user.LoginRequestDto;
import com.example.emotrak.dto.user.SignupRequestDto;
import com.example.emotrak.dto.user.*;
import com.example.emotrak.entity.RefreshToken;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.jwt.TokenProvider;
import com.example.emotrak.jwt.Validation;
import com.example.emotrak.repository.*;
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
    private CheckNicknameRequestDto checkNicknameRequestDto;
    private PasswordRequestDto passwordRequestDto;
    private HttpServletResponse response;
    private HttpServletRequest request;

    private RefreshToken refreshToken;

    private TokenDto tokenDto;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private LikesRepository likesRepository;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private KakaoService kakaoService;
    @Mock
    private NaverService naverService;
    @Mock
    private GoogleService googleService;
    @Mock
    private FileUploadService fileUploadService;
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
        loginUser = new User("qqqqqq11111", "user12356789@naver.com", "비둘기야111", UserRoleEnum.USER);

        nicknameRequestDto = new NicknameRequestDto("비둘비둘");
        signupRequestDto = new SignupRequestDto(user.getEmail(), user.getPassword(), user.getNickname());
        response = mock(HttpServletResponse.class);
        request = mock(HttpServletRequest.class);
        loginRequestDto = new LoginRequestDto(loginUser.getEmail(), loginUser.getPassword());

        checkNicknameRequestDto = new CheckNicknameRequestDto("비둘기야13");
        passwordRequestDto = new PasswordRequestDto("qwer1234");
        refreshToken = new RefreshToken(54L,user2,"eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODIwNzQ0MjV9.U2DSWcYpGnVvjU8RSZFLG23168bV0qjLAkKxCHtYEF0",new Date(999999999));
        tokenDto = new TokenDto("BEARER_PREFIX","2354235","eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODIwNzQ0MjV9.U2DSWcYpGnVvjU8RSZFLG23168bV0qjLAkKxCHtYEF0",99999999999L);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

        @DisplayName("일반 회원 가입")
        @Test
        void signup() {
            //when
            when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
            when(userRepository.existsByNickname(user.getNickname())).thenReturn(false);

            //then
            userService.signup(signupRequestDto);
            verify(userRepository, times(1)).saveAndFlush(Mockito.any(User.class));
        }

        @DisplayName("로그인")
        @Test
        void login() {
            //given
            String encodePassword = user.getPassword();

            //when
            when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.ofNullable(user));
            when(!passwordEncoder.matches(loginRequestDto.getPassword(), encodePassword)).thenReturn(true);

            //then
            userService.login(loginRequestDto, response);
        }
        @DisplayName("가입시 닉네임 체크")
        @Test
        void signupNicknameCheck() {
            //given
            CheckNicknameRequestDto checkNicknameRequestDto = new CheckNicknameRequestDto("비둘비둘");

            //when
            when(userRepository.existsByNickname(checkNicknameRequestDto.getNickname())).thenReturn(false);

            //then
            userService.signupNicknameCheck(checkNicknameRequestDto, user);
        }

        @DisplayName("마이페이지 입장")
        @Test
        void userMypage() {
            //when
            when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user));

            //then
            userService.userMypage(user2);
        }
        @DisplayName("닉네임 변경")
        @Test
        public void nicknameUpdate() {
            //when
            when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            when(userRepository.existsByNickname(nicknameRequestDto.getNickname())).thenReturn(false);

            //then
            userService.nicknameUpdate(nicknameRequestDto,user2);
            verify(userRepository, times(1)).save(Mockito.any(User.class));
        }
        @DisplayName("비밀번호 변경")
        @Test
        public void passwordUpdate() {
            //given
            PasswordRequestDto passwordRequestDto = new PasswordRequestDto("qwerasdf12");

            //when
            when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            userService.passwordUpdate(passwordRequestDto,user2);

            //then
            verify(userRepository, times(1)).save(Mockito.any(User.class));
        }

//        @DisplayName("토큰 재발급")
//        @Test
//        public void refreshTokenAccess() {
//            request = mock(HttpServletRequest.class);
//            response = mock(HttpServletResponse.class);
//            //when
//            response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
//            response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
//            response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
//
//            when(tokenProvider.generateTokenDto(user2,UserRoleEnum.USER)).thenReturn(tokenDto);
//            when(refreshTokenRepository.findByValue(tokenDto.getRefreshToken())).thenReturn(Optional.ofNullable(refreshToken));
//            when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
//            //then
//            userService.refreshToken(request,response);
//            verify(refreshTokenRepository,times(1)).save(Mockito.any(RefreshToken.class));
//
//        }
    }
    @Nested
    @DisplayName("실패 케이스")
    class getUsersFail {
        @Test
        @DisplayName("이메일 공백 체크")
        public void emailEmptyCheck() {
            //give
            signupRequestDto.setEmail("");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signup(signupRequestDto);
            });

            // then
            assertEquals("이메일을 입력해주세요.", customException.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("패스워드 공백 체크")
        public void passwordEmptyCheck() {
            //give
            signupRequestDto.setPassword("");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signup(signupRequestDto);
            });

            // then
            assertEquals("비밀번호를 입력해주세요.", customException.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("닉네임 공백 체크")
        public void nicknameEmptyCheck() {
            //give
            signupRequestDto.setNickname("");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signup(signupRequestDto);
            });

            // then
            assertEquals("닉네임을 입력해주세요.", customException.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("이메일 중복 됨")
        public void emailCheck() {
            //give
            signupRequestDto.setEmail("user123567@naver.com");

            //when
            Mockito.when(userRepository.existsByEmail(signupRequestDto.getEmail())).thenReturn(true);
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signup(signupRequestDto);
            });

            // then
            assertEquals("중복된 이메일이 존재합니다.", customException.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("닉네임 중복 됨")
        public void nicknameCheck() {
            //give
            signupRequestDto.setNickname("비둘기야밥먹자");

            //when
            Mockito.when(userRepository.existsByNickname(signupRequestDto.getNickname())).thenReturn(true);
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signup(signupRequestDto);
            });

            // then
            assertEquals("중복된 닉네임이 존재합니다.", customException.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("이메일 형식 틀림")
        public void emailPatternCheck() {
            //give
            signupRequestDto.setEmail("qwer1234");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signup(signupRequestDto);
            });

            // then
            assertEquals("올바른 이메일 형식이 아닙니다.", customException.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("닉네임 형식 틀림")
        public void nicknamePatternCheck() {
            //give
            signupRequestDto.setNickname("qwer_1234");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signup(signupRequestDto);
            });

            // then
            assertEquals("닉네임 조건을 확인해주세요.", customException.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("패스워드 형식 틀림")
        public void passwordPatternCheck() {
            //give
            signupRequestDto.setPassword("1234");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signup(signupRequestDto);
            });

            // then
            assertEquals("비밀번호 조건을 확인해주세요.", customException.getErrorCode().getMessage());
        }

        @DisplayName("로그인 이메일 공백")
        @Test
        void loginFailEmailBlank() {
            //given
            loginRequestDto.setEmail("");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.login(loginRequestDto,response);
            });

            //then
            assertEquals("이메일을 입력해주세요.", customException.getErrorCode().getMessage());
        }

        @DisplayName("로그인 패스워드 공백")
        @Test
        void loginFailPasswordBlank() {
            //given
            loginRequestDto.setPassword("");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.login(loginRequestDto,response);
            });

            //then
            assertEquals("비밀번호를 입력해주세요.", customException.getErrorCode().getMessage());
        }

        @DisplayName("로그인 유저정보 없음")
        @Test
        void loginFailUserNotFound() {
            //given
            loginRequestDto.setEmail("qwerqq32@naver.com");
            loginRequestDto.setPassword("qwer1234");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.login(loginRequestDto,response);
            });

            //then
            assertEquals("등록된 사용자가 없습니다.", customException.getErrorCode().getMessage());
        }

        @DisplayName("로그인 패스워드 불일치")
        @Test
        void loginFailPassword() {
            //given
            loginRequestDto.setEmail(user2.getEmail());
            loginRequestDto.setPassword("qwer1234");

            //when
            Mockito.when(userRepository.findByEmail(user2.getEmail())).thenReturn(Optional.ofNullable(user2));
            Mockito.when(!passwordEncoder.matches(loginRequestDto.getPassword(),user2.getPassword())).thenReturn(false);
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.login(loginRequestDto,response);
            });

            //then
            assertEquals("비밀번호가 일치하지 않습니다.", customException.getErrorCode().getMessage());
        }

        @DisplayName("회원가입 닉네임 체크 닉네임 공백")
        @Test
        void signupNicknameCheckFail() {
            //given
            checkNicknameRequestDto.setNickname("");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signupNicknameCheck(checkNicknameRequestDto,user);
            });

            //then
            assertEquals("닉네임을 입력해주세요.", customException.getErrorCode().getMessage());
        }

        @DisplayName("회원가입 닉네임 체크 닉네임 동일 시")
        @Test
        void signupNicknameCheckFailNicknameSame() {
            //given
            checkNicknameRequestDto.setNickname(user.getNickname());

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signupNicknameCheck(checkNicknameRequestDto,user);
            });

            //then
            assertEquals("닉네임이 동일합니다.", customException.getErrorCode().getMessage());
        }

        @DisplayName("회원가입 닉네임 체크 중복 닉네임 존재")
        @Test
        void signupNicknameCheckFailNicknameDuple() {
            //given
            checkNicknameRequestDto.setNickname("비둘기야밥먹자");

            //when
            Mockito.when(userRepository.existsByNickname(checkNicknameRequestDto.getNickname())).thenReturn(true);
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signupNicknameCheck(checkNicknameRequestDto,user);
            });

            //then
            assertEquals("중복된 닉네임이 존재합니다.", customException.getErrorCode().getMessage());
        }

        @DisplayName("회원가입 닉네임 체크 닉네임 형식이 불일치")
        @Test
        void signupNicknameCheckFailNicknameNotPattern() {
            //given
            checkNicknameRequestDto.setNickname("qwer_1234");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.signupNicknameCheck(checkNicknameRequestDto,user);
            });

            //then
            assertEquals("닉네임 조건을 확인해주세요.", customException.getErrorCode().getMessage());
        }

        @DisplayName("마이 페이지 유저정보 없음")
        @Test
        void myPageFailUserNotFound() {
            //given
            user.setId(9999L);

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.userMypage(user);
            });

            //then
            assertEquals("등록된 사용자가 없습니다.", customException.getErrorCode().getMessage());
        }

        @DisplayName("닉네임 변경 유저정보 없음")
        @Test
        void nicknameUpdateFailUserNotFound() {
            //given
            user.setId(9999L);

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.nicknameUpdate(nicknameRequestDto,user2);
            });

            //then
            assertEquals("등록된 사용자가 없습니다.", customException.getErrorCode().getMessage());
        }

        @DisplayName("닉네임 변경 닉네임 공백")
        @Test
        void nicknameUpdateFailNicknameBlank() {
            //given
            nicknameRequestDto.setNickname("");

            //when
            Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.nicknameUpdate(nicknameRequestDto,user2);
            });

            //then
            assertEquals("닉네임을 입력해주세요.", customException.getErrorCode().getMessage());
        }

        @DisplayName("닉네임 변경 닉네임이 현재와 같을 시")
        @Test
        void nicknameUpdateFailNicknameSame() {
            //given
            nicknameRequestDto.setNickname(user2.getNickname());

            //when
            Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.nicknameUpdate(nicknameRequestDto,user2);
            });

            //then
            assertEquals("닉네임이 동일합니다.", customException.getErrorCode().getMessage());
        }

        @DisplayName("닉네임 변경 닉네임이 중복 시")
        @Test
        void nicknameUpdateFailNicknameDuple() {
            //given
            nicknameRequestDto.setNickname("비둘기야밥먹자");

            //when
            Mockito.when(userRepository.existsByNickname(nicknameRequestDto.getNickname())).thenReturn(true);
            Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.nicknameUpdate(nicknameRequestDto,user2);
            });

            //then
            assertEquals("중복된 닉네임이 존재합니다.", customException.getErrorCode().getMessage());
        }

        @DisplayName("닉네임 변경 닉네임 형식이 불일치")
        @Test
        void nicknameUpdateFailNicknameNotPattern() {
            //given
            nicknameRequestDto.setNickname("qwer_1234");

            //when
            Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.nicknameUpdate(nicknameRequestDto,user2);
            });

            //then
            assertEquals("닉네임 조건을 확인해주세요.", customException.getErrorCode().getMessage());
        }

        @DisplayName("패스워드 변경 유저정보 없음")
        @Test
        void passwordUpdateFailUserNotFound() {
            //given
            user2.setId(9999L);

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.passwordUpdate(passwordRequestDto,user2);
            });

            //then
            assertEquals("등록된 사용자가 없습니다.", customException.getErrorCode().getMessage());
        }

        @DisplayName("패스워드 변경 패스워드 공백")
        @Test
        void passwordUpdateFailPasswordBlank() {
            //given
            passwordRequestDto.setPassword("");

            //when
            Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.passwordUpdate(passwordRequestDto,user2);
            });

            //then
            assertEquals("비밀번호를 입력해주세요.", customException.getErrorCode().getMessage());
        }

        @DisplayName("패스워드 변경 패스워드 현재와 동일")
        @Test
        void passwordUpdateFailPasswordSame() {
            //given
            passwordRequestDto.setPassword(user2.getPassword());

            //when
            Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            Mockito.when(!passwordEncoder.matches(passwordRequestDto.getPassword(),user2.getPassword())).thenReturn(true);
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.passwordUpdate(passwordRequestDto,user2);
            });

            //then
            assertEquals("패스워드가 동일합니다.", customException.getErrorCode().getMessage());
        }

        @DisplayName("패스워드 변경 패스워드 형식 불일치")
        @Test
        void passwordUpdateFailPasswordNotPattern() {
            //given
            passwordRequestDto.setPassword("qwer");

            //when
            Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
            //Mockito.when(!passwordEncoder.matches(passwordRequestDto.getPassword(),user2.getPassword())).thenReturn(true);
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.passwordUpdate(passwordRequestDto,user2);
            });

            //then
            assertEquals("비밀번호 조건을 확인해주세요.", customException.getErrorCode().getMessage());
        }

        @DisplayName("회원탈퇴 유저정보 없음")
        @Test
        void deleteUserFailUserNotFound() {
            //given
            user2.setId(9999L);
            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                userService.deleteUser(user2);
            });
            //then
            assertEquals("등록된 사용자가 없습니다.", customException.getErrorCode().getMessage());
        }

    }
//    }
    @Nested
    @DisplayName("회원 탈퇴")
    class DeleteUser {
        @Nested
        @DisplayName("성공 케이스")
        class deleteUserSuccess {
            @Test
            @DisplayName("일반 회원")
            public void a_DeleteUser() {
                when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

                // when
                userService.deleteUser(user);

                // then
                verify(userRepository, times(1)).findById(user.getId());
                verify(kakaoService, times(0)).unlinkKakao(user);
                verify(naverService, times(0)).unlinkNaver(user);
                verify(googleService, times(0)).unlinkGoogle(user);
                verify(likesRepository, times(1)).deleteAllByUser(user.getId());
                verify(reportRepository, times(1)).deleteAllByUser(user.getId());
                verify(likesRepository, times(1)).deleteCommentLikeByUser(user.getId());
                verify(reportRepository, times(1)).deleteCommentLikeByUser(user.getId());
                verify(commentRepository, times(1)).deleteAllByUser(user.getId());
                verify(likesRepository, times(1)).deleteByUser(user.getId());
                verify(likesRepository, times(1)).deleteByUserComment(user.getId());
                verify(reportRepository, times(1)).deleteByUser(user.getId());
                verify(reportRepository, times(1)).deleteByUserComment(user.getId());
                verify(commentRepository, times(1)).deleteByUser(user.getId());
                verify(boardRepository, times(1)).findImgUrlByUser(user);
                verify(fileUploadService, times(0)).deleteFiles(anyList());
                verify(boardRepository, times(1)).deleteAllByUser(user.getId());
                verify(refreshTokenRepository, times(1)).deleteByUser(user);
                verify(userRepository, times(1)).delete(user);
            }

            @Test
            @DisplayName("소셜 회원")
            public void b_DeleteSocialUser() {
                user.setHasSocial(true);
                user.setKakaoId(1L);
                user.setNaverId("Naver");
                user.setGoogleId("Google");

                when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

                // when
                userService.deleteUser(user);

                // then
                verify(kakaoService, times(1)).unlinkKakao(user);
                verify(naverService, times(1)).unlinkNaver(user);
                verify(googleService, times(1)).unlinkGoogle(user);
            }

            @Test
            @DisplayName("파일 등록 회원")
            public void c_DeleteFileUser() {
                List<String> imgList = new ArrayList<>();
                imgList.add("img1");
                imgList.add("img2");

                when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
                when(boardRepository.findImgUrlByUser(user)).thenReturn(imgList);

                // when
                userService.deleteUser(user);

                // then
                verify(fileUploadService, times(1)).deleteFiles(anyList());
            }
        }
        @Nested
        @DisplayName("실패 케이스")
        class DeleteUserFail {
            @Test
            @DisplayName("탈퇴 유저가 없음")
            public void a_DeleteUserFail() {
                CustomException customException = assertThrows(CustomException.class, () -> {
                    userService.deleteUser(user);
                });

                assertEquals("등록된 사용자가 없습니다.", customException.getErrorCode().getMessage());
            }
        }
    }

}