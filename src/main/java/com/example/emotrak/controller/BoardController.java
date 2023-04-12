package com.example.emotrak.controller;

import com.example.emotrak.Service.BoardService;
import com.example.emotrak.dto.BoardDetailResponseDto;
import com.example.emotrak.dto.BoardRequestDto;
import com.example.emotrak.dto.ReportRequestDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //감정글 추가 : 이미지가 null일 때에는 이미지를 저장하지 않고 Daily 객체를 생성하고 저장(이미지 유효성 검사를 이미지가 존재할 경우에만 수행)
    @PostMapping(value = "/daily", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createDaily(@RequestParam("image") MultipartFile image,
                                         @RequestPart("contents") @Valid BoardRequestDto boardRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        // 이미지 파일 업로드 및 글 작성 처리
        return ResponseMessage.successResponse(HttpStatus.CREATED, "글작성 성공", boardService.createDaily(boardRequestDto, userDetails.getUser(), image));
    }

    // 글 수정 : 이미지 수정이 없을 경우 빈블럭으로 만들어서 받음, >  required = false를 추가하여 이미지가 선택적으로 전달
    @PatchMapping(value = "/daily/{dailyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDaily(@PathVariable Long dailyId,
                                         @RequestParam(value = "image", required = false) MultipartFile image,
                                         @RequestPart("contents") @Valid BoardRequestDto boardRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        // 이미지 파일 업로드 및 글 수정 처리
        boardService.updateDaily(dailyId, boardRequestDto, userDetails.getUser(), image);
        return ResponseMessage.successResponse(HttpStatus.OK, "글수정 성공", null);
    }

    // 글 삭제 : 이미지 URL이 null이 아닌 경우에만 S3에서 이미지 파일을 삭제하도록 조건문을 추가
    @DeleteMapping("/daily/{dailyId}")
    public ResponseEntity<?> deleteDaily(@PathVariable Long dailyId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boardService.deleteDaily(dailyId, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "글삭제 성공", null);
    }

    // 공유게시판 전체 조회 (이미지)
    @GetMapping("/boards")
    public ResponseEntity<?> getBoardImages(@RequestParam(value = "page", defaultValue = "1") int page
                                          , @RequestParam(value = "size", defaultValue = "10") int size
                                          , @RequestParam(value = "emo", defaultValue = "1,2,3,4,5,6") String emo
                                          , @RequestParam(value = "sort", defaultValue = "recent") String sort) {
        return ResponseMessage.successResponse(HttpStatus.OK, "조회 완료", boardService.getBoardImages(page, size, emo, sort));
    }

    // 공유게시판 상세페이지
    @GetMapping(value = "/boards/{boardId}")
    public ResponseEntity<?> getBoardDetails(@PathVariable Long boardId,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = null;
        if (userDetails != null) user = userDetails.getUser();
        // 게시글 정보 조회 처리
        BoardDetailResponseDto boardDetailResponseDto = boardService.getBoardDetail(boardId, user, page);
        return ResponseMessage.successResponse(HttpStatus.OK, "상세 조회 성공", boardDetailResponseDto);
    }

    //게시물 신고하기
    @PostMapping(value = "/boards/report/{boardId}")
    public ResponseEntity<?> reportBoard(@PathVariable Long boardId,
                                         @RequestBody @Valid ReportRequestDto reportRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 게시글 신고 처리
        boardService.createReport(reportRequestDto, userDetails.getUser(), boardId);
        return ResponseMessage.successResponse(HttpStatus.CREATED, "게시글 신고 성공", null);
    }

    //게시물 신고 삭제하기
    @DeleteMapping("/boards/report/{boardId}")
    public ResponseEntity<?> deleteReport(@PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boardService.deleteReport(userDetails.getUser(), boardId);
        return ResponseMessage.successResponse(HttpStatus.OK, "게시물 신고 삭제 성공", null);
    }

    //게시글 좋아요 (좋아요와 취소 번갈아가며 진행)
    @PostMapping("/boards/likes/{boardId}")
    public ResponseEntity<?> boardlikes(@PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Map<String, Object> response = boardService.boardlikes(userDetails.getUser(), boardId);
        String message = (String) response.get("message");
        return ResponseMessage.successResponse(HttpStatus.OK, message, null);
    }

}
