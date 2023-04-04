package com.example.emotrak.Service;

import com.example.emotrak.dto.BoardRequestDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.Emotion;
import com.example.emotrak.entity.User;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.BoardRepository;
import com.example.emotrak.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final FileUploadService fileUploadService;
    private final EmotionRepository emotionRepository;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB,이부분은 수정이 필요함 1048576 bytes.
    private static final List<String> ALLOWED_IMAGE_CONTENT_TYPES = List.of("image/jpeg", "image/jpg", "image/png", "image/gif");


    //감정글 추가
    public Daily createDailyWithImageUpload(MultipartFile image, BoardRequestDto boardRequestDto, User user)
            throws IOException {
        // 이미지 파일 업로드
        String imageUrl = fileUploadService.uploadFile(image);
        //Emotion 객체 찾기
        Emotion emotion = emotionRepository.findById(boardRequestDto.getEmoId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.CONTENT_NOT_FOUND));
        // Daily 객체 생성 및 저장
        Daily daily = new Daily(imageUrl, boardRequestDto, user, emotion);
        return boardRepository.save(daily);
    }

    // 글 수정
    public Daily updateDailyWithImageUpload(Long dailyId, MultipartFile image, BoardRequestDto boardRequestDto, User user) throws IOException {
        Daily daily = boardRepository.findById(dailyId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
        // S3에서 이전 파일 삭제 및 새 파일 업로드
        String newImageUrl = fileUploadService.updateFile(daily.getImgUrl(), image);
        //Emotion 객체 찾기
        Emotion emotion = emotionRepository.findById(boardRequestDto.getEmoId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.CONTENT_NOT_FOUND)); //Invalid emotion ID 추가필요
        // Daily 객체 업데이트 및 저장
        daily.update(newImageUrl, boardRequestDto, emotion);
        return boardRepository.save(daily);
    }

    // 글 삭제
    public void deleteDaily(Long dailyId, User user) {
        Daily daily = boardRepository.findById(dailyId)
                .orElseThrow(() -> new  CustomException(CustomErrorCode.CONTENT_NOT_FOUND));
        // S3에서 이미지 파일 삭제
        fileUploadService.deleteFile(daily.getImgUrl());
        // 데이터베이스에서 Daily 객체 삭제
        boardRepository.delete(daily);
    }

    // 이미지 유효성 검사 예외처리
    public void validateImage(MultipartFile image) {
        if (image.isEmpty()) throw new CustomException(CustomErrorCode.FILE_UPLOAD_ERROR);
        if (image.getSize() > MAX_FILE_SIZE) throw new CustomException(CustomErrorCode.FILE_UPLOAD_ERROR);
        if (!ALLOWED_IMAGE_CONTENT_TYPES.contains(image.getContentType())) throw new CustomException(CustomErrorCode.FILE_UPLOAD_ERROR);
    }
}
