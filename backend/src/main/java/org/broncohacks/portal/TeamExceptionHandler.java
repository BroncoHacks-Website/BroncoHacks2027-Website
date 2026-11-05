//package org.broncohacks.portal;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class TeamExceptionHandler {
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<TeamResponse> handleValidation(MethodArgumentNotValidException ex) {
//        String firstError = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
//        TeamResponse error = new TeamResponse(false, firstError, null);
//        return ResponseEntity.badRequest().body(error);
//    }
//}