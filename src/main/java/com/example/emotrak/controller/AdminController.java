package com.example.emotrak.controller;

import com.example.emotrak.Service.AdminService;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin/boards")
    public ResponseEntity<?> reportBoard(){
        return ResponseMessage.successResponse(HttpStatus.OK, "신고 게시물 조회 완료", adminService.reportBoard());
    }

    @GetMapping("/admin/comments")
    public ResponseEntity<?> reportComment(){
        return ResponseMessage.successResponse(HttpStatus.OK, "신고 댓글 조회 완료", adminService.reportComment());
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
            @ApiResponse(code = 401, message = "권한이 없습니다", response = ResponseMessage.class )
    })
    @PatchMapping("admin/restrict/{boardId}")
    public ResponseEntity<?> restrictBoard(@PathVariable Long boardId){
        adminService.restrictBoard(boardId);
        return ResponseMessage.successResponse(HttpStatus.OK, "공유 중지 완료", null);
    }

}
