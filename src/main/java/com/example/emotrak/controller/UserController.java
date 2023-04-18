package com.example.emotrak.controller;

import com.example.emotrak.Service.UserService;
import com.example.emotrak.dto.*;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "일반 로그인 관련")
public class UserController {
    private final UserService userService;
    // 1. 회원 가입
    @Tag(name = "Users")
    @Operation(summary = "회원 가입", description = "회원 가입에 필요한 정보를 입력합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto signupRequestDto){
        userService.signup(signupRequestDto);
        return ResponseMessage.successResponse(HttpStatus.OK, "회원가입 완료", null);
    }
    @Tag(name = "Users")
    @Operation(summary = "이메일 체크", description = "회원 가입에 필요한 이메일을 체크합니다.")
    // 1-1. 회원 가입시 이메일 체크
    @PostMapping("/em-check")
    public ResponseEntity<?> signupEmailCheck(@RequestBody CheckEmailRequestDto checkEmailRequestDto){
        userService.signupEmailCheck(checkEmailRequestDto);
        return ResponseMessage.successResponse(HttpStatus.OK, "사용가능한 이메일 입니다.", null);

    }

    // 1-2. 회원 가입시 닉네임 체크
    @Tag(name = "Users")
    @Operation(summary = "닉네임 체크", description = "회원 가입에 필요한 닉네임을 체크합니다.")
    @PostMapping("/nick-check")
    public ResponseEntity<?> signupNicknameCheck(@RequestBody CheckNicknameRequestDto checkNicknameRequestDto){
        userService.signupNicknameCheck((checkNicknameRequestDto));
        return ResponseMessage.successResponse(HttpStatus.OK, "사용가능한 닉네임 입니다.", null);

    }

    // 2. 로그인
    @Tag(name = "Users")
    @Operation(summary = "일반 로그인", description = "가입된 회원 정보로 로그인을 합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
            userService.login(loginRequestDto, response);
            return ResponseMessage.successResponse(HttpStatus.OK, "로그인 완료", null);
    }
    // 3. 마이 페이지 입장
    @Tag(name = "Users")
    @Operation(summary = "유저 정보 수정 페이지", description = "마이페이지로 이동 합니다.")
    @GetMapping("/mypage")
    public ResponseEntity<?> userInfo(@ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage.successResponse(HttpStatus.OK,
                "마이 페이지 입장", userService.userMypage(userDetails.getUser()));
    }

    // 3-1. 닉네입 수정
    @Tag(name = "Users")
    @Operation(summary = "마이페이지 닉네임 수정", description = "마이페이지에서 닉네임을 수정합니다.")
    @PatchMapping("/nickname")
    public ResponseEntity<?> userNicknameUpdate(@RequestBody NicknameRequestDto nicknameRequestDto
            ,@ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.nicknameUpdate(nicknameRequestDto, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "닉네임 수정 완료", null);
    }

    // 3-2. 패스워드 수정
    @Tag(name = "Users")
    @Operation(summary = "마이페이지 패스워드 수정", description = "마이페이지에서 패스워드를 수정합니다.")
    @PatchMapping("/password")
    public ResponseEntity<?> userPasswordUpdate(@RequestBody PasswordRequestDto passwordRequestDto
            ,@ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.passwordUpdate(passwordRequestDto, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "패스워드 수정 완료", null);
    }

    // 4. 회원 탈퇴
    @Tag(name = "Users")
    @Operation(summary = "마이페이지 회원 탈퇴", description = "마이페이지에서 회원탈퇴 합니다.")
    @DeleteMapping ("/delete")
    public ResponseEntity<?> deleteUser(@ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        String accessToken = request.getHeader("Access_Token");
        userService.deleteUser(userDetails.getUser(),accessToken);
        return ResponseMessage.successResponse(HttpStatus.OK, "회원 탈퇴 완료", null);
    }

    // 5. 리프레시 토큰 발급
    @Tag(name = "Users")
    @Operation(summary = "토큰 재발급", description = "엑세스토큰이 만료되면 리프레시토큰으로 재발급 합니다.")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshTokenCheck(HttpServletRequest request, HttpServletResponse response){
        userService.refreshToken(request, response);
        return ResponseMessage.successResponse(HttpStatus.OK, "토큰 재발급 완료", null);
    }
}
