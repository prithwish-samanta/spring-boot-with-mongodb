package dev.demo.spring_boot_with_mongodb.mapper;

import dev.demo.spring_boot_with_mongodb.model.Course;
import dev.demo.spring_boot_with_mongodb.payload.CourseDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for converting between {@link Course} entities and {@link CourseDTO} payloads.
 * Uses MapStruct to automatically generate implementation code at build time.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper {
    /**
     * Convert a CourseDTO to a Course entity.
     *
     * @param dto the data transfer object containing course data
     * @return a new Course entity populated from the DTO
     */
    Course toEntity(CourseDTO dto);

    /**
     * Convert a Course entity to a CourseDTO.
     *
     * @param entity the Course entity to convert
     * @return a CourseDTO populated from the entity
     */
    CourseDTO toDto(Course entity);
}
