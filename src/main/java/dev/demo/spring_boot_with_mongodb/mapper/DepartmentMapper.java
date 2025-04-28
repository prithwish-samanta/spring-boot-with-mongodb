package dev.demo.spring_boot_with_mongodb.mapper;

import dev.demo.spring_boot_with_mongodb.model.Department;
import dev.demo.spring_boot_with_mongodb.payload.DepartmentDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for converting between {@link Department} entities and {@link DepartmentDTO} payloads.
 * Uses MapStruct to generate the implementation at compile time.
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    /**
     * Convert a DepartmentDTO to a Department entity.
     *
     * @param dto the data transfer object containing department data
     * @return a new Department entity populated from the DTO
     */
    Department toEntity(DepartmentDTO dto);

    /**
     * Convert a Department entity to a DepartmentDTO.
     *
     * @param entity the Department entity to convert
     * @return a DepartmentDTO populated from the entity
     */
    DepartmentDTO toDto(Department entity);
}
