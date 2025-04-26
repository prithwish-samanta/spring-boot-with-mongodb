package dev.demo.spring_boot_with_mongodb.payload;

import java.util.List;

public record StudentPageResponse(
        List<StudentDTO> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {
}
