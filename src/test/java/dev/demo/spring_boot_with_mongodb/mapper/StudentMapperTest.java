package dev.demo.spring_boot_with_mongodb.mapper;

import dev.demo.spring_boot_with_mongodb.model.Course;
import dev.demo.spring_boot_with_mongodb.model.Department;
import dev.demo.spring_boot_with_mongodb.model.Student;
import dev.demo.spring_boot_with_mongodb.payload.CourseDTO;
import dev.demo.spring_boot_with_mongodb.payload.DepartmentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentPageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({StudentMapperImpl.class, DepartmentMapperImpl.class, CourseMapperImpl.class})
class StudentMapperTest {
    @Autowired
    private StudentMapper mapper;

    @Test
    void toEntity_shouldMapAllFields() {
        // given
        DepartmentDTO deptDto = new DepartmentDTO("d1", "CS", "Block A", LocalDate.of(2000, 1, 1));
        CourseDTO courseDto = new CourseDTO("Math", 100);
        StudentDTO dto = new StudentDTO(
                "s1",
                "Alice",
                "Smith",
                "alice.smith@example.com",
                LocalDate.of(1999, 5, 15),
                deptDto,
                List.of(courseDto),
                LocalDate.of(2019, 9, 1),
                true,
                100.0
        );
        // when
        Student entity = mapper.toEntity(dto);
        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo("s1");
        assertThat(entity.getFirstName()).isEqualTo("Alice");
        assertThat(entity.getLastName()).isEqualTo("Smith");
        assertThat(entity.getEmail()).isEqualTo("alice.smith@example.com");
        assertThat(entity.getDob()).isEqualTo(LocalDate.of(1999, 5, 15));
        assertThat(entity.getEnrollmentDate()).isEqualTo(LocalDate.of(2019, 9, 1));
        assertThat(entity.isActive()).isTrue();
        // nested dept
        Department dept = entity.getDepartment();
        assertThat(dept).isNotNull();
        assertThat(dept.getId()).isEqualTo("d1");
        assertThat(dept.getName()).isEqualTo("CS");
        // courses
        List<Course> courses = entity.getCourses();
        assertThat(courses).hasSize(1);
        assertThat(courses.getFirst().getName()).isEqualTo("Math");
        assertThat(courses.getFirst().getMarks()).isEqualTo(100);
    }

    @Test
    void toDto_shouldMapAllFields() {
        // given
        Department dept = new Department();
        dept.setId("d2");
        dept.setName("EE");
        dept.setLocation("Block B");
        dept.setCreatedAt(LocalDate.of(2005, 2, 2));
        Course course = new Course();
        course.setName("Physics");
        course.setMarks(95);
        Student entity = new Student();
        entity.setId("s2");
        entity.setFirstName("Bob");
        entity.setLastName("Jones");
        entity.setEmail("bob.jones@example.com");
        entity.setDob(LocalDate.of(2000, 12, 12));
        entity.setDepartment(dept);
        entity.setCourses(List.of(course));
        entity.setEnrollmentDate(LocalDate.of(2020, 1, 1));
        entity.setActive(false);
        // when
        StudentDTO dto = mapper.toDto(entity);
        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo("s2");
        assertThat(dto.firstName()).isEqualTo("Bob");
        assertThat(dto.lastName()).isEqualTo("Jones");
        assertThat(dto.email()).isEqualTo("bob.jones@example.com");
        assertThat(dto.dob()).isEqualTo(LocalDate.of(2000, 12, 12));
        assertThat(dto.enrollmentDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(dto.active()).isFalse();
        // nested dept
        DepartmentDTO deptDto = dto.department();
        assertThat(deptDto.id()).isEqualTo("d2");
        assertThat(deptDto.name()).isEqualTo("EE");
        // courses
        List<CourseDTO> courseDto = dto.courses();
        assertThat(courseDto).hasSize(1);
        assertThat(courseDto.getFirst().name()).isEqualTo("Physics");
        assertThat(courseDto.getFirst().marks()).isEqualTo(95);
    }

    @Test
    void toPageResponse_shouldWrapPageCorrectly() {
        // given
        Student s = new Student();
        s.setId("s3");
        s.setFirstName("Carol");
        s.setLastName("Lee");
        Page<Student> page = new PageImpl<>(
                List.of(s),
                PageRequest.of(0, 1),
                1
        );
        // when
        StudentPageResponse resp = mapper.toPageResponse(page);
        // then
        assertThat(resp).isNotNull();
        assertThat(resp.pageNumber()).isEqualTo(1);
        assertThat(resp.pageSize()).isEqualTo(1);
        assertThat(resp.totalElements()).isEqualTo(1L);
        assertThat(resp.totalPages()).isEqualTo(1);
        assertThat(resp.content()).hasSize(1);
        assertThat(resp.content().getFirst().id()).isEqualTo("s3");
        assertThat(resp.first()).isTrue();
        assertThat(resp.last()).isTrue();
        assertThat(resp.hasNext()).isFalse();
        assertThat(resp.hasPrevious()).isFalse();
    }
}