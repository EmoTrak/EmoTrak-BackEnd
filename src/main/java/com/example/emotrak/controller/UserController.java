package com.example.emotrak.controller;

import com.example.emotrak.Service.UserService;
import com.example.emotrak.dto.*;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    // 1. 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto signupRequestDto){
        userService.signup(signupRequestDto);
        return ResponseMessage.successResponse(HttpStatus.OK, "회원가입 완료", null);
    }

    // 1-1. 회원 가입시 이메일 체크
    @PostMapping("/em-check")
    public ResponseEntity<?> signupEmailCheck(@RequestBody CheckEmailRequestDto checkEmailRequestDto){
        userService.signupEmailCheck(checkEmailRequestDto);
        return ResponseMessage.successResponse(HttpStatus.OK, "사용가능한 이메일 입니다.", null);

    }
    // 1-2. 회원 가입시 닉네임 체크
    @PostMapping("/nick-check")
    public ResponseEntity<?> signupNicknameCheck(@RequestBody CheckNicknameRequestDto checkNicknameRequestDto){
        userService.signupNicknameCheck((checkNicknameRequestDto));
        return ResponseMessage.successResponse(HttpStatus.OK, "사용가능한 닉네임 입니다.", null);

    }

    //    2. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
            userService.login(loginRequestDto, response);
            return ResponseMessage.successResponse(HttpStatus.OK, "로그인 완료", null);
    }
    // 3. 마이 페이지 입장
    @GetMapping("/mypage")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage.successResponse(HttpStatus.OK,
                "마이 페이지 입장", userService.userMypage(userDetails.getUser()));
    }

    // 3-1. 닉네입 수정
    @PatchMapping("/nickname")
    public ResponseEntity<?> userNicknameUpdate(@RequestBody NicknameRequestDto nicknameRequestDto
            ,@AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.nicknameUpdate(nicknameRequestDto, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "닉네임 수정 완료", null);
    }

    // 3-2. 패스워드 수정
    @PatchMapping("/password")
    public ResponseEntity<?> userPasswordUpdate(@RequestBody PasswordRequestDto passwordRequestDto
            ,@AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.passwordUpdate(passwordRequestDto, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "패스워드 수정 완료", null);
    }

    // 5. 회원 탈퇴
    @DeleteMapping ("/delete")
    public ResponseEntity<?> userDelete(@AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.userDelete(userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "회원 탈퇴 완료", null);
    }
}
