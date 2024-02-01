package de.sky.regular.income.config;

import de.sky.regular.income.api.Problem;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Problem> handleException(RuntimeException ex, HttpServletRequest request) {
        return create(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Problem> create(HttpServletRequest request, HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new Problem(ZonedDateTime.now(), request.getRequestURI(), status.value(), status.getReasonPhrase(), message));
    }
}
