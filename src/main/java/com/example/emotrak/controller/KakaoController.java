package com.example.emotrak.controller;

import com.example.emotrak.Service.KakaoService;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    @Tag(name = "Oauth2")
    @Operation(summary = "카카오 연동 해제", description = "소셜 연동 해제")
    @PostMapping("/kakao/unlink")
    public ResponseEntity<?> kakaoUnlink(@ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails,
                                         HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        kakaoService.unlinkKakaoAccount(userDetails.getUser(), accessToken);
        return ResponseMessage.successResponse(HttpStatus.OK, "카카오 연동 해제 완료", null);
    }

}