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

import java.nio.charset.StandardCharsets;
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
    private Daily daily;

    @BeforeEach
    void setUp() {
        user = new User("1234", "bambee@gmail.com", "bambee", UserRoleEnum.USER);
        user.setId(1L);
        boardRequestDto = new BoardRequestDto(false, 2023, 4, 22, 1L, 5, "저는 테스트입니다.", true, false);
        emotion = new Emotion(1L, "기쁨", 1);
        emotion.setId(1L);
        daily = Daily.builder()
                .id(1L)
                .user(user)
                .emotion(emotion)
                .dailyYear(2023)
                .dailyMonth(4)
                .dailyDay(22)
                .detail("저는 테스트입니다.")
                .star(5)
                .imgUrl("imgUrl")
                .share(true)
                .hasRestrict(false)
                .draw(false)
                .build();
        daily.setId(1L);
        List<String> allowedImageContentTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");
        validImage = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test-image".getBytes());
        invalidImage = new MockMultipartFile("invalidImage","invalidImage.jpg", "invalid/imageType", "someImageData".getBytes(StandardCharsets.UTF_8));

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
            BoardRequestDto invalidBoardRequestDto = new BoardRequestDto(true, 2023, 4, 22, invalidEmoId, 5, "저는 테스트입니다.", true, false);
            when(emotionRepository.findById(invalidEmoId)).thenReturn(Optional.empty());
            // when, then
            assertThrows(CustomException.class, () -> boardService.createDaily(invalidBoardRequestDto, user, null));
            verify(fileUploadService, never()).uploadFile(any(MultipartFile.class));
        }
    }

    @Nested
    @DisplayName("감정글 수정")
    class UpdateDaily {
        @Test
        @DisplayName("정상적인 감정글 수정")
        void updateDailySuccess() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(daily));
            when(emotionRepository.findById(emotion.getId())).thenReturn(Optional.of(emotion));
            when(fileUploadService.uploadFile(validImage)).thenReturn("newImageUrl");
            // when
            boardService.updateDaily(daily.getId(), boardRequestDto, user, validImage);
            // then
            verify(boardRepository, times(1)).findById(daily.getId());
            verify(emotionRepository, times(1)).findById(emotion.getId());
            verify(fileUploadService, times(1)).uploadFile(validImage);
        }

        @Test
        @DisplayName("작성자 또는 관리자가 아닌 경우 수정 불가")
        void notAuthorOrAdmin() {
            // given
            User nonAuthorUser = new User("user2@test.com", "user2Password", "user2Nickname", UserRoleEnum.USER);
            nonAuthorUser.setId(2L);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(daily));
            // when, then
            assertThrows(CustomException.class, () -> boardService.updateDaily(daily.getId(), boardRequestDto, nonAuthorUser, validImage));
            verify(fileUploadService, never()).uploadFile(any(MultipartFile.class));
        }

        @Test
        @DisplayName("감정글이 제한 상태이고 공유 상태인 경우 수정 불가")
        void updateRestrictedAndSharedPost() {
            // given
            Daily restrictedSharedDaily = Daily.builder()
                    .id(1L)
                    .user(user)
                    .emotion(emotion)
                    .dailyYear(2023)
                    .dailyMonth(4)
                    .dailyDay(22)
                    .detail("저는 테스트입니다.")
                    .star(5)
                    .imgUrl("imgUrl")
                    .share(true)
                    .hasRestrict(true)
                    .draw(false)
                    .build();
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(restrictedSharedDaily));
            // when, then
            assertThrows(CustomException.class, () -> boardService.updateDaily(restrictedSharedDaily.getId(), boardRequestDto, user, validImage));
            verify(fileUploadService, never()).uploadFile(any(MultipartFile.class));
        }

        @Test
        @DisplayName("이미지 삭제 요청 처리")
        void handleImageDelete() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(daily));
            when(emotionRepository.findById(emotion.getId())).thenReturn(Optional.of(emotion));
            doNothing().when(fileUploadService).deleteFile(anyString());
            BoardRequestDto deleteImgBoardRequestDto = new BoardRequestDto(true, 2023, 4, 22, emotion.getId(), 5, "저는 테스트입니다.", true, true);
            // when
            boardService.updateDaily(daily.getId(), deleteImgBoardRequestDto, user, null);
            // then
            verify(fileUploadService, times(1)).deleteFile(anyString());
            verify(fileUploadService, never()).uploadFile(any(MultipartFile.class));
        }

        @Test
        @DisplayName("유효하지 않은 이미지 타입")
        void validateImage_invalidImageType() {
            // given
            invalidImage = Mockito.mock(MultipartFile.class);
            when(invalidImage.getContentType()).thenReturn("invalid/imageType");

            // when, then
            assertThrows(CustomException.class, () -> boardService.validateImage(invalidImage));
        }


        @Test
        @DisplayName("Emotion 객체를 찾을 수 없는 경우 수정불가")
        void findEmotionByIdNotFound() {
            // given
            Long invalidEmoId = 999L;
            BoardRequestDto invalidBoardRequestDto = new BoardRequestDto(true, 2023, 4, 22, invalidEmoId, 5, "저는 테스트입니다.", true, false);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(daily));
            when(emotionRepository.findById(invalidEmoId)).thenReturn(Optional.empty());
            // when, then
            assertThrows(CustomException.class, () -> boardService.updateDaily(daily.getId(), invalidBoardRequestDto, user, null));
            verify(fileUploadService, never()).uploadFile(any(MultipartFile.class));
        }

        @Test
        @DisplayName("존재하지 않는 글 수정 불가")
        void findDailyById_notFound() {
            // given
            Long invalidDailyId = 999L;
            User testUser = new User("user2@test.com", "user2Password", "user2Nickname", UserRoleEnum.USER);
            BoardRequestDto updateRequestDto = new BoardRequestDto(true, 2023, 4, 22, emotion.getId(), 5, "저는 테스트입니다.", true, false);

            when(boardRepository.findById(invalidDailyId)).thenReturn(Optional.empty());

            // when, then
            assertThrows(CustomException.class, () -> boardService.updateDaily(invalidDailyId, updateRequestDto, testUser, null));
            verify(boardRepository, times(1)).findById(invalidDailyId);
        }
    }


    @Nested
    @DisplayName("감정글 삭제")
    class DeleteDaily {
        @Test
        @DisplayName("정상적인 감정글 삭제")
        void deleteDaily_success() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(daily));

            // when
            boardService.deleteDaily(daily.getId(), user);

            // then
            verify(fileUploadService, times(1)).deleteFile(daily.getImgUrl());
            verify(likesRepository, times(1)).deleteCommentLike(daily.getId());
            verify(reportRepository, times(1)).deleteCommentByDaily(daily.getId());
            verify(commentRepository, times(1)).deleteByDaily(daily.getId());
            verify(likesRepository, times(1)).deleteBoardLike(daily.getId());
            verify(reportRepository, times(1)).deleteAllByDaily(daily);
            verify(boardRepository, times(1)).delete(daily);
        }

        @Test
        @DisplayName("정상적인 감정글 삭제, 이미지가 없는 경우")
        void deleteDaily_success_withoutImage() {
            // given
            daily.setImgUrl(null);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(daily));

            // when
            boardService.deleteDaily(daily.getId(), user);


            // then
            verify(boardRepository, times(1)).findById(daily.getId());
            verify(fileUploadService, never()).deleteFile(anyString());
            verify(likesRepository, times(1)).deleteCommentLike(daily.getId());
            verify(reportRepository, times(1)).deleteCommentByDaily(daily.getId());
            verify(commentRepository, times(1)).deleteByDaily(daily.getId());
            verify(likesRepository, times(1)).deleteBoardLike(daily.getId());
            verify(reportRepository, times(1)).deleteAllByDaily(daily);
            verify(boardRepository, times(1)).delete(daily);
        }

        @Test
        @DisplayName("작성자가 아닌 경우 삭제 불가")
        void deleteDaily_notAuthor() {
            // given
            User nonAuthorUser = new User("user2@test.com", "user2Password", "user2Nickname", UserRoleEnum.USER);
            nonAuthorUser.setId(2L);
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(daily));

            // when, then
            assertThrows(CustomException.class, () -> boardService.deleteDaily(daily.getId(), nonAuthorUser));
        }

        @Test
        @DisplayName("존재하지 않는 글 삭제 불가")
        void findDailyById_notFound() {
            // given
            Long invalidDailyId = 999L;
            User testUser = new User("user2@test.com", "user2Password", "user2Nickname", UserRoleEnum.USER);
            when(boardRepository.findById(invalidDailyId)).thenReturn(Optional.empty());

            // when, then
            assertThrows(CustomException.class, () -> boardService.deleteDaily(invalidDailyId, testUser));
            verify(boardRepository, times(1)).findById(invalidDailyId);
        }

    }


    @Nested
    @DisplayName("공유게시판 조회")
    class DetailDaily {

    }


    @Nested
    @DisplayName("게시글 좋아요")
    class LikesDaily {

    }


    @Nested
    @DisplayName("게시글 신고")
    class RepostDaily {


    }

}
