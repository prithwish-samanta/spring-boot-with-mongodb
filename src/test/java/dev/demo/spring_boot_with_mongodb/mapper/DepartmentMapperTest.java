package dev.demo.spring_boot_with_mongodb.mapper;

import dev.demo.spring_boot_with_mongodb.model.Department;
import dev.demo.spring_boot_with_mongodb.payload.DepartmentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DepartmentMapperTest {
    private DepartmentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(DepartmentMapper.class);
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // given
        DepartmentDTO dto = new DepartmentDTO(
                "dept1", "Computer Science", "Building A", LocalDate.of(2000, 5, 20)
        );
        // when
        Department entity = mapper.toEntity(dto);
        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo("dept1");
        assertThat(entity.getName()).isEqualTo("Computer Science");
        assertThat(entity.getLocation()).isEqualTo("Building A");
        assertThat(entity.getCreatedAt()).isEqualTo(LocalDate.of(2000, 5, 20));
    }

    @Test
    void toDto_shouldMapAllFields() {
        // given
        Department entity = new Department();
        entity.setId("dept2");
        entity.setName("Mathematics");
        entity.setLocation("Building B");
        entity.setCreatedAt(LocalDate.of(1995, 9, 1));
        // when
        DepartmentDTO dto = mapper.toDto(entity);
        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo("dept2");
        assertThat(dto.name()).isEqualTo("Mathematics");
        assertThat(dto.location()).isEqualTo("Building B");
        assertThat(dto.createdAt()).isEqualTo(LocalDate.of(1995, 9, 1));
    }
}