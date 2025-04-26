package dev.demo.spring_boot_with_mongodb.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseDTO(
        @NotBlank(message = "Course name must not be blank")
        String name,
        @NotNull(message = "Marks must be provided")
        @Min(value = 0, message = "Marks cannot be less than {value}")
        @Max(value = 100, message = "Marks cannot exceed {value}")
        Integer marks
) {
}
