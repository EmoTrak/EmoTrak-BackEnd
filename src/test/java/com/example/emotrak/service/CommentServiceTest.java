package com.example.emotrak.service;

import com.example.emotrak.dto.comment.CommentRequestDto;
import com.example.emotrak.dto.like.LikeResponseDto;
import com.example.emotrak.dto.report.ReportRequestDto;
import com.example.emotrak.entity.*;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.BoardRepository;
import com.example.emotrak.repository.CommentRepository;
import com.example.emotrak.repository.LikesRepository;
import com.example.emotrak.repository.ReportRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// JUnit 5와 Mockito 가 통합되어 테스트를 수행(단위테스트)
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private LikesRepository likesRepository;

    private Long dailyId;
    private CommentRequestDto commentRequestDto;
    private User user;
    private Comment comment;
    private Long commentId;
    private ReportRequestDto reportRequestDto;

    // 각 테스트 메서드 실행 전에 setUp() 메서드의 호출 및 초기화
    @BeforeEach
    void setUp() {
        dailyId = 1L;
        user = new User("1234", "bambee@gmail.com", "bambee", UserRoleEnum.USER);
        user.setId(1L);
        commentRequestDto = new CommentRequestDto("저는 댓글입니다.");
        commentId = 1L;
        comment = new Comment(commentRequestDto, new Daily(), user);
        reportRequestDto = new ReportRequestDto("신고합니다.");
    }

    @Nested
    @DisplayName("댓글 작성")
    class createComment {
        @Test
        @DisplayName("정상적인 댓글 작성")
        void createCommentSuccess() {
            // given : 테스트에 필요한 데이터를 설정
            when(boardRepository.findById(dailyId)).thenReturn(Optional.of(new Daily()));
            // when : 테스트할 메서드를 호출
            commentService.createComment(dailyId, commentRequestDto, user);
            /* then : 테스트 결과를 검증
             * 호출된 메서드의 결과값을 검증하는 것이 아니라, 메서드가 정상적으로 실행되어서 특정 메서드가 호출되었는지 검증하는 것이 목적
             * verify 메서드를 사용해서 호출 횟수나 파라미터 값을 검증
             */
            verify(commentRepository, times(1)).saveAndFlush(Mockito.any(Comment.class));
        }

        @Test
        @DisplayName("존재하지 않는 게시물에서 댓글 작성")
        void createCommentFail_boardNotFound() {
            // given
            when(boardRepository.findById(dailyId)).thenReturn(Optional.empty());
            // when, then
            assertThrows(CustomException.class, () -> commentService.createComment(dailyId, commentRequestDto, user));
            verify(commentRepository, Mockito.never()).saveAndFlush(Mockito.any(Comment.class));
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class UpdateComment {
        @Test
        @DisplayName("정상적인 댓글 수정")
        void updateCommentSuccess() {
            // given
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            // when
            commentService.updateComment(commentId, commentRequestDto, user);
            // then
            verify(commentRepository, times(1)).save(comment);
        }

        @Test
        @DisplayName("관리자가 댓글을 수정하는 경우")
        void updateCommentSuccessByAdmin() {
            // given
            User admin = new User("1234", "admin@gmail.com", "admin", UserRoleEnum.ADMIN);
            CommentRequestDto updatedCommentRequest = new CommentRequestDto("수정된 댓글 내용입니다.");
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            // when
            commentService.updateComment(commentId, updatedCommentRequest, admin);
            // then
            verify(commentRepository, times(1)).save(comment);
        }

        @Test
        @DisplayName("작성자가 아닌 유저가 댓글을 수정하는 경우")
        void updateCommentFailByNotAuthor() {
            // given
            User otherUser = new User("1234", "other@gmail.com", "other", UserRoleEnum.USER);
            CommentRequestDto newCommentRequestDto = new CommentRequestDto("수정하려는 댓글 내용");
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            // then
            assertThrows(CustomException.class, () -> commentService.updateComment(commentId, newCommentRequestDto, otherUser));
            verify(commentRepository, Mockito.never()).save(comment);
        }

        @Test
        @DisplayName("존재하지 않는 댓글 수정")
        void updateCommentFail_commentNotFound() {
            // given
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
            // when, then
            assertThrows(CustomException.class, () -> commentService.updateComment(commentId, commentRequestDto, user));
            verify(commentRepository, Mockito.never()).save(comment);
        }
    }


    @Nested
    @DisplayName("댓글 삭제")
    class DeleteComment {
        @Test
        @DisplayName("정상적인 댓글 삭제")
        void deleteCommentSuccessByAuthor() {
            // given
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            // when
            commentService.deleteComment(commentId, user);
            // then
            verify(likesRepository, times(1)).deleteAllByComment(comment);
            verify(reportRepository, times(1)).deleteAllByComment(comment);
            verify(commentRepository, times(1)).delete(comment);
        }

        @Test
        @DisplayName("관리자가 댓글을 삭제하는 경우")
        void deleteCommentSuccessByAdmin() {
            // given
            User admin = new User("1234", "admin@gmail.com", "admin", UserRoleEnum.ADMIN);
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            // when
            commentService.deleteComment(commentId, admin);
            // then
            verify(likesRepository, times(1)).deleteAllByComment(comment);
            verify(reportRepository, times(1)).deleteAllByComment(comment);
            verify(commentRepository, times(1)).delete(comment);
        }

        @Test
        @DisplayName("작성자가 아닌 유저가 댓글을 삭제하는 경우")
        void deleteCommentFailByNotAuthor() {
            // given
            User otherUser = new User("1234", "other@gmail.com", "other", UserRoleEnum.USER);
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            // then
            assertThrows(CustomException.class, () -> commentService.deleteComment(commentId, otherUser));
            verify(likesRepository, Mockito.never()).deleteAllByComment(comment);
            verify(reportRepository, Mockito.never()).deleteAllByComment(comment);
            verify(commentRepository, Mockito.never()).delete(comment);
        }

        @Test
        @DisplayName("존재하지 않는 댓글 삭제")
        void deleteCommentFail_commentNotFound() {
            // given
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
            // when, then
            assertThrows(CustomException.class, () -> commentService.deleteComment(commentId, user));
            verify(likesRepository, Mockito.never()).deleteAllByComment(comment);
            verify(reportRepository, Mockito.never()).deleteAllByComment(comment);
            verify(commentRepository, Mockito.never()).delete(comment);
        }
    }

    @Nested
    @DisplayName("댓글 좋아요")
    class LikesComment {
        @Test
        @DisplayName("댓글 좋아요 추가 및 삭제")
        void toggleCommentLike() {
            // given
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(likesRepository.findByUserAndComment(user, comment)).thenReturn(Optional.empty());

            // when: 좋아요 추가
            LikeResponseDto likeResponseDto = commentService.commentLikes(user, commentId);

            // then: 좋아요가 추가되었는지 확인
            assertThat(likeResponseDto.isHasLike()).isTrue();
            verify(likesRepository, times(1)).save(any(Likes.class));
            verify(likesRepository, times(1)).countByComment(comment);

            // given: 이미 좋아요가 눌러진 상태
            when(likesRepository.findByUserAndComment(user, comment)).thenReturn(Optional.of(new Likes(comment, user)));

            // when: 좋아요 취소
            likeResponseDto = commentService.commentLikes(user, commentId);

            // then: 좋아요가 취소되었는지 확인
            assertThat(likeResponseDto.isHasLike()).isFalse();
            verify(likesRepository, times(1)).deleteByUserAndComment(user, comment);
            verify(likesRepository, times(2)).countByComment(comment);
        }

        @Test
        @DisplayName("존재하지 않는 댓글 좋아요.")
        void commentLikesCommentNotFound() {
            // given
            Long commentId = 1L;
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
            // when
            assertThrows(CustomException.class, () -> commentService.commentLikes(user, commentId),
                    "댓글이 존재하지 않는 경우에는 CustomException 이 발생해야 합니다.");
            // then
            verify(likesRepository, Mockito.never()).save(Mockito.any());
            verify(likesRepository, Mockito.never()).delete(Mockito.any());
        }
    }

    @Nested
    @DisplayName("댓글 신고")
    class ReportComment {
        @Test
        @DisplayName("정상적인 댓글 신고")
        void createReport() {
            // given
            Comment comment = new Comment(new CommentRequestDto("댓글 내용"), new Daily(), user);
            comment.setId(1L);
            ReportRequestDto reportRequestDto = new ReportRequestDto("신고 사유");
            when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
            // when
            commentService.createReport(reportRequestDto, user, comment.getId());
            // then
            verify(reportRepository, times(1)).save(Mockito.any(Report.class));
        }

        @Test
        @DisplayName("존재하지 않는 댓글 신고")
        void reportCommentNotFound() {
            // given
            Long commentId = 1L;
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
            // when
            assertThrows(CustomException.class, () -> commentService.createReport(reportRequestDto, user, commentId),
                    "댓글이 존재하지 않는 경우에는 CustomException 이 발생해야 합니다.");
            // then
            verify(reportRepository, Mockito.never()).save(Mockito.any());
        }

        @Test
        @DisplayName("중복되는 댓글 신고")
        void reportCommentDuplicate() {
            // given
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(reportRepository.findByUserAndCommentId(user, commentId)).thenReturn(Optional.of(new Report(reportRequestDto, user, comment)));

            // when & then
            assertThatThrownBy(() -> commentService.createReport(reportRequestDto, user, commentId))
                    .isInstanceOf(CustomException.class)
                    .extracting(ex -> ((CustomException) ex).getErrorCode())
                    .isEqualTo(CustomErrorCode.DUPLICATE_REPORT);
            verify(reportRepository, times(0)).save(any(Report.class));
        }

    }
}
