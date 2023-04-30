package com.example.emotrak.controller;

import com.example.emotrak.exception.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusCheckController {

    @GetMapping("/health-check")
    public ResponseEntity<?> checkHealthStatus() {
        return ResponseMessage.successResponse(HttpStatus.OK, "health check 완료", null);
    }
}
