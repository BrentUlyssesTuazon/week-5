package com.fintech.walletapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Existing Week 4 Validation Handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Extract the default validation message we wrote in our DTO
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();

        ErrorResponseDTO error = new ErrorResponseDTO(400, "Validation Failed", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Existing Catcher 2: For custom "Not Found" situations (e.g., Account ID 999)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(IllegalArgumentException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Week 5: Bad Credentials (wrong username or password during /login) -> 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Invalid username or password"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Week 5: Access Denied (failed @PreAuthorize / @PostAuthorize rule) -> 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "You do not have permission to access this resource"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // Week 5: Broken JSON body or type mismatch in URL -> 400
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Malformed JSON request or invalid value in the URL"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Week 5: Catch-all safety net
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleEverythingElse(Exception ex) {
        // 1. Spring's built-in web errors keep their specific status code (404, 405, etc.)
        if (ex instanceof ErrorResponse) {
            int status = ((ErrorResponse) ex).getStatusCode().value();
            return ResponseEntity.status(status)
                    .body(new ErrorResponseDTO(status, "Request Error", ex.getMessage()));
        }

        // 2. Uncaught security errors return clean 401
        if (ex instanceof AuthenticationException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO(401, "Unauthorized", "Access denied: No token or invalid token provided"));
        }

        // 3. Unexpected application errors -> clean generic 500
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please contact support."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}