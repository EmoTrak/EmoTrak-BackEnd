package com.example.emotrak.service;

import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.Report;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.BoardRepository;
import com.example.emotrak.repository.CommentRepository;
import com.example.emotrak.repository.LikesRepository;
import com.example.emotrak.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private ReportRepository reportRepository;

    private Long boardId = 1L;
    private Daily daily;
    private Report report;

    @BeforeEach
    void setUp() {
        daily = new Daily();
        daily.setId(boardId);

        report = new Report();
        report.setId(1L);
    }

    @Nested
    @DisplayName("공유 중지")
    class restrictBoard {
        @Test
        @DisplayName("성공 케이스")
        public void restrictBoard() {
            // given
            Mockito.when(boardRepository.findById(boardId)).thenReturn(Optional.of(daily));

            // when
            adminService.restrictBoard(boardId);

            // then
            Mockito.verify(boardRepository).findById(boardId);
            Mockito.verify(likesRepository).deleteCommentLike(boardId);
            Mockito.verify(reportRepository).deleteCommentByDaily(boardId);
            Mockito.verify(commentRepository).deleteByDaily(boardId);
            Mockito.verify(likesRepository).deleteBoardLike(boardId);
            Mockito.verify(reportRepository).deleteAllByDaily(daily);
            assertEquals(daily.isHasRestrict(), true);
        }
        @Nested
        @DisplayName("실패 케이스")
        class restrictBoardFail {
            @Test
            @DisplayName("선택한 게시물이 없음")
            public void restrictBoard_NotFound() {
                // when
                CustomException customException = assertThrows(CustomException.class, () -> {
                    adminService.restrictBoard(boardId);
                });

                // then
                assertEquals("선택한 게시물을 찾을 수 없습니다.", customException.getErrorCode().getMessage());
            }

            @Test
            @DisplayName("이미 공유 중지됨")
            public void restrictBoard_Restricted() {
                // given
                daily.setHasRestrict(true);
                Mockito.when(boardRepository.findById(boardId)).thenReturn(Optional.of(daily));

                // when
                CustomException customException = assertThrows(CustomException.class, () -> {
                    adminService.restrictBoard(boardId);
                });

                // then
                assertEquals("공유 중지된 글입니다.", customException.getErrorCode().getMessage());
            }
        }
    }

    @Nested
    @DisplayName("신고 삭제")
    class deleteReport {
        @Test
        @DisplayName("성공 케이스")
        public void deleteReport() {
            when(reportRepository.findById(report.getId())).thenReturn(Optional.of(report));

            adminService.deleteReport(report.getId());
            verify(reportRepository, times(1)).delete(report);
        }

        @Nested
        @DisplayName("실패 케이스")
        class deleteReportFail {
            @Test
            @DisplayName("삭제할 신고가 없음")
            public void deleteReport_NotFound() {
                CustomException customException = assertThrows(CustomException.class, () -> {
                    adminService.deleteReport(report.getId());
                });
                assertEquals("신고내역을 찾을 수 없습니다.", customException.getErrorCode().getMessage());

                verify(reportRepository, times(0)).delete(report);
            }

        }
    }
}