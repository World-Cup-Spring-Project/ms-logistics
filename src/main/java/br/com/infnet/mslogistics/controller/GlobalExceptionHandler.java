package br.com.infnet.mslogistics.controller;

import br.com.infnet.mslogistics.dto.ErrorResponse;
import br.com.infnet.mslogistics.exception.BookingConflictException;
import br.com.infnet.mslogistics.exception.CoreDataUnavailableException;
import br.com.infnet.mslogistics.exception.InvalidTeamException;
import br.com.infnet.mslogistics.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(BookingConflictException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidTeamException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTeam(InvalidTeamException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "INVALID_TEAM", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CoreDataUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUnavailable(CoreDataUnavailableException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Retry-After", "30") // Instrução para os clientes tentarem de novo depois de 30 seg
                .body(new ErrorResponse(503, "SERVICE_UNAVAILABLE", ex.getMessage(), request.getRequestURI(), Instant.now(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildError(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", details, request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String code, String message, String path) {
        // Passando 'null' como 6º parâmetro para satisfazer a lista de erros de campo gerada pelo Claude Code
        ErrorResponse error = new ErrorResponse(status.value(), code, message, path, Instant.now(), null);
        return ResponseEntity.status(status).body(error);
    }
}