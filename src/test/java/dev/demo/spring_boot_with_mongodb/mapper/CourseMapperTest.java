package dev.demo.spring_boot_with_mongodb.mapper;

import dev.demo.spring_boot_with_mongodb.model.Course;
import dev.demo.spring_boot_with_mongodb.payload.CourseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class CourseMapperTest {
    private CourseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CourseMapper.class);
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // given
        CourseDTO dto = new CourseDTO("Math", 95);
        // when
        Course entity = mapper.toEntity(dto);
        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Math");
        assertThat(entity.getMarks()).isEqualTo(95);
    }

    @Test
    void toDto_shouldMapAllFields() {
        // given
        Course entity = new Course();
        entity.setName("History");
        entity.setMarks(82);
        // when
        CourseDTO dto = mapper.toDto(entity);
        // then
        assertThat(dto).isNotNull();
        assertThat(dto.name()).isEqualTo("History");
        assertThat(dto.marks()).isEqualTo(82);
    }
}