package com.example.emotrak.controller;

import com.example.emotrak.service.AdminService;
import com.example.emotrak.exception.ResponseMessage;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자")
public class AdminController {

    private final AdminService adminService;

    @Tag(name = "Admin")
    @Operation(summary = "신고 게시물 조회", description = "신고한 게시물들을 조회합니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "신고 게시물 조회 완료", response = ResponseMessage.class ),
            @ApiResponse(code = 403, message = "권한이 없습니다", response = ResponseMessage.class )
    })
    @GetMapping("/admin/boards")
    public ResponseEntity<?> reportBoard(@RequestParam(value = "page", defaultValue = "1") int page) {
        return ResponseMessage.successResponse(HttpStatus.OK, "신고 게시물 조회 완료", adminService.reportBoard(page));
    }

    @Tag(name = "Admin")
    @Operation(summary = "신고 댓글 조회", description = "신고한 댓글들을 조회합니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "신고 댓글 조회 완료", response = ResponseMessage.class ),
            @ApiResponse(code = 403, message = "권한이 없습니다", response = ResponseMessage.class )
    })
    @GetMapping("/admin/comments")
    public ResponseEntity<?> reportComment(@RequestParam(value = "page", defaultValue = "1")int page){
        return ResponseMessage.successResponse(HttpStatus.OK, "신고 댓글 조회 완료", adminService.reportComment(page));
    }

    @Tag(name = "Admin")
    @Operation(summary = "공유 중지", description = "신고가 많은 공유 글의 공유를 중지시킵니다.")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(
                            name = "boardId"
                            , value = "공유 글 번호"
                            , required = true
                            , dataType = "Long"
                            , paramType = "path"
                            , defaultValue = "28"
                    )
            })
    @ApiResponses({
            @ApiResponse(code = 200, message = "공유 중지 완료", response = ResponseMessage.class ),
            @ApiResponse(code = 403, message = "권한이 없습니다", response = ResponseMessage.class )
    })
    @PatchMapping("admin/restrict/{boardId}")
    public ResponseEntity<?> restrictBoard(@PathVariable Long boardId){
        adminService.restrictBoard(boardId);
        return ResponseMessage.successResponse(HttpStatus.OK, "공유 중지 완료", null);
    }

    @Tag(name = "Admin")
    @Operation(summary = "신고 삭제", description = "게시물, 댓글의 허위 신고를 삭제합니다.")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(
                            name = "reportId"
                            , value = "신고 번호"
                            , required = true
                            , dataType = "Long"
                            , paramType = "path"
                            , defaultValue = "1"
                    )
            })
    @ApiResponses({
            @ApiResponse(code = 200, message = "신고 삭제 완료", response = ResponseMessage.class ),
            @ApiResponse(code = 403, message = "권한이 없습니다", response = ResponseMessage.class )
    })
    @DeleteMapping("/admin/report/{reportId}")
    public ResponseEntity<?> deleteReport(@PathVariable Long reportId){
        adminService.deleteReport(reportId);
        return ResponseMessage.successResponse(HttpStatus.OK, "신고 삭제 완료", null);
    }

}
