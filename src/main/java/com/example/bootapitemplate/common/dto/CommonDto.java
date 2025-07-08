package com.example.bootapitemplate.common.dto;

import com.example.bootapitemplate.common.exception.CustomException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 공용 Response Body
 */
@Slf4j
public class CommonDto {

    /**
     * 정상 호출에 대해, 데이터가 있는 공용 응답을 생성한다.
     * @param data : API 응답 결과로 리턴할 객체 (DTO Response 등)
     */
    public static <T> ResponseEntity<Response<T>> responseFrom(T data) {
        return ResponseEntity.ok(Response.from(data));
    }

    /**
     * 정상 호출에 대해, 데이터가 없는 공용 응답을 생성한다.
     */
    public static ResponseEntity<?> emptyResponse() {
        return ResponseEntity.ok(Response.from(null));
    }


    /**
     * 커스텀 예외로부터 오류 관련 공용 응답을 생성한다.
     */
    public static ResponseEntity<?> exceptionResponseFrom(CustomException exception) {
        log.error("custom error : {} - {} ", exception.getErrorCode(), exception.getErrorMessage());
        return ResponseEntity.status(exception.getResultStatus()).body(ExternalErrorResponse.from(exception));
    }

    // 공용 JSON body 를 만들기 위한 내부 데이터 클래스
    @ToString
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Response<T> {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final T data;
        private static <T> Response<T> from(T data) {
            return Response.<T>builder()
                    .data(data)
                    .build();
        }

    }

    // 공용 JSON body 를 만들기 위한 내부 데이터 클래스
    @ToString
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListResponse<T> {
        private final PageInfo pageInfo;
        private final List<T> list;

        public static <T> ListResponse<T> from(List<T> list, PageInfo pageInfo) {
            return ListResponse.<T>builder()
                    .list(list)
                    .pageInfo(pageInfo)
                    .build();
        }

        public static <T> ListResponse<T> from(List<T> list) {
            return ListResponse.<T>builder()
                    .list(list)
                    .pageInfo(null)
                    .build();
        }

    }

    @Builder
    @Getter
    public static class ExternalErrorResponse {
        private final String errorCode;
        private final String errorMessage;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final String externalError;

        public static ExternalErrorResponse from(CustomException exception) {
            return ExternalErrorResponse.builder()
                    .errorCode(exception.getErrorCode())
                    .errorMessage(exception.getErrorMessage())
                    .externalError(exception.getExternalError())
                    .build();
        }
    }


    @Builder
    public record PageInfo(
            long totalCount
            , int page
            , int showCount
    ) {
        public static PageInfo empty(Integer page, Integer showCount) {
            return PageInfo.builder()
                    .totalCount(0).page(page).showCount(showCount)
                    .build();
        }
    }

}
