package com.example.emotrak.controller;

import com.example.emotrak.Service.GraphService;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@Tag(name = "Graph", description = "그래프")
public class GraphController {
    private final GraphService graphService;

    @Tag(name = "Graph")
    @Operation(summary = "그래프 조회", description = "막대그래프와 원그래프를 조회합니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "그래프 조회 완료", response = ResponseMessage.class )
    })
    @GetMapping("/graph")
    public ResponseEntity<?> graph(@Parameter(description = "년도", required = true)@RequestParam int year,
                                   @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage.successResponse(HttpStatus.OK, "그래프 성공", graphService.graph(year, userDetails.getUser()));
    }
}
