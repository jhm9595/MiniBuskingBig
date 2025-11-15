package com.minibuskingbig.common.dto;

import com.minibuskingbig.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private LocalDateTime timestamp;
    private List<FieldErrorDetail> errors;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class FieldErrorDetail {
        private String field;
        private String value;
        private String reason;

        public static FieldErrorDetail of(FieldError fieldError) {
            return new FieldErrorDetail(
                fieldError.getField(),
                fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue().toString(),
                fieldError.getDefaultMessage()
            );
        }
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
            errorCode.getCode(),
            errorCode.getMessage(),
            LocalDateTime.now(),
            new ArrayList<>()
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(
            errorCode.getCode(),
            errorCode.getMessage(),
            LocalDateTime.now(),
            bindingResult.getFieldErrors().stream()
                .map(FieldErrorDetail::of)
                .collect(Collectors.toList())
        );
    }
}
