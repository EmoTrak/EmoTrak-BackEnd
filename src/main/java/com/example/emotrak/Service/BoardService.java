package com.example.emotrak.Service;

import com.example.emotrak.dto.*;
import com.example.emotrak.entity.*;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.exception.ResponseMessage;
import com.example.emotrak.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.emotrak.entity.UserRoleEnum.ADMIN;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final FileUploadService fileUploadService;
    private final EmotionRepository emotionRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final LikesRepository likesRepository;
    private static final long MAX_FILE_SIZE = 1 * 1024 * 1024; // 1MB,이부분은 수정이 필요함 1048576 bytes=1MB
    private static final List<String> ALLOWED_IMAGE_CONTENT_TYPES = List.of("image/jpeg", "image/jpg", "image/png", "image/gif");


    //감정글 추가
    public void createDaily(BoardRequestDto boardRequestDto, User user, @Nullable MultipartFile image) {
        /*
        중복되는 이미지 처리 코드를 별도의 메서드로 분리
        업로드하려는 새 이미지 파일, 현재 이미지의 URL(createDaily 에서는 이미지가 없으므로 null 값을 전달)
        삭제할지 여부를 나타내는 boolean 값(요청에 이미지 삭제가 포함되어 있으면 true 값을 전달)
        */
        String imageUrl = handleImage(image, null, false);
        // Emotion 객체 찾기
        Emotion emotion = findEmotionById(boardRequestDto.getEmoId());
        // Daily 객체 생성 및 저장
        Daily daily = new Daily(imageUrl, boardRequestDto, user, emotion);
        boardRepository.save(daily);
    }

    // 글 수정
    public void updateDaily(Long dailyId, BoardRequestDto boardRequestDto, User user, MultipartFile image) throws IOException {
        Daily daily = findDailyById(dailyId);
        validateUserOrAdmin(user, daily);
        String newImageUrl = handleImage(image, daily.getImgUrl(), boardRequestDto.isDeleteImg());
        // Emotion 객체 찾기
        Emotion emotion = findEmotionById(boardRequestDto.getEmoId());
        // Daily 객체 업데이트 및 저장
        daily.update(newImageUrl, boardRequestDto, emotion);
        boardRepository.save(daily);
    }

    // 글 삭제
    public void deleteDaily(Long dailyId, User user) {
        Daily daily = findDailyById(dailyId);
        validateUserOnly(user, daily);
        // 이미지가 null이 아닌 경우에만 S3에서 이미지 파일 삭제
        if (daily.getImgUrl() != null) {
            fileUploadService.deleteFile(daily.getImgUrl());
        }
        // 데이터베이스에서 Daily 객체 삭제
        boardRepository.delete(daily);
    }

    //예외처리
    public void validateImage(MultipartFile image) {
        if (image.getSize() > MAX_FILE_SIZE || !ALLOWED_IMAGE_CONTENT_TYPES.contains(image.getContentType())) {
            throw new CustomException(CustomErrorCode.FILE_UPLOAD_ERROR);}
    }

    private Daily findDailyById(Long dailyId) {
        return boardRepository.findById(dailyId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    }

    private Emotion findEmotionById(Long emotionId) {
        return emotionRepository.findById(emotionId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CONTENT_NOT_FOUND));
    }

    private String handleImage(MultipartFile image, String currentImageUrl, boolean deleteImg) {
        String newImageUrl = currentImageUrl;
        //deleteImg가 true(이미지삭제요청)인 경우, 기존 이미지 삭제 및 업로드
        if (deleteImg) {
            if (currentImageUrl != null) {
                fileUploadService.deleteFile(currentImageUrl);
            }
            newImageUrl = null;
        }
        // 이미지가 null이 아니고 비어있지 않은 경우, 새로운 이미지 업로드
        if (image != null && !image.isEmpty()) {
            validateImage(image);
            newImageUrl = fileUploadService.uploadFile(image);
        }
        return newImageUrl;
    }
    // 수정 권한을 해당 유저와 관리자만 가능하게 메소드
    private void validateUserOrAdmin(User user, Daily daily) {
        if (daily.getUser().getId() != user.getId() && user.getRole() != ADMIN) {
            throw new CustomException(CustomErrorCode.NOT_AUTHOR);
        }
    }
    // 삭제 권한을 해당 유저만 가능하게 변경
    private void validateUserOnly(User user, Daily daily) {
        if (daily.getUser().getId() != user.getId()) {
            throw new CustomException(CustomErrorCode.NOT_AUTHOR);
        }
    }


    // 공유게시판 전체조회(이미지)
    public List<BoardImgRequestDto> getBoardImages(Long page, Long size, String emo) {
        Stream<String> stringStream = Arrays.stream(emo.split(","));
        List<Long> longImo = stringStream.parallel().mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
        List<Object[]> objectList = boardRepository.getBoardImages(page, size, longImo);

        List<BoardImgRequestDto> boardImgRequestDtoList = new ArrayList<>();
        for (Object[] object : objectList) {
            BoardImgRequestDto boardImgRequestDto = new BoardImgRequestDto(object);
            boardImgRequestDtoList.add(boardImgRequestDto);
        }
        return boardImgRequestDtoList;
    }

    // 공유게시판 상세페이지
    public BoardDetailResponseDto getBoardDetail(Long id, User user, int page) {
        Daily daily = boardRepository.findById(id).orElseThrow(
                () -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND)
        );
        // share가 false이고, 사용자와 작성자가 다를 경우 예외 처리
        if (!daily.isShare() && daily.getUser().getId() != user.getId()) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_ACCESS);
        }
        // 페이지네이션을 적용하여 댓글 목록 가져오기
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentsPage = commentRepository.findAllByDaily(daily, pageable);
        List<CommentDetailResponseDto> commentDetailResponseDtoList = commentsPage.getContent().stream()
                .map(comment -> new CommentDetailResponseDto(comment, user))
                .collect(Collectors.toList());
        return new BoardDetailResponseDto(daily, user, commentDetailResponseDtoList);
    }


    //게시물 신고하기
    public void createReport(ReportRequestDto reportRequestDto, User user, Long id) {
        Daily daily = boardRepository.findById(id).orElseThrow(
                () -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND)
        );
        Optional<Report> existingReport = reportRepository.findByUserAndDailyId(user, id);
        if (existingReport.isPresent()) {
            throw new CustomException(CustomErrorCode.DUPLICATE_REPORT); // 이미 신고한 게시물입니다.
        }
        Report report = new Report(reportRequestDto, user, daily);
        reportRepository.save(report);
    }

    // 게시물 신고 삭제하기
    public void deleteReport(User user, Long id) {
        Report report = reportRepository.findByUserAndDailyId(user, id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.REPORT_NOT_FOUND));
        reportRepository.delete(report);
    }

    //게시글 좋아요 (좋아요와 취소 번갈아가며 진행)
    public Map<String, Object> boardlikes(User user, Long boardId) {
        Daily daily = findDailyById(boardId);
        Map<String, Object> response = new HashMap<>();
        if (likesRepository.findByUserAndDaily(user, daily).isEmpty()) {
            // 좋아요 추가
            likesRepository.save(new Likes(daily, user));
            daily.plusLikesCount();
            response.put("message", "좋아요 성공");
        } else {
            // 이미 좋아요한 경우, 좋아요 취소
            likesRepository.deleteByUserAndDaily(user, daily);
            daily.minusLikesCount();
            response.put("message", "좋아요 취소 성공");
        }
        return response;
    }
}
