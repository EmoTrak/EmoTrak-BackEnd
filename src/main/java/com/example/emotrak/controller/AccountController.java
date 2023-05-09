package com.example.emotrak.controller;

import com.example.emotrak.service.EmailSendService;
import com.example.emotrak.dto.user.CheckEmailRequestDto;
import com.example.emotrak.exception.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "Users", description = "일반 로그인 관련")
public class AccountController {

    private final EmailSendService emailSendService;
    @Tag(name = "Users")
    @Operation(summary = "이메일 컨펌 체크", description = "회원 가입에 필요한 이메일주소를 메일샌드로 체크합니다.")
    @PostMapping("/users/mail-confirm")
    @ResponseBody
    public ResponseEntity<?> mailConfirm(@RequestBody CheckEmailRequestDto checkEmailRequestDto) throws Exception {
        String code = emailSendService.sendSimpleMessage(checkEmailRequestDto);
//        log.info("인증코드 : " + code);
        return ResponseMessage.successResponse(HttpStatus.OK, "이메일 인증 번호", code);
    }
}