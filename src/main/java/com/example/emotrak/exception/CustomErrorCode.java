package com.example.emotrak.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    NOT_EMAIL_PATTERN (BAD_REQUEST, "x-1001", "올바른 이메일 형식이 아닙니다."),
    EMAIL_BLANK (BAD_REQUEST, "x-1002", "이메일을 입력해주세요."),
    PASSWORD_BLANK (BAD_REQUEST, "x-1003", "비밀번호를 입력해주세요."),
    NOT_PASSWORD_PATTERN (BAD_REQUEST, "x-1004", "비밀번호 조건을 확인해주세요."),
    USER_NOT_FOUND(BAD_REQUEST, "x-1005", "등록된 사용자가 없습니다."),
    NOT_PROPER_PASSWORD(BAD_REQUEST, "x-1006", "비밀번호가 일치하지 않습니다."),
    NOT_ADMIN (BAD_REQUEST, "x-1007", "관리자 암호가 일치하지 않습니다."),
    FILE_UPLOAD_ERROR(BAD_REQUEST,"x-1008" ,"파일 업로드 중 에러가 발생했습니다." ),
    NICKNAME_BLANK (BAD_REQUEST, "x-1009", "닉네임을 입력해주세요."),
    FILE_DELETION_ERROR(BAD_REQUEST, "x-1010", "파일 삭제 중 에러가 발생했습니다."),
    RESTRICT_ERROR(BAD_REQUEST, "x-1011", "공유 중지된 글입니다."),
    NOT_NICKNAME_PATTERN (BAD_REQUEST, "x-1012", "닉네임 조건을 확인해주세요."),
    AWS_SERVICE_ERROR(BAD_REQUEST,"x-1013","AWS 서비스 관련 오류가 발생했습니다."),
    AWS_CLIENT_ERROR(BAD_REQUEST,"x-1013","AWS 클라이언트 관련 오류가 발생했습니다."),
    INVALID_FILE_TYPE(BAD_REQUEST,"x-1014","파일 크기 또는 파일 형식이 유효하지않습니다"),


    /* 401 UNAUTHORIZED : 권한 없음 */
    INVALID_TOKEN(UNAUTHORIZED, "x-1001", "Expired JWT token"),
    NON_TOKEN(UNAUTHORIZED, "x-1002", "로그인이 필요합니다."),
    REFRESH_TOKEN_IS_EXPIRED(UNAUTHORIZED, "x-1003", "리프레쉬 토큰이 만료되었습니다."),
    NOT_AUTHOR(UNAUTHORIZED, "x-1004", "작성자만 수정/삭제가 가능합니다."),
    UNAUTHORIZED_ACCESS(UNAUTHORIZED,"x-1005", "권한이 없습니다."),


    /* 403 FORBIDDEN : 권한 */


    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    BOARD_NOT_FOUND(NOT_FOUND, "x-1001", "선택한 게시물을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(NOT_FOUND, "x-1002", "선택한 댓글을 찾을 수 없습니다."),
    CONTENT_NOT_FOUND(NOT_FOUND, "x-1003", "찾을 수 없는 유형입니다."),
    REPORT_NOT_FOUND(NOT_FOUND,"x-1004","신고내역을 찾을 수 없습니다"),


    /* 409 CONFLICT : 중복 */
    DUPLICATE_EMAIL(CONFLICT, "x-1001", "중복된 이메일이 존재합니다"),
    DUPLICATE_NICKNAME(CONFLICT, "x-1002", "중복된 닉네임이 존재합니다"),
    DUPLICATE_REPORT(CONFLICT,"x-1003" ,"이미 신고한 게시물입니다" ),
    SAME_PASSWORD(CONFLICT, "x-1004", "패스워드가 동일합니다"),
    SAME_NICKNAME(CONFLICT, "x-1005", "닉네임이 동일합니다"),
    DATA_INTEGRITY_VIOLATION (CONFLICT, "x-1006", "데이터 무결성 위반"),

    /* 415 UNSUPPORTED_MEDIA_TYPE : 미디어타입 오류 */
    UNSUPPORTED_MEDIA_TYPE (HttpStatus.UNSUPPORTED_MEDIA_TYPE, "x-1001", "지원되지 않는 미디어 타입입니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

}