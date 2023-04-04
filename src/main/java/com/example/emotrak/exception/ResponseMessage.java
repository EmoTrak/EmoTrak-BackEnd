package com.example.emotrak.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor
public class ResponseMessage {
    private final String message;
    private final int statusCode;
    private final String errorCode;
    private final Object data;

    public ResponseMessage(CustomErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.statusCode = errorCode.getHttpStatus().value();
        this.errorCode = errorCode.getErrorCode();
        this.data = null;
    }

    public static ResponseEntity errorResponse(CustomErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseMessage.builder()
                        .message(errorCode.getMessage())
                        .statusCode(errorCode.getHttpStatus().value())
                        .errorCode(errorCode.getErrorCode())
                        .data(null)
                        .build()
                );
    }

    public static ResponseEntity successResponse(HttpStatus httpStatus, String message, Object data) {
        return ResponseEntity
                .status(httpStatus)
                .body(ResponseMessage.builder()
                        .message(message)
                        .statusCode(httpStatus.value())
                        .errorCode(null)
                        .data(data)
                        .build()
                );
    }

}


