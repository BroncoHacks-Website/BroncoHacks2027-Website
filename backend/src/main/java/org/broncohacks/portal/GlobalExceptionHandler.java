package org.broncohacks.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleMalformedRequest(
            HttpMessageNotReadableException ex) {

        return ResponseEntity
                .badRequest()
                .body(new Response(
                        false,
                        "Malformed request body",
                        null
                ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Response> handleNotFound(
            NoResourceFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new Response(
                        false,
                        "Not found",
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGeneralException(
            Exception ex) {

        logger.error("Unhandled error", ex);

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
