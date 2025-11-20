package com.bankapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice

public class GlobalExceptionHandler{

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(AlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<String> handleBadCredentialException(BadCredentialException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(AlreadyDisabledOrNotPresent.class)
    public ResponseEntity<String> handleUserAlreadyIsDisableOrNotPresent(AlreadyDisabledOrNotPresent ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(UserOrAccountDisabled.class)
    public ResponseEntity<String> handleUserOrAccountDisabled(UserOrAccountDisabled ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}
