package com.example.emotrak.controller;

import com.example.emotrak.service.KakaoService;
import com.example.emotrak.exception.ResponseMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Oauth2", description = "소셜로그인")
public class KakaoController {
    private final KakaoService kakaoService;

    @Tag(name = "Oauth2")
    @Operation(summary = "카카오로그인", description = "소셜로그인")
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        kakaoService.kakaoLogin(code, response);
        return ResponseMessage.successResponse(HttpStatus.OK, "카카오 로그인 완료", null);
    }

}