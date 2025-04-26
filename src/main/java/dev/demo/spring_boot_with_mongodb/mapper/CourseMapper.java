package dev.demo.spring_boot_with_mongodb.mapper;

import dev.demo.spring_boot_with_mongodb.model.Course;
import dev.demo.spring_boot_with_mongodb.payload.CourseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    Course toEntity(CourseDTO dto);

    CourseDTO toDto(Course entity);
}
