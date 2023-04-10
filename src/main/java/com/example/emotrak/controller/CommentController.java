package com.example.emotrak.controller;

import com.example.emotrak.Service.CommentService;
import com.example.emotrak.dto.CommentRequestDto;
import com.example.emotrak.dto.ReportRequestDto;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long boardId,
                                           @RequestBody CommentRequestDto commentRequestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.createComment(boardId, commentRequestDto, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.CREATED, "댓글 작성 성공", null);
    }

    // 댓글 수정
    @PatchMapping ("/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @RequestBody CommentRequestDto commentRequestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.updateComment(commentId, commentRequestDto, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "댓글 수정 성공", null);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "댓글 삭제 성공", null);
    }

    //댓글 신고하기
    @PostMapping("/comments/report/{commentId}")
    public ResponseEntity<?> reportBoard(@PathVariable Long commentId,
                                         @RequestBody ReportRequestDto reportRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 게시글 신고 처리
        commentService.createReport(reportRequestDto, userDetails.getUser(), commentId);
        return ResponseMessage.successResponse(HttpStatus.CREATED, "댓글 신고 성공", null);
    }

    //댓글 신고 삭제하기
    @DeleteMapping("/comments/report/{commentId}")
    public ResponseEntity<?> deleteReport(@PathVariable Long commentId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteReport(userDetails.getUser(), commentId);
        return ResponseMessage.successResponse(HttpStatus.OK, "댓글 신고 삭제 성공", null);
    }

        //댓글 좋아요 (좋아요와 취소 번갈아가며 진행)
        @PostMapping("/comments/likes/{commentId}")
        public ResponseEntity<?> commentlikes(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
            Map<String, Object> response = commentService.commentlikes(userDetails.getUser(), commentId);
            String message = (String) response.get("message");
            return ResponseMessage.successResponse(HttpStatus.OK, message, null);
        }

}