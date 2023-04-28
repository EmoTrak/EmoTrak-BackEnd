package com.example.emotrak.controller;

import com.example.emotrak.service.GoogleService;
import com.example.emotrak.exception.ResponseMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Oauth2", description = "소셜로그인")
public class GoogleController {

    private final GoogleService googleService;

    @Tag(name = "Oauth2")
    @Operation(summary = "구글 로그인", description = "소셜로그인")
    @GetMapping("/google/callback")
    public ResponseEntity<?> googleLogin(@RequestParam String code,
                                         @RequestParam String scope,
                                         @RequestParam String offline,
                                         HttpServletResponse response) throws JsonProcessingException {
        googleService.googleLogin(code, scope, offline, response);
        return ResponseMessage.successResponse(HttpStatus.OK, "구글 로그인 완료", null);
    }

}