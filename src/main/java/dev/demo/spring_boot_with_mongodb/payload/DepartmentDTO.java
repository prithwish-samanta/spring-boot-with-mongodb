package dev.demo.spring_boot_with_mongodb.payload;

import java.time.LocalDate;

public record DepartmentDTO(
        String id,
        String name,
        String location,
        LocalDate createdAt
) {
}
