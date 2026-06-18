package br.com.infnet.mslogistics.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String code,
        String message,
        String path,
        Instant timestamp,
        List<FieldError> errors
) {
    public static ErrorResponse of(int status, String code, String message, String path) {
        return new ErrorResponse(status, code, message, path, Instant.now(), null);
    }

    public static ErrorResponse of(int status, String code, String message, String path, List<FieldError> errors) {
        return new ErrorResponse(status, code, message, path, Instant.now(), errors);
    }

    public record FieldError(String field, String message) {}
}
