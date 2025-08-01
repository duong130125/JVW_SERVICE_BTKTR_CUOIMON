package ra.bt_ktr_cuoimon.advice_controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ra.bt_ktr_cuoimon.model.dto.response.DataErrorResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ResControllerAdvice {

    // ✅ Xử lý lỗi: @Valid trên @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataErrorResponse<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return buildErrorResponse("Validation failed", errors, HttpStatus.BAD_REQUEST);
    }

    // ✅ Xử lý lỗi: @Valid trên @RequestParam, @PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<DataErrorResponse<Map<String, String>>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(error ->
                errors.put(error.getPropertyPath().toString(), error.getMessage())
        );
        return buildErrorResponse("Validation failed", errors, HttpStatus.BAD_REQUEST);
    }

    // ✅ Xử lý lỗi: dữ liệu không hợp lệ hoặc không tìm thấy
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DataErrorResponse<Map<String, String>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return buildErrorResponse("Invalid request", errors, HttpStatus.NOT_FOUND);
    }

    // ✅ Xử lý lỗi: định dạng ngày không đúng
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<DataErrorResponse<Map<String, String>>> handleDateTimeParseException(DateTimeParseException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return buildErrorResponse("Invalid date format", errors, HttpStatus.BAD_REQUEST);
    }

    // ✅ Xử lý lỗi: đường dẫn không tồn tại (Spring Boot 3+)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<DataErrorResponse<Map<String, String>>> handleNoResourceFoundException(NoResourceFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return buildErrorResponse("Resource not found", errors, HttpStatus.NOT_FOUND);
    }

    // ✅ Xử lý lỗi: request không hợp lệ do người dùng (custom exception)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<DataErrorResponse<Map<String, String>>> handleBadRequestException(BadRequestException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return buildErrorResponse("Bad request", errors, HttpStatus.BAD_REQUEST);
    }

    // ✅ Xử lý lỗi: Không tìm thấy bản ghi khi dùng findById().orElseThrow()
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<DataErrorResponse<Map<String, String>>> handleNotFoundException(ChangeSetPersister.NotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return buildErrorResponse("Not Found", errors, HttpStatus.NOT_FOUND);
    }

    // ✅ Hàm build chung cho tất cả phản hồi lỗi (chuẩn hóa cấu trúc)
    private ResponseEntity<DataErrorResponse<Map<String, String>>> buildErrorResponse(
            String message,
            Map<String, String> errors,
            HttpStatus status
    ) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        DataErrorResponse<Map<String, String>> response = new DataErrorResponse<>(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                request.getRequestURI(),
                message,
                errors
        );

        return new ResponseEntity<>(response, status);
    }
}