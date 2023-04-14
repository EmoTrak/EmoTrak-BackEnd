package com.example.emotrak.Service;

import com.example.emotrak.dto.ReportResponseDto;

import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.Report;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.*;
import lombok.RequiredArgsConstructor;
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
    public List<ReportResponseDto> reportBoard() {
        List<Object[]> objectList = adminRepository.getReportBoard();

        List<ReportResponseDto> reportBoardResponseDtoList = new ArrayList<>();
        for (Object[] object : objectList) {
            ReportResponseDto reportResponseDto = new ReportResponseDto(object);
            reportBoardResponseDtoList.add(reportResponseDto);
        }
        return reportBoardResponseDtoList;
    }

    //신고 댓글 조회
    public List<ReportResponseDto> reportComment() {
        List<Object[]> objectList = adminRepository.getReportComment();

        List<ReportResponseDto> reportBoardResponseDtoList = new ArrayList<>();

        for (Object[] object : objectList) {
            ReportResponseDto reportResponseDto = new ReportResponseDto(object);
            reportBoardResponseDtoList.add(reportResponseDto);
        }
        return reportBoardResponseDtoList;
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
        reportRepository.deleteByDaily(daily.getId());

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
