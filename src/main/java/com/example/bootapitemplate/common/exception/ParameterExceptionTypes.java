package com.example.bootapitemplate.common.exception;

import com.example.bootapitemplate.common.dto.CommonResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 오류 메세지에 커스텀 파라미터가 필요한 경우를 분리하여 관리한다.
 */
@Getter
@AllArgsConstructor
public enum ParameterExceptionTypes implements CommonResponse {

    /**
     * 입력 필드에 대한 오류 공용
     */
    INVALID_PARAMETER_FAILURE_MESSAGE(HttpStatus.BAD_REQUEST, "bad request", "1001"),
    /**
     * 올바르지 못한 JSON 값입니다.
     */
    INVALID_JSON_FAILURE_MESSAGE(HttpStatus.BAD_REQUEST, "invalid JSON request", "1001"),
    /**
     * 외부 API 통신 오류
     */
    EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "external API error", "1001"),
    /**
     * 특수한 경우 출력해야 할 오류(로그성)
     */
    LOG_TYPE_MESSAGE(HttpStatus.SERVICE_UNAVAILABLE, "check log", "9999"),
    ;


    public CustomException init(String errorMessage) {
        this.resultMessage = errorMessage;
        this.resultCode = "1001";
        return this.toException();
    }

    private final HttpStatus resultStatus;
    private String resultMessage;
    private String resultCode;

}
