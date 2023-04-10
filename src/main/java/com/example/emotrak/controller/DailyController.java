package com.example.emotrak.controller;

import com.example.emotrak.Service.DailyService;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily")
@Tag(name = "Daily", description = "달력 조회")
public class DailyController {

    private final DailyService dailyService;

    @Tag(name = "Daily")
    @Operation(summary = "달력 전체 조회", description = "월별 달력의 감정 전체를 조회합니다.")
    @GetMapping("")
    public ResponseEntity getDailyMonth(@Parameter(description = "년도", required = true) @RequestParam int year
                                      , @Parameter(description = "월", required = true) @RequestParam int month
                                      , @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.successResponse(HttpStatus.OK, "조회 완료", dailyService.getDailyMonth(year, month, userDetails.getUser()));
    }

    @Tag(name = "Daily")
    @Operation(summary = "달력 상세 조회", description = "달력의 일별 감정을 모두 조회합니다.")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(
                            name = "dailyId"
                            , value = "감정 글 번호"
                            , required = true
                            , dataType = "Long"
                            , paramType = "path"
                            , defaultValue = "28"
                    )
            })
    @GetMapping("/{dailyId}")
    public ResponseEntity getDailyDetail(@PathVariable Long dailyId, @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.successResponse(HttpStatus.OK, "조회 완료", dailyService.getDailyDetail(dailyId, userDetails.getUser()));
    }

}
