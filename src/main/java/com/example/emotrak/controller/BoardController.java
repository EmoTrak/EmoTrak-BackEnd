package com.example.emotrak.controller;

import com.example.emotrak.Service.BoardService;
import com.example.emotrak.dto.BoardRequestDto;
import com.example.emotrak.entity.Daily;
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

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //감정글 추가
    @PostMapping(value = "/daily", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createDaily(@RequestParam("image") MultipartFile image,
                                         @RequestPart("contents") @Valid BoardRequestDto boardRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        // 이미지 유효성 검사
        boardService.validateImage(image);
        // 이미지 파일 업로드 및 글 작성 처리
        Daily daily = boardService.createDailyWithImageUpload(image, boardRequestDto, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.CREATED, "글작성 성공", null);
    }

    // 글 수정 : 이미지 수정이 없을 경우 빈블럭으로 만들어서 받음, url 세분화시킬지 미결정
    @PatchMapping(value = "/daily/{dailyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDaily(@PathVariable Long dailyId,
                                         @RequestParam("image") MultipartFile image,
                                         @RequestPart("contents") @Valid BoardRequestDto boardRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        // 이미지 유효성 검사
        boardService.validateImage(image);
        // 이미지 파일 업로드 및 글 수정 처리
        Daily daily = boardService.updateDailyWithImageUpload(dailyId, image, boardRequestDto, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "글수정 성공", null);
    }

    // 글 삭제
    @DeleteMapping("/daily/{dailyId}")
    public ResponseEntity<?> deleteDaily(@PathVariable Long dailyId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boardService.deleteDaily(dailyId, userDetails.getUser());
        return ResponseMessage.successResponse(HttpStatus.OK, "글삭제 성공", null);
    }

    //공유게시판 전체조회 > 이건 나중에 api 완성된다....


    //상세페이지


}
