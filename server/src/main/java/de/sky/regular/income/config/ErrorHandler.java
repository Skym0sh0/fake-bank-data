package de.sky.regular.income.config;

import de.sky.regular.income.api.Problem;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Slf4j
@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Problem> handleException(RuntimeException ex, HttpServletRequest request) {
        log.error("Handle Runtime Exception", ex);
        return create(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Problem> create(HttpServletRequest request, HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(
                        Problem.builder()
                                .timestamp(OffsetDateTime.now())
                                .path(request.getRequestURI())
                                .status(status.value())
                                .error(status.getReasonPhrase())
                                .errorDetails(message)
                                .build()
                );
    }
}
