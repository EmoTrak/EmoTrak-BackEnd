package com.example.emotrak.controller;

import com.example.emotrak.Service.KakaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class KakaoController {
//    카카오 로그인을 위한 컨트롤러

    private final KakaoService kakaoService;

    //    카카오 로그인 반환값 string redirect로 할지 어떻게 할지 좀 더 고민
    @GetMapping("/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        //kakaoService.kakaoLogin(code, response);
        // Cookie 생성 및 직접 브라우저에 Set
//        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
////        cookie.setDomain("http://clone-thunder-market.s3-website.ap-northeast-2.amazonaws.com");
//        response.addCookie(cookie);
        return "카카로 로그인 성공";

    }
}