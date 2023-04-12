package com.example.emotrak.controller;

import com.example.emotrak.Service.NaverService;
import com.example.emotrak.exception.ResponseMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class NaverController {

    private final NaverService naverService;

    @GetMapping("/naver/callback")
    public ResponseEntity<?> naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException {
        naverService.naverLogin(code,state,response);
        return ResponseMessage.successResponse(HttpStatus.OK, "네이버 로그인 완료", null);
    }

}
