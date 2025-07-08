package com.example.bootapitemplate.common.exception;

import com.example.bootapitemplate.common.dto.CommonDto;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Exception 이 발생하면 오류에 관한 공용 응답으로 가공하는 전역 핸들러
 */
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    /**
     * 공용 예외 목록
     */

    // 지정한 ResultCode 가 있는 예외 핸들링    
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(exception);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(
                ServiceExceptionTypes.INVALID_REQUEST_PARAMETER.toException()
        );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleCustomException(BindException exception) {
        printAbout(exception);
        var errorMap =
                exception.getFieldErrors()
                        .stream()
                        .collect(Collectors.toMap(FieldError::getField, v -> v.getRejectedValue()));
        return CommonDto.exceptionResponseFrom(
                ParameterExceptionTypes.INVALID_PARAMETER_FAILURE_MESSAGE.init(
                        String.format("다음의 입력값들을 확인해주세요. : %s", errorMap)
                )
        );
    }

    // 요청 URL 경로에 해당하는 라우터(컨트롤러의 매핑 경로)가 없는 경우
    @ExceptionHandler(value = NoHandlerFoundException.class)
    protected ResponseEntity<?> handleNotFoundPathException(NoHandlerFoundException exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(ServiceExceptionTypes.NOT_FOUND_API.toException());
    }

    // 요청 또는 응답과 관련된 JSON 파싱에서 예외 발생시
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleException(HttpMessageNotReadableException exception) {
         if (exception.contains(JsonParseException.class)) {
            return CommonDto.exceptionResponseFrom(ParameterExceptionTypes.INVALID_JSON_FAILURE_MESSAGE.init(
                    String.format("올바르지 않은 JSON 값입니다. %s", exception.getMessage())));
        }
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(ServiceExceptionTypes.INVALID_REQUEST_PARAMETER.toException());
    }
    
    // 파라미터 오류
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleException(MethodArgumentNotValidException exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(ServiceExceptionTypes.INVALID_REQUEST_PARAMETER.toException());
    }

    // 잘못된 enum 유형 값 파라미터 입력시 (multipart), int 가 들어갈 수 있는 API 경로에 문자열이 들어간 경우 등 전반적인 파라미터 입력 오류시 발생
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleException(MethodArgumentTypeMismatchException exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(ServiceExceptionTypes.INVALID_REQUEST_PARAMETER.toException());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(
                ParameterExceptionTypes.INVALID_PARAMETER_FAILURE_MESSAGE.init(
                        String.format("파라미터가 누락되었습니다. : '%s'", exception.getParameterName())
                )
        );
    }

    // 잘못된 enum 유형 값 파라미터 입력시 (NOT JSON Request)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingServletRequestPartException(MissingServletRequestPartException exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(
                ParameterExceptionTypes.INVALID_PARAMETER_FAILURE_MESSAGE.init(
                        String.format("파라미터가 누락되었습니다. : '%s'", exception.getRequestPartName())
                )
        );
    }

    // 잘못된 @RequestHeader 변수 입력시
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handleMissingServletRequestPartException(MissingRequestHeaderException exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(
                ParameterExceptionTypes.INVALID_PARAMETER_FAILURE_MESSAGE.init(
                        String.format("헤더가 누락되었습니다. : '%s'", exception.getHeaderName())
                )
        );
    }

    // 잘못된 Http Method 입력시
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMissingServletRequestPartException(HttpRequestMethodNotSupportedException exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(ServiceExceptionTypes.NOT_FOUND_API.toException());
    }

    // 잘못된 API 경로 입력시
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException exception) {
        printAbout(exception);
        log.error("cannot found uri : {} - /{}", exception.getHttpMethod().name(), exception.getResourcePath());
        return CommonDto.exceptionResponseFrom(ServiceExceptionTypes.NOT_FOUND_API.toException());
    }

    // 전역 예외 처리, 미리 확인하지 못한 디버깅 용도
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleBaseException(Exception exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(ServiceExceptionTypes.DEBUGGING_NEED_ERROR.toException());
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    public ResponseEntity<?> handleDuplicateKeyException(Exception exception) {
        printAbout(exception);
        return CommonDto.exceptionResponseFrom(ServiceExceptionTypes.DUPLICATED_REQUEST.toException());
    }

    private static void printAbout(Exception exception) {
        if(exception != null) {
            String exceptionCanonicalName = exception.getClass().getCanonicalName();
            log.error("{} occurred at : {}", exceptionCanonicalName, LocalDateTime.now());
            if(exception.getMessage() != null) {
                log.error("stack trace : ", exception);
            }
            if(exception instanceof CustomException customException) {
                log.error(customException.getErrorMessage());
            } else {
                log.error(exception.getMessage());
            }
        } else {
            log.error("exception is null at : {} ", LocalDateTime.now());
        }
    }

}
