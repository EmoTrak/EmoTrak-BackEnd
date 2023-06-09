package com.example.emotrak.controller;

import com.example.emotrak.service.BoardService;
import com.example.emotrak.dto.board.BoardRequestDto;
import com.example.emotrak.dto.like.LikeResponseDto;
import com.example.emotrak.dto.report.ReportRequestDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "Board", description = "게시글")
@Tag(name = "Report", description = "신고합니다")
@Tag(name = "Likes", description = "좋아합니다")
public class BoardController {

    private final BoardService boardService;

    //이미지가 null일 때에는 이미지를 저장하지 않고 Daily 객체를 생성하고 저장
    @Tag(name = "Board")
    @Operation(summary = "감정글 추가", description = "이미지가 존재할 경우에만 이미지 유효성 검사를 수행")
    @PostMapping(value = "/daily", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createDaily(@RequestParam(value = "image", required = false) MultipartFile image,
                                         @RequestPart("contents") @Valid BoardRequestDto boardRequestDto,
                                         @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 이미지 파일 업로드 및 글 작성 처리
        return ResponseMessage.successResponse(HttpStatus.CREATED, "글작성 성공", boardService.createDaily(boardRequestDto, userDetails.getUser(), image));
    }

    @Tag(name = "Board")
    @Operation(summary = "감정글 수정", description = "required = false를 추가하여 이미지가 선택적으로 전달")
    @PatchMapping(value = "/daily/{dailyId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateDaily(@PathVariable Long dailyId,
                                         @RequestParam(value = "image", required = false) MultipartFile image,
                                         @RequestPart("contents") @Valid BoardRequestDto boardRequestDto,
                                         @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 이미지 파일 업로드 및 글 수정 처리
        boardService.updateDaily(dailyId, boardRequestDto, userDetails.getUser(), image);
        return ResponseMessage.successResponse(HttpStatus.OK, "글수정 성공", null);
    }

    @Tag(name = "Board")
    @Operation(summary = "감정글 삭제", description = "이미지 URL이 null이 아닌 경우에만 S3에서 이미지 파일을 삭제하도록 조건문을 추가")
    @DeleteMapping("/daily/{dailyId}")
    public ResponseEntity<?> deleteDaily(@PathVariable Long dailyId,
                                         @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boardService.deleteDaily(dailyId, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "글삭제 성공", null);
    }

    @Tag(name = "Board")
    @Operation(summary = "공유게시판 전체 조회", description = "이미지")
    @GetMapping("/boards")
    public ResponseEntity<?> getBoardImages(@RequestParam(value = "page", defaultValue = "1") int page
                                          , @RequestParam(value = "size", defaultValue = "24") int size
                                          , @RequestParam(value = "emo", defaultValue = "1,2,3,4,5,6") String emo
                                          , @RequestParam(value = "sort", defaultValue = "recent") String sort
                                          , @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = (userDetails != null) ? userDetails.getUser() : null;
        return ResponseMessage.successResponse(HttpStatus.OK, "조회 완료", boardService.getBoardImages(page, size, emo, sort, user));
    }

    @Tag(name = "Board")
    @Operation(summary = "공유게시판 상세페이지 조회", description = "댓글 20개 기준 페이징처리")
    @GetMapping(value = "/boards/{boardId}")
    public ResponseEntity<?> getBoardDetails(@PathVariable Long boardId,
                                             @RequestParam(value = "page", defaultValue = "1") int page,
                                             @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = (userDetails != null) ? userDetails.getUser() : null;
        return ResponseMessage.successResponse(HttpStatus.OK, "상세 조회 성공", boardService.getBoardDetail(boardId, user, page));
    }

    @Tag(name = "Report")
    @Operation(summary = "게시글 신고하기", description = "게시글 신고 성공")
    @PostMapping(value = "/boards/report/{boardId}")
    public ResponseEntity<?> reportBoard(@PathVariable Long boardId,
                                         @RequestBody @Valid ReportRequestDto reportRequestDto,
                                         @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boardService.createReport(reportRequestDto, userDetails.getUser(), boardId);
        return ResponseMessage.successResponse(HttpStatus.CREATED, "게시글 신고 성공", null);
    }

    @Tag(name = "Likes")
    @Operation(summary = "게시글 좋아요", description = "좋아요와 취소 번갈아가며 진행")
    @PostMapping("/boards/likes/{boardId}")
    public ResponseEntity<?> boardLikes(@PathVariable Long boardId,
                                        @ApiIgnore @AuthenticationPrincipal UserDetailsImpl userDetails) {
        LikeResponseDto likeResponseDto = boardService.boardLikes(userDetails.getUser(), boardId);
        String message = likeResponseDto.isHasLike() ? "좋아요 성공" : "좋아요 취소 성공";
        return ResponseMessage.successResponse(HttpStatus.OK, message, likeResponseDto);
    }

}
