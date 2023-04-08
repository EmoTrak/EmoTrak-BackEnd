package com.example.emotrak.controller;

import com.example.emotrak.Service.GraphService;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GraphController {
    private final GraphService graphService;

    @GetMapping("/graph")
    public ResponseEntity<?> graph(@RequestParam int year, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage.successResponse(HttpStatus.OK, "그래프 성공", graphService.graph(year, userDetails.getUser()));
    }
}
