package com.example.emotrak.controller;

import com.example.emotrak.Service.DailyService;
import com.example.emotrak.dto.DailyRequestDto;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily")
public class DailyController {

    private final DailyService dailyService;
    @GetMapping("")
    public ResponseEntity getDailyMonth(@RequestBody DailyRequestDto dailyRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.successResponse(HttpStatus.OK, "조회 완료", dailyService.getDailyMonth(dailyRequestDto, userDetails.getUser()));
    }

    @GetMapping("/{dailyId}")
    public ResponseEntity getDailyDetail(@PathVariable Long dailyId) {
        return ResponseMessage.successResponse(HttpStatus.OK, "조회 완료", dailyService.getDailyDetail(dailyId));
    }

    @GetMapping("/test")
    public ResponseEntity getTest(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.successResponse(HttpStatus.OK, "조회 완료", dailyService.getTest(userDetails.getUser()));
    }
}
