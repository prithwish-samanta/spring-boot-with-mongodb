package dev.demo.spring_boot_with_mongodb.mapper;

import dev.demo.spring_boot_with_mongodb.model.Student;
import dev.demo.spring_boot_with_mongodb.payload.StudentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentPageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {DepartmentMapper.class, CourseMapper.class}
)
public interface StudentMapper {
    Student toEntity(StudentDTO dto);

    StudentDTO toDto(Student entity);

    List<StudentDTO> toDtoList(List<Student> entities);

    default StudentPageResponse toPageResponse(Page<Student> page) {
        return new StudentPageResponse(
                toDtoList(page.getContent()),
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
