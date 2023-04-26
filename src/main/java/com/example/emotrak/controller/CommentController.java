package com.example.emotrak.controller;

import com.example.emotrak.Service.CommentService;
import com.example.emotrak.dto.comment.CommentRequestDto;
import com.example.emotrak.dto.like.LikeResponseDto;
import com.example.emotrak.dto.report.ReportRequestDto;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Tag(name = "Comment", description = "댓글")
@Tag(name = "Report", description = "신고합니다")
@Tag(name = "Likes", description = "좋아합니다")
public class CommentController {

    private final CommentService commentService;

    @Tag(name = "Comment")
    @Operation(summary = "댓글 작성", description = "댓글 작성 성공")
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long boardId,
                                           @RequestBody CommentRequestDto commentRequestDto,
                                           @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.createComment(boardId, commentRequestDto, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.CREATED, "댓글 작성 성공", null);
    }

    @Tag(name = "Comment")
    @Operation(summary = "댓글 수정", description = "댓글 수정 성공")
    @PatchMapping ("/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @RequestBody CommentRequestDto commentRequestDto,
                                           @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.updateComment(commentId, commentRequestDto, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "댓글 수정 성공", null);
    }

    @Tag(name = "Comment")
    @Operation(summary = "댓글 삭제", description = "댓글 삭제 성공")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                           @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "댓글 삭제 성공", null);
    }

    @Tag(name = "Report")
    @Operation(summary = "댓글 신고하기", description = "댓글 신고 성공")
    @PostMapping("/comments/report/{commentId}")
    public ResponseEntity<?> reportBoard(@PathVariable Long commentId,
                                         @RequestBody ReportRequestDto reportRequestDto,
                                         @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 게시글 신고 처리
        commentService.createReport(reportRequestDto, userDetails.getUser(), commentId);
        return ResponseMessage.successResponse(HttpStatus.CREATED, "댓글 신고 성공", null);
    }

    @Tag(name = "Likes")
    @Operation(summary = "댓글 좋아요", description = "좋아요와 취소 번갈아가며 진행")
    @PostMapping("/comments/likes/{commentId}")
    public ResponseEntity<?> commentLikes(@PathVariable Long commentId,
                                          @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        LikeResponseDto likeResponseDto = commentService.commentLikes(userDetails.getUser(), commentId);
        String message = likeResponseDto.isHasLike() ? "좋아요 성공" : "좋아요 취소 성공";
        return ResponseMessage.successResponse(HttpStatus.OK, message, likeResponseDto);
    }

}