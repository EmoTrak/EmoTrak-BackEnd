package com.example.emotrak.controller;

import com.example.emotrak.Service.UserService;
import com.example.emotrak.dto.CheckEmailRequestDto;
import com.example.emotrak.dto.LoginRequestDto;
import com.example.emotrak.dto.SignupRequestDto;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.example.emotrak.exception.CustomExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

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

    //    2. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
            userService.login(loginRequestDto, response);
            return ResponseMessage.successResponse(HttpStatus.OK, "로그인 완료", null);
    }
}
