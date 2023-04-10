package com.example.emotrak.Service;

import com.example.emotrak.dto.CommentRequestDto;
import com.example.emotrak.dto.ReportRequestDto;
import com.example.emotrak.entity.*;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.BoardRepository;
import com.example.emotrak.repository.CommentRepository;
import com.example.emotrak.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.emotrak.entity.UserRoleEnum.ADMIN;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final ReportRepository reportRepository;

    //댓글작성
    public void createComment(Long id, CommentRequestDto commentRequestDto, User user) {
        Daily daily = boardRepository.findById(id).orElseThrow(
                () -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND)
        );
        commentRepository.saveAndFlush(new Comment(commentRequestDto, daily, user));
    }

    //댓글수정
    public void updateComment(Long commentId, CommentRequestDto commentRequestDto, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND)
        );
        validateComment(user, comment);
        comment.updateComment(commentRequestDto);
        commentRepository.save(comment);
    }

    //댓글삭제
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND)
        );
        validateComment(user, comment);
        commentRepository.delete(comment);
    }

    //중복메서드 정리
    private void validateComment(User user, Comment comment) {
        if (comment.getUser().getId() != user.getId() && user.getRole() != ADMIN) {
            throw new CustomException(CustomErrorCode.NOT_AUTHOR);
        }
    }

    //댓글 신고하기
    public void createReport(ReportRequestDto reportRequestDto, User user, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND)
        );
        Optional<Report> existingReport = reportRepository.findByUserAndCommentId(user, commentId);
        if (existingReport.isPresent()) {
            throw new CustomException(CustomErrorCode.DUPLICATE_REPORT); // 이미 신고한 게시물입니다.
        }
        Report report = new Report(reportRequestDto, user, comment);
        reportRepository.save(report);
    }

    // 댓글 신고 삭제하기
    public void deleteReport(User user, Long commentId) {
        Report report = reportRepository.findByUserAndCommentId(user, commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.REPORT_NOT_FOUND));
        reportRepository.delete(report);
    }

}