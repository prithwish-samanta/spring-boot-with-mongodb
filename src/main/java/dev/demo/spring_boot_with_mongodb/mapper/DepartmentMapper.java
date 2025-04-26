package dev.demo.spring_boot_with_mongodb.mapper;

import dev.demo.spring_boot_with_mongodb.model.Department;
import dev.demo.spring_boot_with_mongodb.payload.DepartmentDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toEntity(DepartmentDTO dto);

    DepartmentDTO toDto(Department entity);
}
