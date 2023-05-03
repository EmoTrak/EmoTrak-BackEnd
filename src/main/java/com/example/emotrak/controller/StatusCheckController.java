package com.example.emotrak.controller;

import com.example.emotrak.exception.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "healthCheck", description = "health check")
public class StatusCheckController {

    @Tag(name = "healthCheck")
    @Operation(summary = "통신 체크", description = "통신을 체크합니다.")
    @GetMapping("/health-check")
    public ResponseEntity<?> checkHealthStatus() {
        return ResponseMessage.successResponse(HttpStatus.OK, "health check 완료", null);
    }
}
