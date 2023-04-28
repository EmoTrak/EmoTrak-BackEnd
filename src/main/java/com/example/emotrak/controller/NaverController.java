package com.example.emotrak.controller;

import com.example.emotrak.Service.NaverService;
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
@Tag(name = "Oauth2", description = "소셜 로그인")
public class NaverController {

    private final NaverService naverService;

    @Tag(name = "Oauth2")
    @Operation(summary = "네이버로그인", description = "소셜 로그인")
    @GetMapping("/naver/callback")
    public ResponseEntity<?> naverLogin(@RequestParam String code,
                                        @RequestParam String state, HttpServletResponse response) throws JsonProcessingException {
        naverService.naverLogin(code,state,response);
        return ResponseMessage.successResponse(HttpStatus.OK, "네이버 로그인 완료", null);
    }

}
