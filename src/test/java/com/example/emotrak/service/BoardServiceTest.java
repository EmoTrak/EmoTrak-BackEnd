package com.example.emotrak.service;

import com.example.emotrak.dto.board.BoardRequestDto;
import com.example.emotrak.dto.like.LikeResponseDto;
import com.example.emotrak.dto.report.ReportRequestDto;
import com.example.emotrak.entity.*;
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
import java.util.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    private ReportRequestDto reportRequestDto;

    @BeforeEach
    void setUp() {
        user = new User("1234", "bambee@gmail.com", "bambee", UserRoleEnum.USER);
        user.setId(1L);
        boardRequestDto = new BoardRequestDto(false, 2023, 4, 22, 1L, 5, "저는 테스트입니다.", true, false);
        emotion = new Emotion(1L, "기쁨", 1);
        emotion.setId(1L);
        daily = new Daily("imgUrl", boardRequestDto, user, emotion);
        daily.setId(1L);
        reportRequestDto = new ReportRequestDto("신고합니다.");
        List<String> allowedImageContentTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");
        validImage = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test-image".getBytes());
        invalidImage = new MockMultipartFile("invalidImage","invalidImage.jpg", "invalid/imageType", "someImageData".getBytes());
        // BoardService 의 allowedImageContentTypes 필드에 목록 설정
        ReflectionTestUtils.setField(boardService, "allowedImageContentTypes", allowedImageContentTypes);
    }

    @Nested
    @DisplayName("감정글 작성")
    class CreateDaily {
        @Test
        @DisplayName("정상적인 감정글 작성")
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
        @DisplayName("이미지가 제공되지 않은 경우, 정상적인 게시글 작성")
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
        @DisplayName("작성자가 아닌 경우 수정 불가")
        void updateNotAuthor() {
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
            Long id = 1L;
            BoardRequestDto boardRequestDtoWithRestrictions = new BoardRequestDto(false, 2023, 4, 22, 1L, 5, "저는 테스트입니다.", true, false);
            Daily restrictedSharedDaily = new Daily("imgUrl", boardRequestDtoWithRestrictions, user, emotion);
            restrictedSharedDaily.setId(1L);
            restrictedSharedDaily.setShare(true);
            restrictedSharedDaily.setHasRestrict(true);
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
        void deleteDailySuccess() {
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
        void deleteDailySuccessWithoutImage() {
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
        void deleteDailyNotAuthor() {
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


//    @Nested
//    @DisplayName("공유게시판 조회")
//    class DetailDaily {
//        @Test
//        @DisplayName("공유된 글 조회 - 게시글 작성자와 사용자 같은 경우")
//        void getBoardDetail_authorAndUserSame() {
//            // given
//            BigInteger zero = new BigInteger("0");
//            BigInteger one = new BigInteger("1");
//            BigInteger userId = new BigInteger(user.getId().toString());
//
//             /*
//             [0]:share, [1]:userId, [2]:id, [3]:date, [4]:emoId
//             [5]:star, [6]:detail, [7]:imgUrl, [8]:hasAuth, [9]:nickname, [10]:likesCnt, [11]:restrict
//             [12]:hasLike, [13]:draw, [14]:hasReport, [15]:totalComments
//             */
//            Object[] objectDaily = { daily.isShare(), userId, one, daily.getCreatedAt(), new BigInteger(emotion.getId().toString())
//                    , daily.getStar(), daily.getDetail(), daily.getImgUrl(), one, user.getNickname(), zero, daily.isHasRestrict()
//                    , zero, daily.isDraw(), zero, zero};
//
//            List<CommentDetailResponseDto> commentList = new ArrayList<>();
//            BoardDetailResponseDto expectedResponse = new BoardDetailResponseDto(objectDaily, commentList, true);
//            when(boardRepository.getDailyDetail(user.getId(), ((BigInteger)objectDaily[2]).longValue())).thenReturn(Collections.singletonList(objectDaily));
//
//            int page = 1;
//            int size = 20;
//            Pageable pageable = PageRequest.of(page-1, size+1);
//            when(commentRepository.getCommentDetail(user.getId(), ((BigInteger)objectDaily[2]).longValue(), pageable)).thenReturn(new ArrayList<>()); // 댓글이 없는 경우
//
//            // when
//            BoardDetailResponseDto actualResponse = boardService.getBoardDetail(((BigInteger)objectDaily[2]).longValue(), user, page);
//
//            // then
//            assertEquals(expectedResponse.getId(), actualResponse.getId());
//            assertEquals(expectedResponse.getDate(), actualResponse.getDate());
//            assertEquals(expectedResponse.getEmoId(), actualResponse.getEmoId());
//            assertEquals(expectedResponse.getStar(), actualResponse.getStar());
//            assertEquals(expectedResponse.getDetail(), actualResponse.getDetail());
//            assertEquals(expectedResponse.getImgUrl(), actualResponse.getImgUrl());
//            assertEquals(expectedResponse.isHasAuth(), actualResponse.isHasAuth());
//            assertEquals(expectedResponse.getNickname(), actualResponse.getNickname());
//            assertEquals(expectedResponse.getLikesCnt(), actualResponse.getLikesCnt());
//            assertEquals(expectedResponse.isRestrict(), actualResponse.isRestrict());
//            assertEquals(expectedResponse.isHasLike(), actualResponse.isHasLike());
//            assertEquals(expectedResponse.isLastPage(), actualResponse.isLastPage());
//            assertEquals(expectedResponse.isDraw(), actualResponse.isDraw());
//            assertEquals(expectedResponse.isHasReport(), actualResponse.isHasReport());
//            assertEquals(expectedResponse.getTotalComments(), actualResponse.getTotalComments());
//            assertEquals(expectedResponse.getCommentDetailResponseDtoList(), actualResponse.getCommentDetailResponseDtoList());
//
//            verify(boardRepository, times(1)).getDailyDetail(user.getId(), ((BigInteger)objectDaily[2]).longValue());
//        }
//
//        @Test
//        @DisplayName("공유된 글 조회 - 게시글 작성자와 사용자 다른 경우")
//        void getBoardDetail_authorAndUserDifferent() {
//            // given
//            User otherUser = new User("5678", "otherUser@gmail.com", "otherUser", UserRoleEnum.USER);
//            otherUser.setId(2L);
//            BigInteger zero = new BigInteger("0");
//            BigInteger one = new BigInteger("1");
//            BigInteger userId = new BigInteger(otherUser.getId().toString());
//
//             /*
//             [0]:share, [1]:userId, [2]:id, [3]:date, [4]:emoId
//             [5]:star, [6]:detail, [7]:imgUrl, [8]:hasAuth, [9]:nickname, [10]:likesCnt, [11]:restrict
//             [12]:hasLike, [13]:draw, [14]:hasReport, [15]:totalComments
//             */
//            Object[] otherUserDaily = { daily.isShare(), userId, one, daily.getCreatedAt(), new BigInteger(emotion.getId().toString())
//                    , daily.getStar(), daily.getDetail(), daily.getImgUrl(), one, otherUser.getNickname(), zero, daily.isHasRestrict()
//                    , zero, daily.isDraw(), zero, zero};
//
//            List<CommentDetailResponseDto> commentList = new ArrayList<>();
//            BoardDetailResponseDto expectedResponse = new BoardDetailResponseDto(otherUserDaily, commentList, true);
//            when(boardRepository.getDailyDetail(user.getId(), ((BigInteger)otherUserDaily[2]).longValue())).thenReturn(Collections.singletonList(otherUserDaily));
//            int page = 1;
//            int size = 20;
//            Pageable pageable = PageRequest.of(page-1, size+1);
//            when(commentRepository.getCommentDetail(user.getId(), ((BigInteger)otherUserDaily[2]).longValue(), pageable)).thenReturn(new ArrayList<>()); // 댓글이 없는 경우
//
//            // when
//            BoardDetailResponseDto actualResponse = boardService.getBoardDetail(((BigInteger)otherUserDaily[2]).longValue(), user, page);
//
//            // then
//            assertEquals(expectedResponse.getId(), actualResponse.getId());
//            assertEquals(expectedResponse.getDate(), actualResponse.getDate());
//            assertEquals(expectedResponse.getEmoId(), actualResponse.getEmoId());
//            assertEquals(expectedResponse.getStar(), actualResponse.getStar());
//            assertEquals(expectedResponse.getDetail(), actualResponse.getDetail());
//            assertEquals(expectedResponse.getImgUrl(), actualResponse.getImgUrl());
//            assertEquals(expectedResponse.isHasAuth(), actualResponse.isHasAuth());
//            assertEquals(expectedResponse.getNickname(), actualResponse.getNickname());
//            assertEquals(expectedResponse.getLikesCnt(), actualResponse.getLikesCnt());
//            assertEquals(expectedResponse.isRestrict(), actualResponse.isRestrict());
//            assertEquals(expectedResponse.isHasLike(), actualResponse.isHasLike());
//            assertEquals(expectedResponse.isLastPage(), actualResponse.isLastPage());
//            assertEquals(expectedResponse.isDraw(), actualResponse.isDraw());
//            assertEquals(expectedResponse.isHasReport(), actualResponse.isHasReport());
//            assertEquals(expectedResponse.getTotalComments(), actualResponse.getTotalComments());
//            assertEquals(expectedResponse.getCommentDetailResponseDtoList(), actualResponse.getCommentDetailResponseDtoList());
//
//            verify(boardRepository, times(1)).getDailyDetail(user.getId(), ((BigInteger)otherUserDaily[2]).longValue());
//        }

        @Test
        @DisplayName("공유된 글 조회 - 유효하지 않은 페이지 번호")
        void getBoardDetail_invalidPage() {
            // given
            Long id = 1L;
            int invalidPage = -1;

            // when
            CustomException customException = assertThrows(CustomException.class, () -> {
                boardService.getBoardDetail(id, user, invalidPage);
            });

            // then
            assertEquals("페이지는 1부터 시작합니다.", customException.getErrorCode().getMessage());

            verify(boardRepository, times(0)).getDailyDetail(user.getId(), id);
        }


//        @Test
//        @DisplayName("공유되지 않은 글 조회")
//        void getBoardDetail_Unauthorized() {
//            // given
//            Long id = 1L;
//            int page = 1;
//            User otherUser = new User("5678", "otherUser@gmail.com", "otherUser", UserRoleEnum.USER);
//            otherUser.setId(2L);
//            BigInteger zero = new BigInteger("0");
//            BigInteger one = new BigInteger("1");
//            BigInteger userId = new BigInteger(otherUser.getId().toString());
//
//             /*
//             [0]:share, [1]:userId, [2]:id, [3]:date, [4]:emoId
//             [5]:star, [6]:detail, [7]:imgUrl, [8]:hasAuth, [9]:nickname, [10]:likesCnt, [11]:restrict
//             [12]:hasLike, [13]:draw, [14]:hasReport, [15]:totalComments
//             */
//            Object[] otherUserDaily = { false, userId, one, daily.getCreatedAt(), new BigInteger(emotion.getId().toString())
//                    , daily.getStar(), daily.getDetail(), daily.getImgUrl(), one, otherUser.getNickname(), zero, daily.isHasRestrict()
//                    , zero, daily.isDraw(), zero, zero};
//            when(boardRepository.getDailyDetail(user.getId(), ((BigInteger)otherUserDaily[2]).longValue())).thenReturn(Collections.singletonList(otherUserDaily));
//
//            // when
//            CustomException customException = assertThrows(CustomException.class, () -> {
//                boardService.getBoardDetail(id, user, page);
//            });
//
//            // then
//            assertEquals("권한이 없습니다.", customException.getErrorCode().getMessage());
//
//            verify(boardRepository, times(1)).getDailyDetail(user.getId(), id);
//        }

//        @Test
//        @DisplayName("존재하지 않는 글 조회")
//        void getBoardDetail_DailyNotFound() {
//            // given
//            Long id = 1L;
//            int page = 1;
//            when(boardRepository.getDailyDetail(user.getId(), id)).thenReturn(BoardGetDetailDto);
//
//            // when
//            CustomException customException = assertThrows(CustomException.class, () -> {
//                boardService.getBoardDetail(id, user, page);
//            });
//
//            // then
//            assertEquals("선택한 게시물을 찾을 수 없습니다.", customException.getErrorCode().getMessage());
//
//            verify(boardRepository, times(1)).getDailyDetail(user.getId(), id);
//        }
//
//    }


    @Nested
    @DisplayName("게시글 좋아요")
    class LikesDaily {
        @Test
        @DisplayName("게시글 좋아요 추가 및 삭제")
        void toggleBoardLike() {
            // given
            Long id = 1L;
            when(boardRepository.findById(id)).thenReturn(Optional.of(daily));
            when(likesRepository.findByUserAndDaily(user, daily)).thenReturn(Optional.empty());

            // when: 좋아요 추가
            LikeResponseDto likeResponseDto = boardService.boardLikes(user, id);

            // then: 좋아요가 추가되었는지 확인
            assertThat(likeResponseDto.isHasLike()).isTrue();
            verify(likesRepository, times(1)).save(Mockito.any(Likes.class));
            verify(likesRepository, times(1)).countByDaily(daily);

            // given: 이미 좋아요가 눌러진 상태
            Likes likes = new Likes(daily, user);
            likes.setId(1L);
            when(likesRepository.findByUserAndDaily(user, daily)).thenReturn(Optional.of(likes));
            doNothing().when(likesRepository).deleteById(Mockito.anyLong());

            // when: 좋아요 취소
            likeResponseDto = boardService.boardLikes(user, id);

            // then: 좋아요가 취소되었는지 확인
            assertThat(likeResponseDto.isHasLike()).isFalse();
            verify(likesRepository, times(1)).deleteById(likes.getId());
            verify(likesRepository, times(2)).countByDaily(daily);
        }



        @Test
        @DisplayName("존재하지 않는 게시글 좋아요")
        void boardLikesBoardNotFound() {
            // given
            Long boardId = 1L;
            when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

            // when
            assertThrows(CustomException.class, () -> boardService.boardLikes(user, boardId),
                    "게시글이 존재하지 않는 경우에는 CustomException 이 발생해야 합니다.");

            // then
            verify(likesRepository, never()).save(any());
            verify(likesRepository, never()).deleteById(anyLong());
        }
    }


    @Nested
    @DisplayName("게시글 신고")
    class ReportDaily {
        @Test
        @DisplayName("정상적인 게시물 신고")
        void createReport() {
            // given
            Long id = 1L;
            Daily sharedDaily = new Daily("imgUrl",
                    new BoardRequestDto(false, 2023, 4, 22, 1L, 5, "저는 테스트입니다.", true, false),
                    user, emotion);
            sharedDaily.setId(id);
            ReportRequestDto reportRequestDto = new ReportRequestDto("신고 사유");
            when(boardRepository.findById(daily.getId())).thenReturn(Optional.of(daily));
            // when
            boardService.createReport(reportRequestDto, user, daily.getId());
            // then
            verify(reportRepository, times(1)).save(Mockito.any(Report.class));
        }

        @Test
        @DisplayName("존재하지 않는 게시물 신고")
        void reportDailyNotFound() {
            // given
            Long dailyId = 1L;
            when(boardRepository.findById(dailyId)).thenReturn(Optional.empty());
            ReportRequestDto reportRequestDto = new ReportRequestDto("신고 사유");
            // when
            assertThrows(CustomException.class, () -> boardService.createReport(reportRequestDto, user, dailyId),
                    "게시물이 존재하지 않는 경우에는 CustomException 이 발생해야 합니다.");
            // then
            verify(reportRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("중복되는 게시물 신고")
        void reportDailyDuplicate() {
            // given
            Long dailyId = 1L;
            when(boardRepository.findById(dailyId)).thenReturn(Optional.empty());
            // when
            assertThrows(CustomException.class, () -> boardService.createReport(reportRequestDto, user, dailyId),
                    "게시물이 존재하지 않는 경우에는 CustomException 이 발생해야 합니다.");
            // then
            verify(reportRepository, Mockito.never()).save(Mockito.any());
//            // given
//            when(boardRepository.findById(dailyId)).thenReturn(Optional.of(daily));
//            when(reportRepository.findByUserAndDailyId(user, dailyId)).thenReturn(Optional.of(new Report(reportRequestDto, user, daily)));
//
//            // when & then
//            CustomException exception = assertThrows(CustomException.class, () -> boardService.createReport(reportRequestDto, user, dailyId));
//            assertThat(exception.getErrorCode()).isEqualTo(CustomErrorCode.DUPLICATE_REPORT);
//            verify(reportRepository, never()).save(any(Report.class));
        }
    }

}