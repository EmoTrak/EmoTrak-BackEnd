package com.example.emotrak.controller;

import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ExampleController {

    // 리턴 예시
    @PostMapping("/users")
    public ResponseEntity example() {
        return ResponseMessage.successResponse(HttpStatus.CREATED, "작성 완료", null);
    }

    // 예외처리 예시
    @GetMapping("/users")
    public ResponseEntity example2() {
        throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
    }

    // 예외처리 예시

    @PostMapping("/post")
    public ResponseEntity example3() {
        return ResponseMessage.successResponse(HttpStatus.OK, "작성 완료", null);
    }
}
