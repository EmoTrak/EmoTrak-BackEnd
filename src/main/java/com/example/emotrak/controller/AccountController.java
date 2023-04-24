package com.example.emotrak.controller;

import com.example.emotrak.Service.EmailSendService;
import com.example.emotrak.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final EmailSendService emailSendService;

    @PostMapping("/users/signup/mailConfirm")
    @ResponseBody
    public ResponseEntity<?> mailConfirm(@RequestParam String email) throws Exception {
        String code = emailSendService.sendSimpleMessage(email);
        log.info("인증코드 : " + code);
        return ResponseMessage.successResponse(HttpStatus.OK, "이메일 인증 번호", code);
    }
}