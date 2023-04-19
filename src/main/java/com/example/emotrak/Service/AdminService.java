package com.example.emotrak.Service;

import com.example.emotrak.dto.ReportHistory;
import com.example.emotrak.dto.ReportResponseDto;

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

import java.math.BigInteger;
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
        Pageable pageable = PageRequest.of(page-1, size);  //첫번째 파라미터값 = 첫번째 페이지, 두번째 파라미터값 = 한페이지당 들어가는 데이터 갯수
                                                         //페이지 정보와 사이즈 정보를 담은 객체생성
        List<Object[]> objectList = adminRepository.getReportBoard(pageable);   //해당 페이지의 목록을 조회

        List<ReportHistory> reportHistoryList = new ArrayList<>();
        long totalCount = 0;

        for(int i = 0; i < objectList.size(); i++) {
            if(i == 0) {
                totalCount = ((BigInteger) objectList.get(0)[0]).longValue();
            }
            ReportHistory reportHistory = new ReportHistory(objectList.get(i));
            reportHistoryList.add(reportHistory);
        }
        ReportResponseDto reportResponseDto = new ReportResponseDto(totalCount, reportHistoryList);

        return reportResponseDto;
    }

    //신고 댓글 조회
    @Transactional(readOnly = true)
    public ReportResponseDto reportComment(int page) {
        int size = 15;
        Pageable pageable = PageRequest.of(page-1, size);

        List<Object[]> objectList = adminRepository.getReportComment(pageable);

        List<ReportHistory> reportHistoryList = new ArrayList<>();
        long totalCount = 0;

        for(int i = 0; i < objectList.size(); i++) {
            if(i == 0) {
                totalCount = ((BigInteger)objectList.get(0)[0]).longValue();
            }
            ReportHistory reportHistory = new ReportHistory(objectList.get(i));
            reportHistoryList.add(reportHistory);
        }
        ReportResponseDto reportResponseDto = new ReportResponseDto(totalCount,reportHistoryList);

        return reportResponseDto;
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
