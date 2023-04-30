package com.example.emotrak.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity handleCustomException(CustomException e) {
        return ResponseMessage.errorResponse(e.getErrorCode());
    }

    // HttpMediaTypeNotSupportedException 처리를 위한 메서드
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseMessage.errorResponse(CustomErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseMessage.errorResponse(CustomErrorCode.INVALID_FILE_SIZE);
    }


}

