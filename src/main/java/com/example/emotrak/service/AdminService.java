package com.example.emotrak.service;

import com.example.emotrak.dto.report.ReportHistoryDto;
import com.example.emotrak.dto.report.ReportQueryDto;
import com.example.emotrak.dto.report.ReportResponseDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.Report;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
    private final AdminRepository adminRepository;
    private final BoardRepository boardRepository;
    private final LikesRepository likesRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;

    //신고 게시글 조회
    @Transactional(readOnly = true)
    public ReportResponseDto reportBoard(int page) {
        int size = 15;
        Pageable pageable = PageRequest.of(page-1, size);
        List<ReportQueryDto> reportQueryDtoList = adminRepository.getReportBoard(pageable);

        List<ReportHistoryDto> reportHistoryDtoList = new ArrayList<>();
        long totalCount = 0;

        for(int i = 0; i < reportQueryDtoList.size(); i++) {
            if(i == 0) {
                totalCount = reportQueryDtoList.get(0).getTotalCount();
            }
            ReportHistoryDto reportHistoryDto = new ReportHistoryDto(reportQueryDtoList.get(i));
            reportHistoryDtoList.add(reportHistoryDto);
        }
        return new ReportResponseDto(totalCount, reportHistoryDtoList);
    }

    //신고 댓글 조회
    @Transactional(readOnly = true)
    public ReportResponseDto reportComment(int page) {
        int size = 15;
        Pageable pageable = PageRequest.of(page-1, size);

        List<ReportQueryDto> reportQueryDtoList = adminRepository.getReportComment(pageable);

        List<ReportHistoryDto> reportHistoryDtoList = new ArrayList<>();
        long totalCount = 0;

        for(int i = 0; i < reportQueryDtoList.size(); i++) {
            if(i == 0) {
                totalCount = reportQueryDtoList.get(0).getTotalCount();
            }
            ReportHistoryDto reportHistoryDto = new ReportHistoryDto(reportQueryDtoList.get(i));
            reportHistoryDtoList.add(reportHistoryDto);
        }
        return new ReportResponseDto(totalCount, reportHistoryDtoList);
    }

    // 게시글 공유 중지
    public void restrictBoard(Long boardId) {
        Daily daily = boardRepository.findById(boardId).orElseThrow(
                () -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND)
        );

        if (daily.isHasRestrict()) {
            throw new CustomException(CustomErrorCode.RESTRICT_ERROR);
        }

        // 공유 중단 -> 공유할 수 없도록
        daily.restricted();

        // 댓글 좋아요 날리기
        likesRepository.deleteCommentLike(daily.getId());

        // 댓글 신고 날리기
        reportRepository.deleteCommentByDaily(daily.getId());

        // 댓글 날리기
        commentRepository.deleteByDaily(daily.getId());

        // 게시글 좋아요 날리기
        likesRepository.deleteBoardLike(daily.getId());

        // 해당 게시글 관련 신고 날리기
        reportRepository.deleteAllByDaily(daily);
    }

    // 신고 삭제
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new CustomException(CustomErrorCode.REPORT_NOT_FOUND)
        );
        reportRepository.delete(report);
    }

}
