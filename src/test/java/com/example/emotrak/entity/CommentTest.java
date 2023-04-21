package com.example.emotrak.entity;

import com.example.emotrak.dto.comment.CommentRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    @DisplayName("댓글 내용을 업데이트할 수 있다.")
    void updateComment() {
        // given
        CommentRequestDto commentRequestDto = new CommentRequestDto("Updated Comment");
        User user = new User();
        Daily daily = new Daily();
        Comment comment = new Comment(commentRequestDto, daily, user);

        // when
        comment.updateComment(commentRequestDto);

        // then
        String updatedComment = comment.getComment();
        Assertions.assertEquals("Updated Comment", updatedComment, "댓글 내용이 예상과 다릅니다.");
    }

    @Test
    @DisplayName("댓글 내용이 null일 경우, NullPointerException이 발생한다.")
    public void testUpdateCommentWithNull() {
        // given
        CommentRequestDto requestDto = new CommentRequestDto(null);
        User user = new User();
        Daily daily = new Daily();
        Comment comment = new Comment(requestDto, daily, user);

        // when, then
        Assertions.assertThrows(NullPointerException.class, () -> {
            comment.updateComment(requestDto);
        }, "댓글 내용이 null일 경우, NullPointerException이 발생해야 합니다.");
    }

    @Test
    @DisplayName("댓글 작성자가 null일 경우, NullPointerException이 발생한다.")
    public void testCommentWithNullUser() {
        // given
        CommentRequestDto requestDto = new CommentRequestDto("test comment");
        User user = null;
        Daily daily = new Daily();

        // when, then
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Comment(requestDto, daily, user);
        }, "댓글 작성자가 null일 경우, NullPointerException이 발생해야 합니다.");
    }

    @Test
    @DisplayName("댓글이 작성된 게시물이 null일 경우, NullPointerException이 발생한다.")
    public void testCommentWithNullDaily() {
        // given
        CommentRequestDto requestDto = new CommentRequestDto("test comment");
        User user = new User();
        Daily daily = null;

        // when, then
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Comment(requestDto, daily, user);
        }, "댓글이 작성된 게시물이 null일 경우, NullPointerException이 발생해야 합니다.");
    }

    @Test
    @DisplayName("CommentRequestDto 객체에서 comment 필드가 null일 경우, NullPointerException이 발생하는지 확인하는 테스트")
    public void testCommentRequestDtoWithNullComment() {
        // given
        CommentRequestDto requestDto = new CommentRequestDto(null);
        User user = new User();
        Daily daily = new Daily();

        // when, then
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Comment(requestDto, daily, user);
        }, "CommentRequestDto 객체에서 comment 필드가 null일 경우, NullPointerException이 발생해야 합니다.");
    }

    @Test
    @DisplayName("Comment 객체를 생성한 후, getId 메소드를 호출하여 id 필드 값이 null이 아닌지 확인하는 테스트")
    public void testCommentIdIsNotNull() {
        // given
        CommentRequestDto requestDto = new CommentRequestDto("test comment");
        User user = new User();
        Daily daily = new Daily();
        Comment comment = new Comment(requestDto, daily, user);

        // when
        Long id = comment.getId();

        // then
        Assertions.assertNotNull(id, "Comment 객체의 id 필드 값이 null이 아니어야 합니다.");
    }

    @Test
    @DisplayName("Comment 객체를 생성한 후, getUser 메소드를 호출하여 user 필드가 예상한 대로 설정되는지 확인하는 테스트")
    public void testCommentGetUser() {
        // given
        CommentRequestDto requestDto = new CommentRequestDto("test comment");
        User user = new User();
        Daily daily = new Daily();
        Comment comment = new Comment(requestDto, daily, user);

        // when
        User commentUser = comment.getUser();

        // then
        Assertions.assertEquals(user, commentUser, "Comment 객체의 user 필드 값이 예상한 대로 설정되지 않았습니다.");
    }

    @Test
    @DisplayName("Comment 객체를 생성한 후, getDaily 메소드를 호출하여 daily 필드가 예상한 대로 설정되는지 확인하는 테스트")
    public void testCommentGetDaily() {
        // given
        CommentRequestDto requestDto = new CommentRequestDto("test comment");
        User user = new User();
        Daily daily = new Daily();
        Comment comment = new Comment(requestDto, daily, user);

        // when
        Daily commentDaily = comment.getDaily();

        // then
        Assertions.assertEquals(daily, commentDaily, "Comment 객체의 daily 필드 값이 예상한 대로 설정되지 않았습니다.");
    }



}