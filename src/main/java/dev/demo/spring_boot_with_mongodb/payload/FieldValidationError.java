package dev.demo.spring_boot_with_mongodb.payload;

public record FieldValidationError(
        String field,
        String message
) {
}
