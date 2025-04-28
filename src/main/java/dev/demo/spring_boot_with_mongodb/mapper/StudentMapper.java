package dev.demo.spring_boot_with_mongodb.mapper;

import dev.demo.spring_boot_with_mongodb.model.Student;
import dev.demo.spring_boot_with_mongodb.payload.StudentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentPageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Mapper for converting between {@link Student} entities and {@link StudentDTO} payloads,
 * as well as wrapping paged results into {@link StudentPageResponse}.
 * Uses MapStruct for compile-time implementation generation.
 */
@Mapper(
        componentModel = "spring",
        uses = {DepartmentMapper.class, CourseMapper.class}
)
public interface StudentMapper {
    /**
     * Convert a StudentDTO to a Student entity.
     *
     * @param dto the data transfer object containing student data
     * @return a new Student entity populated from the DTO
     */
    Student toEntity(StudentDTO dto);

    /**
     * Convert a Student entity to a StudentDTO.
     *
     * @param entity the Student entity to convert
     * @return a StudentDTO populated from the entity
     */
    StudentDTO toDto(Student entity);

    /**
     * Convert a list of Student entities to a list of StudentDTOs.
     *
     * @param entities the list of Student entities
     * @return the list of corresponding StudentDTOs
     */
    List<StudentDTO> toDtoList(List<Student> entities);

    /**
     * Wrap a {@link Page} of Student entities into a {@link StudentPageResponse} DTO,
     * mapping the content and preserving paging metadata.
     *
     * @param page the Page of Student entities
     * @return a StudentPageResponse containing mapped DTOs and paging info
     */
    default StudentPageResponse toPageResponse(Page<Student> page) {
        return new StudentPageResponse(
                toDtoList(page.getContent()),
                // Convert a zero-based index to one-based for the client
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
