package dev.demo.spring_boot_with_mongodb.payload;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<FieldValidationError> fieldErrors,
        String path
) {
}
