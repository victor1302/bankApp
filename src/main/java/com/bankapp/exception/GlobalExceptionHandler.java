package com.bankapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler{

    private Map<String, Object> body(HttpStatus status, String message) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        );
    }

    @ExceptionHandler({AuthException.class, BadCredentialException.class})
    public ResponseEntity<Map<String, Object>> handleAuth(RuntimeException ex){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(body(status, ex.getMessage()));
    }

    @ExceptionHandler({UserOrAccountDisabled.class, ForbiddenOperationException.class})
    public ResponseEntity<Map<String, Object>> handleForbidden(RuntimeException ex){
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(body(status, ex.getMessage()));
    }

    @ExceptionHandler({UserNotFoundException.class,
            AccountNotFoundException.class,
            InvoiceNotFoundException.class,
            InstallmentNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex){
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(body(status, ex.getMessage()));
    }

    @ExceptionHandler({AlreadyExistsException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(AlreadyExistsException ex){
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(body(status, ex.getMessage()));
    }

    @ExceptionHandler({AlreadyDisabledOrNotPresent.class,
            AccountDontHaveEnoughMoney.class,
            DestinationAccountRequiredException.class,
            SelfTransferNotAllowedException.class,
            BusinessValidationException.class})
    public ResponseEntity<Map<String, Object>> handleBusiness(RuntimeException ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(body(status, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(body(status, "Unexpected error"));
    }
}
