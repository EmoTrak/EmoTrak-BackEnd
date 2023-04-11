package com.example.emotrak.Service;

import com.example.emotrak.dto.ReportBoardResponseDto;
import com.example.emotrak.dto.ReportCommentResponseDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.AdminRepository;
import com.example.emotrak.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
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

    //신고 게시글 조회
    public List<ReportBoardResponseDto> reportBoard(User user) {
        if (user.getRole() != UserRoleEnum.ADMIN) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<Object[]> objectList = adminRepository.getReportBoard();

        List<ReportBoardResponseDto> reportBoardResponseDtoList = new ArrayList<>();
        ReportBoardResponseDto reportBoardResponseDto = null;
        for (Object[] object : objectList) {
            Long id = ((BigInteger) object[0]).longValue();
            String reason = (String) object[2];

            if (reportBoardResponseDto == null || reportBoardResponseDto.getId() != id) {
                reportBoardResponseDto = new ReportBoardResponseDto(object);
                reportBoardResponseDtoList.add(reportBoardResponseDto);
            }
            reportBoardResponseDto.addReasom(reason);
        }
        return reportBoardResponseDtoList;
    }

    public List<ReportCommentResponseDto> reportComment(User user) {
        if (user.getRole() != UserRoleEnum.ADMIN) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_ACCESS);
        }
        List<Object[]> objectList = adminRepository.getReportComment();

        List<ReportCommentResponseDto> reportCommentResponseDtoList = new ArrayList<>();
        ReportCommentResponseDto reportCommentResponseDto = null;

        for (Object[] object : objectList) {
            Long id = ((BigInteger) object[0]).longValue();
            String reason = (String) object[2];
            if (reportCommentResponseDto == null || reportCommentResponseDto.getId() != id) {
                reportCommentResponseDto = new ReportCommentResponseDto(object);
                reportCommentResponseDtoList.add(reportCommentResponseDto);
            }
            reportCommentResponseDto.addComment(reason);
        }
        return reportCommentResponseDtoList;
    }


    public void restrictBoard(Long boardId, User user) {
        if (user.getRole() != UserRoleEnum.ADMIN) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_ACCESS);
        }

        Daily daily = boardRepository.findById(boardId).orElseThrow(
                () -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND)
        );

        if (daily.isHasRestrict()) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_ACCESS);
        }
        daily.restricted();
    }
}
