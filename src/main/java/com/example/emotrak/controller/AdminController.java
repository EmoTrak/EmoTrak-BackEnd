package com.example.emotrak.controller;

import com.example.emotrak.Service.AdminService;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin/boards")
    public ResponseEntity<?> reportBoard(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage.successResponse(HttpStatus.OK, "신고 게시물 조회 완료", adminService.reportBoard(userDetails.getUser()));
    }
    @GetMapping("/admin/comments")
    public ResponseEntity<?> reportComment(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage.successResponse(HttpStatus.OK, "신고 댓글 조회 완료", adminService.reportComment(userDetails.getUser()));
    }
}
