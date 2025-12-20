package com.omsoft.retail.user.handler;

import com.omsoft.retail.user.exception.ErrorResponse;
import com.omsoft.retail.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {

        ErrorResponse error = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Validation Errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = buildError(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    // Media type error (your issue)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeException(
            HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {

        ErrorResponse error = buildError(
                HttpStatus.NOT_ACCEPTABLE,
                "Requested media type is not supported",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(error);
    }

    private ErrorResponse buildError(HttpStatus status, String message, String path) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setPath(path);
        return error;
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request) {

        ErrorResponse error = buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}

