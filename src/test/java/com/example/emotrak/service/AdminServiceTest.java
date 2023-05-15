package com.example.emotrak.service;

import com.example.emotrak.dto.report.ReportHistoryDto;
import com.example.emotrak.dto.report.ReportQueryDto;
import com.example.emotrak.dto.report.ReportQueryDtoImpl;
import com.example.emotrak.dto.report.ReportResponseDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.Report;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private ReportRepository reportRepository;

    private Long boardId = 1L;
    private Daily daily;
    private Report report;
    private int page;
    private long totalCount;

    @BeforeEach
    void setUp() {
        daily = new Daily();
        daily.setId(boardId);

        report = new Report();
        report.setId(1L);

    }

    @Nested
    @DisplayName("관리자 페이지 조회")
    class GetAdmin {
        @Test
        @DisplayName("신고 게시물 조회 성공")
        public void reportBoard() {
            // given
            totalCount = 2L;
            page = 1;

            List<ReportQueryDto> expectedReportQueryDtoList = new ArrayList<>();
            ReportQueryDto reportQueryDto1 = new ReportQueryDtoImpl();
            reportQueryDto1.setTotalCount(totalCount);
            reportQueryDto1.setReportId(10L);
            reportQueryDto1.setId(1L);
            reportQueryDto1.setNickname("주지스님1");
            reportQueryDto1.setEmail("asfd1@naver.com");
            reportQueryDto1.setReason("신고사유1");
            reportQueryDto1.serCount(1L);
            expectedReportQueryDtoList.add(reportQueryDto1);

            ReportQueryDto reportQueryDto2 = new ReportQueryDtoImpl();
            reportQueryDto2.setTotalCount(totalCount);
            reportQueryDto2.setReportId(20L);
            reportQueryDto2.setId(2L);
            reportQueryDto2.setNickname("주지스님2");
            reportQueryDto2.setEmail("asfd2@naver.com");
            reportQueryDto2.setReason("신고사유2");
            reportQueryDto2.serCount(1L);
            expectedReportQueryDtoList.add(reportQueryDto2);

            ReportHistoryDto reportHistory1 = new ReportHistoryDto(reportQueryDto1);
            ReportHistoryDto reportHistory2 = new ReportHistoryDto(reportQueryDto2);
            List<ReportHistoryDto> reportHistoryList = new ArrayList<>();
            reportHistoryList.add(reportHistory1);
            reportHistoryList.add(reportHistory2);

            ReportResponseDto expectedReportResponseDto = new ReportResponseDto(totalCount, reportHistoryList);

            Pageable pageable = PageRequest.of(page - 1, 15);
            when(adminRepository.getReportBoard(pageable)).thenReturn(expectedReportQueryDtoList);

            // when
            ReportResponseDto reportResponseDto = adminService.reportBoard(page);

            // then
            assertAll(
                    () -> assertEquals(expectedReportResponseDto.getTotalCount(), reportResponseDto.getTotalCount(),
                            "총 신고 개수가 다릅니다."),
                    () -> assertEquals(expectedReportResponseDto.getContents().size(),
                            reportResponseDto.getContents().size(), "리스트 크기가 다릅니다.")
            );
            for (int i = 0; i < expectedReportResponseDto.getContents().size(); i++) {
                ReportHistoryDto expectedReportHistoryDto = expectedReportResponseDto.getContents().get(i);
                ReportHistoryDto actualReportHistoryDto = reportResponseDto.getContents().get(i);

                assertAll(
                        () -> assertEquals(expectedReportHistoryDto.getReportId(), actualReportHistoryDto.getReportId(),
                                "신고한 게시물 아이디가 다릅니다."),
                        () -> assertEquals(expectedReportHistoryDto.getId(), actualReportHistoryDto.getId(),
                                "게시물 신고자 아이디가 다릅니다."),
                        () -> assertEquals(expectedReportHistoryDto.getNickname(), actualReportHistoryDto.getNickname(),
                                "게시물 신고자 회원 닉네임이 다릅니다."),
                        () -> assertEquals(expectedReportHistoryDto.getEmail(), actualReportHistoryDto.getEmail(),
                                "게시물 신고자 회원 이메일이 다릅니다."),
                        () -> assertEquals(expectedReportHistoryDto.getReason(), actualReportHistoryDto.getReason(),
                                "게시물 신고 이유가 다릅니다."),
                        () -> assertEquals(expectedReportHistoryDto.getCount(), actualReportHistoryDto.getCount(),
                                "게시물 신고 횟수가 다릅니다.")
                );
            }
        }

        @Test
        @DisplayName("신고 댓글 조회 성공")
        public void reportComment() {
            // given
            totalCount = 2L;
            page = 1;

            List<ReportQueryDto> expectedReportQueryDtoList = new ArrayList<>();
            ReportQueryDto reportQueryDto1 = new ReportQueryDtoImpl();
            reportQueryDto1.setTotalCount(totalCount);
            reportQueryDto1.setReportId(10L);
            reportQueryDto1.setId(1L);
            reportQueryDto1.setNickname("주지스님1");
            reportQueryDto1.setEmail("asfd1@naver.com");
            reportQueryDto1.setReason("신고사유1");
            reportQueryDto1.serCount(1L);
            expectedReportQueryDtoList.add(reportQueryDto1);

            ReportQueryDto reportQueryDto2 = new ReportQueryDtoImpl();
            reportQueryDto2.setTotalCount(totalCount);
            reportQueryDto2.setReportId(20L);
            reportQueryDto2.setId(2L);
            reportQueryDto2.setNickname("주지스님2");
            reportQueryDto2.setEmail("asfd2@naver.com");
            reportQueryDto2.setReason("신고사유2");
            reportQueryDto2.serCount(1L);
            expectedReportQueryDtoList.add(reportQueryDto2);

            ReportHistoryDto reportHistory1 = new ReportHistoryDto(reportQueryDto1);
            ReportHistoryDto reportHistory2 = new ReportHistoryDto(reportQueryDto2);
            List<ReportHistoryDto> reportHistoryList = new ArrayList<>();
            reportHistoryList.add(reportHistory1);
            reportHistoryList.add(reportHistory2);

            ReportResponseDto expectedReportResponseDto = new ReportResponseDto(totalCount, reportHistoryList);

            Pageable pageable = PageRequest.of(page - 1, 15);
            when(adminRepository.getReportComment(pageable)).thenReturn(expectedReportQueryDtoList);

            // when
            ReportResponseDto reportResponseDto = adminService.reportComment(page);

            // then
            assertAll(
                    () -> assertEquals(expectedReportResponseDto.getTotalCount(), reportResponseDto.getTotalCount(),
                            "총 신고 개수가 다릅니다."),
                    () -> assertEquals(expectedReportResponseDto.getContents().size(),
                            reportResponseDto.getContents().size(), "리스트 크기가 다릅니다.")
            );
            for (int i = 0; i < expectedReportResponseDto.getContents().size(); i++) {
                ReportHistoryDto expectedReportHistoryDto = expectedReportResponseDto.getContents().get(i);
                ReportHistoryDto actualReportHistoryDto = reportResponseDto.getContents().get(i);

                assertAll(
                        () -> assertEquals(expectedReportHistoryDto.getReportId(), actualReportHistoryDto.getReportId(),
                                "신고한 댓글 아이디가 다릅니다."),
                        () -> assertEquals(expectedReportHistoryDto.getId(), actualReportHistoryDto.getId(),
                                "댓글 신고자 아이디가 다릅니다."),
                        () -> assertEquals(expectedReportHistoryDto.getNickname(), actualReportHistoryDto.getNickname(),
                                "댓글 신고자 회원 닉네임이 다릅니다."),
                        () -> assertEquals(expectedReportHistoryDto.getEmail(), actualReportHistoryDto.getEmail(),
                                "댓글 신고자 회원 이메일이 다릅니다."),
                        () -> assertEquals(expectedReportHistoryDto.getReason(), actualReportHistoryDto.getReason(),
                                "댓글 신고 이유가 다릅니다."),
                        () -> assertEquals(expectedReportHistoryDto.getCount(), actualReportHistoryDto.getCount(),
                                "댓글 신고 횟수가 다릅니다.")
                );
            }
        }
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