package com.example.emotrak.controller;

import com.example.emotrak.Service.UserService;
import com.example.emotrak.dto.CheckEmailRequestDto;
import com.example.emotrak.dto.LoginRequestDto;
import com.example.emotrak.dto.SecurityExceptionDto;
import com.example.emotrak.dto.SignupRequestDto;
import com.example.emotrak.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    // 1. 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto signupRequestDto){
        userService.signup(signupRequestDto);
        return ResponseMessage.successResponse(HttpStatus.OK, "회원가입 완료", null);
    }
    // 1-1. 회원 가입시 이메일 체크
    @PostMapping("/signup/check-email")
    public ResponseEntity<?> signupEmailCheck(@Valid @RequestBody CheckEmailRequestDto checkEmailRequestDto){
        userService.signupEmailCheck(checkEmailRequestDto);
        return ResponseMessage.successResponse(HttpStatus.OK, "사용가능한 이메일 입니다.", null);

    }

    //    2. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        userService.login(loginRequestDto, response);
        return ResponseMessage.successResponse(HttpStatus.OK, "로그인 완료", null);

    }
}
