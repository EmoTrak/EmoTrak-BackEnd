package com.example.emotrak.Service;

import com.example.emotrak.dto.board.BoardRequestDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.Emotion;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private FileUploadService fileUploadService;
    @Mock
    private EmotionRepository emotionRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private LikesRepository likesRepository;

    private User user;
    private BoardRequestDto boardRequestDto;
    private MultipartFile invalidImage;
    private MultipartFile validImage;
    private Emotion emotion;

    @BeforeEach
    void setUp() {
        user = new User("1234", "bambee@gmail.com", "bambee", UserRoleEnum.USER);
        user.setId(1L);
        boardRequestDto = new BoardRequestDto(false, 2023, 4, 22, 1L, 5, "이것은 테스트입니다.", true, false);
        emotion = new Emotion(1L, "기쁨", 1);
        emotion.setId(1L);
        List<String> allowedImageContentTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");

        boardRepository = Mockito.mock(BoardRepository.class);
        fileUploadService = Mockito.mock(FileUploadService.class);
        emotionRepository = Mockito.mock(EmotionRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        reportRepository = Mockito.mock(ReportRepository.class);
        likesRepository = Mockito.mock(LikesRepository.class);
        boardService = new BoardService(boardRepository, fileUploadService, emotionRepository, commentRepository, reportRepository, likesRepository);

        // BoardService 의 allowedImageContentTypes 필드에 목록 설정
        ReflectionTestUtils.setField(boardService, "allowedImageContentTypes", allowedImageContentTypes);
    }

    @Nested
    @DisplayName("감정글 추가")
    class CreateDaily {
        @Test
        @DisplayName("정상적인 감정글 추가")
        void createDailySuccess() {
            // given
            when(boardRepository.countDailyPostsByUserAndDate(user, boardRequestDto.getYear(), boardRequestDto.getMonth(), boardRequestDto.getDay())).thenReturn(1L);
            when(emotionRepository.findById(1L)).thenReturn(Optional.of(emotion));
            // when
            boardService.createDaily(boardRequestDto, user, validImage);
            // then
            verify(boardRepository, times(1)).save(Mockito.any(Daily.class));
        }

        @Test
        @DisplayName("하루에 감정글이 2개 초과")
        void tooManyPosts() {
            // given
            when(boardRepository.countDailyPostsByUserAndDate(user, boardRequestDto.getYear(), boardRequestDto.getMonth(), boardRequestDto.getDay())).thenReturn(2L);
            // when & then
            assertThrows(CustomException.class, () -> boardService.createDaily(boardRequestDto, user, validImage));
            verify(boardRepository, times(0)).save(Mockito.any(Daily.class));
        }

        @Test
        @DisplayName("이미지 파일 형식이 유효하지 않은 경우")
        void validateImageInvalidContentType() {
            // given
            MultipartFile invalidImage = new MockMultipartFile("image", "test.jpg", "invalid/content-type", new byte[0]);
            // when, then
            assertThrows(CustomException.class, () -> boardService.validateImage(invalidImage));
            verify(fileUploadService, times(0)).uploadFile(any());
        }

        @Test
        @DisplayName("이미지가 제공되지 않은 경우")
        void createDailyWithoutImage() {
            // given
            when(emotionRepository.findById(emotion.getId())).thenReturn(Optional.of(emotion));
            when(boardRepository.countDailyPostsByUserAndDate(user, boardRequestDto.getYear(), boardRequestDto.getMonth(), boardRequestDto.getDay())).thenReturn(0L);
            // when
           boardService.createDaily(boardRequestDto, user, null);
            // then
            verify(fileUploadService, never()).uploadFile(any(MultipartFile.class)); // 파일 업로드가 호출되지 않았는지 검증
            verify(boardRepository, times(1)).save(any(Daily.class)); // Daily 객체가 저장되었는지 검증
        }

        @Test
        @DisplayName("Emotion 객체를 찾을 수 없는 경우")
        void findEmotionByIdNotFound() {
            // given
            Long invalidEmoId = 999L;
            BoardRequestDto invalidBoardRequestDto = new BoardRequestDto(true, 2023, 4, 22, invalidEmoId, 5, "Test detail", true, false);
            when(emotionRepository.findById(invalidEmoId)).thenReturn(Optional.empty());
            // when, then
            assertThrows(CustomException.class, () -> boardService.createDaily(invalidBoardRequestDto, user, null));
            verify(fileUploadService, never()).uploadFile(any(MultipartFile.class));
        }
    }

    @Nested
    @DisplayName("감정글 수정")
    class UpdateDaily {

    }


    @Nested
    @DisplayName("감정글 삭제")
    class DeleteDaily {

    }


    // 공유게시판상세조회, 게시글신고, 게시글 좋아요

}
