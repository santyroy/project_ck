package dev.roy.coinkeeper.exception;

import dev.roy.coinkeeper.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class AppExceptionHandler {

    private static final String MESSAGE = "message";

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(BindingResult result,
                                                                             MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, 400, "Missing Mandatory fields", errors));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(ex.getMessage());
        Map<String, String> violations = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                violations.put(violation.getRootBeanClass().getName(), violation.getMessageTemplate()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, 400, ex.getMessage(), violations));
    }

    @ExceptionHandler(value = UserRoleNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserRoleNotFoundException(UserRoleNotFoundException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, 404, ex.getMessage(), errors));
    }

    @ExceptionHandler(value = UserEmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUserEmailAlreadyExistsException(UserEmailAlreadyExistsException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, 400, ex.getMessage(), errors));
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, 404, ex.getMessage(), errors));
    }

    @ExceptionHandler(value = BudgetNotFoundException.class)
    public ResponseEntity<ApiResponse> handleBudgetNotFoundException(BudgetNotFoundException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, 404, ex.getMessage(), errors));
    }

    @ExceptionHandler(value = InvalidBudgetException.class)
    public ResponseEntity<ApiResponse> handleInvalidBudgetException(InvalidBudgetException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, 400, ex.getMessage(), errors));
    }

    @ExceptionHandler(value = TransactionNotFoundException.class)
    public ResponseEntity<ApiResponse> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, 404, ex.getMessage(), errors));
    }

    @ExceptionHandler(value = InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, "Invalid Credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, 401, ex.getMessage(), errors));
    }

    @ExceptionHandler(value = InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, 401, ex.getMessage(), errors));
    }

    @ExceptionHandler(value = InvalidOTPException.class)
    public ResponseEntity<ApiResponse> handleInvalidOTPException(InvalidOTPException ex) {
        log.error(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, 401, ex.getMessage(), errors));
    }
}
