package com.example.emotrak.Service;

import com.example.emotrak.dto.CommentRequestDto;
import com.example.emotrak.entity.Comment;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.BoardRepository;
import com.example.emotrak.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    //댓글작성
    public void createComment(Long id, CommentRequestDto commentRequestDto, User user) {
        validateUserToken(user);
        Daily daily = boardRepository.findById(id).orElseThrow(
                () -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND)
        );
        Comment comment = commentRepository.saveAndFlush(new Comment(commentRequestDto, daily, user));
        boardRepository.save(daily);
    }

    //댓글수정
    public void updateComment(Long commentId, CommentRequestDto commentRequestDto, User user) {
        validateUserToken(user);
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND)
        );
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(CustomErrorCode.NOT_AUTHOR);
        }
        comment.updateComment(commentRequestDto);
        commentRepository.save(comment);
    }

    //댓글삭제
    public void deleteComment(Long commentId, User user) {
        validateUserToken(user);
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND)
        );
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(CustomErrorCode.NOT_AUTHOR);
        }
        commentRepository.delete(comment);
    }

    //중복메서드 정리
    private void validateUserToken(User user) {
        if (user == null) {
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
        }
    }
}