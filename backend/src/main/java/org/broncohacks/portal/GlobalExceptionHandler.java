package org.broncohacks.portal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");

        return ResponseEntity
                .badRequest()
                .body(new Response(false, message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGeneralException(
            Exception ex) {

        return ResponseEntity
                .internalServerError()
                .body(new Response(
                        false,
                        "Internal server error",
                        null
                ));
    }

    public record Response(
            boolean successful,
            String message,
            Object data
    ) {
    }
}
