package dev.demo.spring_boot_with_mongodb.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record StudentDTO(
        String id,
        @NotBlank(message = "First name must not be blank")
        String firstName,
        @NotBlank(message = "Last name must not be blank")
        String lastName,
        @NotBlank(message = "Email address must not be blank")
        @Email(message = "Email address must be valid")
        String email,
        @NotNull(message = "Date of birth must be provided")
        @Past(message = "Date of birth must be in the past")
        LocalDate dob,
        @NotNull(message = "Department id must not be provided")
        DepartmentDTO department,
        @NotEmpty(message = "At least one course must be provided") @Valid
        List<CourseDTO> courses,
        @NotNull(message = "Enrollment date must be provided")
        @PastOrPresent(message = "Enrollment date cannot be in the future")
        LocalDate enrollmentDate,
        @NotNull(message = "Active status must be specified")
        Boolean active,
        Double percentage
) {
}
