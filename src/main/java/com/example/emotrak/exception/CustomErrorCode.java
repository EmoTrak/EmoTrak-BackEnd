package com.example.emotrak.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_TOKEN(UNAUTHORIZED, "x-1001", "토큰이 유효하지 않습니다"),
    EXPIRE_TOKEN(FORBIDDEN, "x-1002", "토큰이 만료되었습니다"),
    NOT_PROPER_PASSWORD(BAD_REQUEST, "x-1001", "비밀번호가 일치하지 않습니다."),
    DUPLICATE_EMAIL(BAD_REQUEST, "x-1002", "중복된 이메일이 존재합니다"),
    NOT_LENGTH(BAD_REQUEST, "x-1001","입력한 문자열 길이가 맞지 않습니다."),
    NOT_PROPER_URLFORM(BAD_REQUEST, "x-1001", "입력한 URL 형식이 맞지 않습니다."),
    NOT_AUTHOR(BAD_REQUEST, "x-1001", "작성자만 삭제/수정할 수 있습니다."),
    NOT_INPUT(BAD_REQUEST, "x-1001", "입력한 문자열 조건이 맞지 않습니다."),
    NOT_ADMIN (BAD_REQUEST, "x-1001", "관리자 암호가 아닙니다.."),
    NOT_EMAIL_PATTERN (BAD_REQUEST, "x-1001", "올바른 이메일 형식이 아닙니다."),
    NOT_PASSWORD_PATTERN (BAD_REQUEST, "x-1001", "숫자와 영어 소문자와 특수문자를 사용해 8-15자리 비밀번호를 입력해주세요."),

    EMAIL_BLANK (BAD_REQUEST, "x-1001", "이메일을 입력해주세요."),
    PASSWORD_BLANK (BAD_REQUEST, "x-1001", "비밀번호를 입력해주세요."),
    FILE_UPLOAD_ERROR(BAD_REQUEST,"x-1001" ,"파일 업로드 중 에러가 발생했습니다" ),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    USER_NOT_FOUND(NOT_FOUND, "x-1001", "등록된 사용자가 없습니다"),
    BOARD_NOT_FOUND(NOT_FOUND, "x-1001", "선택한 게시물을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(NOT_FOUND, "x-1001", "선택한 댓글을 찾을 수 없습니다."),
    SECRET_KEY_NOT_FOUND(NOT_FOUND, "x-1001", "해당 시크릿 키는 존재하지 않습니다."),
    BOARD_TYPE_NOT_FOUND(NOT_FOUND, "x-1001", "게시글 유형을 찾을 수 없습니다."),
    CONTENT_NOT_FOUND(NOT_FOUND, "x-1001", "마지막 게시글입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

}

