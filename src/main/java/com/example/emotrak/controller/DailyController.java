package com.example.emotrak.controller;

import com.example.emotrak.Service.DailyService;
import com.example.emotrak.dto.DailyRequestDto;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily")
public class DailyController {

    private final DailyService dailyService;
    @GetMapping("")
    public ResponseEntity getDailyMonth(@RequestBody DailyRequestDto dailyRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.successResponse(HttpStatus.OK, "조회 완료", dailyService.getDailyMonth(dailyRequestDto, userDetails.getUser()));
    }
}
