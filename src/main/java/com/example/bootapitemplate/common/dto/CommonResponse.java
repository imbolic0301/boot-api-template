package com.example.bootapitemplate.common.dto;

import com.example.bootapitemplate.common.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * API 의 응답은 공통 규격을 지켜야 한다. 응답 코드와 설명 메세지가 필요하다.
 * 응답 코드와 설명 메세지는 기존의 양식을 따라간다. (내부 API 통신 이슈 고려
 *
 * )
 */
public interface CommonResponse {
    HttpStatus getResultStatus();
    String getResultMessage();
    String getResultCode();
    default Map getExternalError() { return null; }
    default CustomException toException() {
        return new CustomException(this);
    }
}
