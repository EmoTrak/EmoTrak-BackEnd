package com.example.emotrak.Service;

import com.example.emotrak.dto.BoardRequestDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.Emotion;
import com.example.emotrak.entity.User;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.BoardRepository;
import com.example.emotrak.repository.CommentRepository;
import com.example.emotrak.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
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
    private final CommentRepository commentRepository;
    private static final long MAX_FILE_SIZE = 1 * 1024 * 1024; // 1MB,이부분은 수정이 필요함 1048576 bytes=1MB
    private static final List<String> ALLOWED_IMAGE_CONTENT_TYPES = List.of("image/jpeg", "image/jpg", "image/png", "image/gif");


    //감정글 추가
    public Daily createDaily(BoardRequestDto boardRequestDto, User user, @Nullable MultipartFile image) throws IOException {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            // 이미지 파일 업로드
            imageUrl = fileUploadService.uploadFile(image);
        }
        //Emotion 객체 찾기
        Emotion emotion = emotionRepository.findById(boardRequestDto.getEmoId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.CONTENT_NOT_FOUND));
        // Daily 객체 생성 및 저장
        Daily daily = new Daily(imageUrl, boardRequestDto, user, emotion);
        return boardRepository.save(daily);
    }

    // 글 수정
    public Daily updateDaily(Long dailyId, BoardRequestDto boardRequestDto, User user, MultipartFile image) throws IOException {
        Daily daily = boardRepository.findById(dailyId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
        String newImageUrl = daily.getImgUrl();
        if (image != null) {
            if (!image.isEmpty()) {
                // S3에서 이전 파일 삭제 및 새 파일 업로드
                newImageUrl = fileUploadService.updateFile(daily.getImgUrl(), image);
            } else {
                // 이미지를 null로 설정하여 이전 이미지 삭제
                fileUploadService.deleteFile(daily.getImgUrl());
                newImageUrl = null;
            }
        }
        //Emotion 객체 찾기
        Emotion emotion = emotionRepository.findById(boardRequestDto.getEmoId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.CONTENT_NOT_FOUND)); //Invalid emotion ID 추가필요 (이진님파일함칠때바뀜)
        // Daily 객체 업데이트 및 저장
        daily.update(newImageUrl, boardRequestDto, emotion);
        return boardRepository.save(daily);
    }

    // 글 삭제
    public void deleteDaily(Long dailyId, User user) {
        Daily daily = boardRepository.findById(dailyId)
                .orElseThrow(() -> new  CustomException(CustomErrorCode.CONTENT_NOT_FOUND));
        // 이미지가 null이 아닌 경우에만 S3에서 이미지 파일 삭제
        if (daily.getImgUrl() != null) {
            fileUploadService.deleteFile(daily.getImgUrl());
        }
        // 데이터베이스에서 Daily 객체 삭제
        boardRepository.delete(daily);
    }

    // 이미지 유효성 검사 예외처리
    public void validateImage(MultipartFile image) {
        if (image.getSize() > MAX_FILE_SIZE) throw new CustomException(CustomErrorCode.FILE_UPLOAD_ERROR);
        if (!ALLOWED_IMAGE_CONTENT_TYPES.contains(image.getContentType())) throw new CustomException(CustomErrorCode.FILE_UPLOAD_ERROR);  //상의후결정
    }

//    public BoardDetailResponseDto getBoardDetail(Long dailyId, User user) {
//        Daily daily = boardRepository.findById(dailyId)
//                .orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
//        // 댓글 목록 조회
//        List<Comment> commentList = commentRepository.findAllByDaily(daily);
//        // 댓글 목록을 CommentDto로 변환
//        List<CommentDetailResponseDto> commentDetailResponseDtoList = commentList.stream()
//                .map(comment -> new CommentDetailResponseDto(
//                        comment.getId(),
//                        comment.getUser().getEmail(),
//                        comment.getComment(),
//                        comment.getCreatedAt().toString(),
//                        comment.getUser().getId().equals(user.getId())))
//                .collect(Collectors.toList());
//        // 게시글 정보를 BoardDetailsDto로 변환
//        BoardDetailResponseDto boardDetailResponseDto = new BoardDetailResponseDto(daily, commentDetailResponseDtoList);
//        return boardDetailsDto;
//    }
}
