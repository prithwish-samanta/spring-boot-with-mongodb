package dev.demo.spring_boot_with_mongodb.service;

import dev.demo.spring_boot_with_mongodb.exception.ResourceNotFoundException;
import dev.demo.spring_boot_with_mongodb.mapper.CourseMapper;
import dev.demo.spring_boot_with_mongodb.mapper.StudentMapper;
import dev.demo.spring_boot_with_mongodb.model.Course;
import dev.demo.spring_boot_with_mongodb.model.Department;
import dev.demo.spring_boot_with_mongodb.model.Student;
import dev.demo.spring_boot_with_mongodb.payload.CourseDTO;
import dev.demo.spring_boot_with_mongodb.payload.DepartmentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentPageResponse;
import dev.demo.spring_boot_with_mongodb.repository.DepartmentRepository;
import dev.demo.spring_boot_with_mongodb.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.TextCriteria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {
    @Mock
    StudentRepository studentRepo;
    @Mock
    DepartmentRepository departmentRepo;
    @Mock
    StudentMapper studentMapper;
    @Mock
    CourseMapper courseMapper;
    StudentDTO dto;
    Student entity;
    Department dept;
    @InjectMocks
    private StudentServiceImpl service;

    @BeforeEach
    void setup() {
        dept = new Department();
        dept.setId("d1");
        dept.setName("CS");
        dept.setCreatedAt(LocalDate.of(2000, 1, 1));
        // simple DTO and entity with one course
        dto = new StudentDTO(
                null, "Alice", "Wong", "a.wong@example.com",
                LocalDate.of(2000, 1, 1),
                new DepartmentDTO("d1", "CS", null, null),
                List.of(new CourseDTO("Algo", 90)),
                LocalDate.of(2020, 8, 20),
                true,
                null
        );
        entity = new Student();
        entity.setId("s1");
        entity.setFirstName("Alice");
        entity.setLastName("Wong");
        entity.setEmail("a.wong@example.com");
        entity.setDob(LocalDate.of(2000, 1, 1));
        entity.setDepartment(dept);
        Course course = new Course();
        course.setName("Algo");
        course.setMarks(90);
        entity.setCourses(List.of(course));
        entity.setEnrollmentDate(LocalDate.of(2020, 8, 20));
        entity.setActive(true);
    }

    @Test
    @DisplayName("getAll() should return mapped page")
    void getAll() {
        // given
        Page<Student> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 1, Sort.by("firstName")), 1);
        given(studentRepo.findAll(any(Pageable.class))).willReturn(page);
        StudentPageResponse pageResp = new StudentPageResponse(
                List.of(dto), 1, 1, 1L, 1, true, true, false, false);
        given(studentMapper.toPageResponse(page)).willReturn(pageResp);
        // when
        StudentPageResponse resp = service.getAll(1, 1, "firstName", "asc");
        // then
        assertThat(resp).isEqualTo(pageResp);
        then(studentRepo).should().findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("update() updates and returns DTO")
    void update() {
        // given
        given(studentRepo.findById("s1")).willReturn(Optional.of(entity));
        StudentDTO updateDto = new StudentDTO("s1", "Alicia", "Wong", "a.wong@example.com",
                LocalDate.of(2000, 1, 1), new DepartmentDTO("d1", "CS", "", null),
                List.of(new CourseDTO("Algo", 95)), LocalDate.of(2020, 8, 20), true, null);
        // department lookup
        given(departmentRepo.findById("d1")).willReturn(Optional.of(dept));
        // course conversion
        Course course = new Course();
        course.setName("Algo");
        course.setMarks(90);
        given(courseMapper.toEntity(any(CourseDTO.class))).willReturn(course);
        // save
        given(studentRepo.save(any(Student.class))).willReturn(entity);
        given(studentMapper.toDto(entity)).willReturn(updateDto);
        // when
        StudentDTO res = service.update("s1", updateDto);
        // then
        assertThat(res).isEqualTo(updateDto);
        then(studentRepo).should().save(any(Student.class));
    }

    @Test
    @DisplayName("delete() existing id deletes")
    void delete() {
        // given
        given(studentRepo.findById("s1")).willReturn(Optional.of(entity));
        // when
        service.delete("s1");
        // then
        then(studentRepo).should().delete(entity);
    }

    @Test
    @DisplayName("delete() missing id throws")
    void deleteNotFound() {
        // given
        given(studentRepo.findById("s1")).willReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> service.delete("s1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("searchByName() delegates to repo + mapper")
    void searchByName() {
        // given
        given(studentRepo.getByName("Al")).willReturn(List.of(entity));
        given(studentMapper.toDto(entity)).willReturn(dto);
        // when
        List<StudentDTO> list = service.searchByName("Al");
        // then
        assertThat(list).containsExactly(dto);
    }

    @Test
    @DisplayName("textSearch() delegates to repo + mapper")
    void textSearch() {
        // given
        Page<Student> page = new PageImpl<>(List.of(entity));
        given(studentRepo.findAllBy(any(TextCriteria.class), any(Pageable.class))).willReturn(page);
        StudentPageResponse pageResp = new StudentPageResponse(
                List.of(dto), 1, 20, 1L, 1, true, true, false, false);
        given(studentMapper.toPageResponse(page)).willReturn(pageResp);
        // when
        StudentPageResponse resp = service.textSearch("a.wong", 1, 20, "lastName", "asc");
        // then
        assertThat(resp).isEqualTo(pageResp);
    }

    @Test
    @DisplayName("getActiveByDepartment() delegates to repo + mapper")
    void getActiveByDepartment() {
        //given
        Page<Student> page = new PageImpl<>(List.of(entity));
        given(studentRepo.findByDepartment_IdAndActiveTrue(eq("d1"), any(Pageable.class))).willReturn(page);
        StudentPageResponse pageResp = new StudentPageResponse(
                List.of(dto), 1, 20, 1L, 1, true, true, false, false);
        given(studentMapper.toPageResponse(page)).willReturn(pageResp);
        // when
        StudentPageResponse resp = service.getActiveByDepartment("d1", 1, 20, "lastName", "asc");
        // then
        assertThat(resp).isEqualTo(pageResp);
    }

    @Test
    @DisplayName("getActiveStudentsCount() returns repo count")
    void getActiveStudentsCount() {
        // given
        given(studentRepo.countByActiveTrue()).willReturn(3);
        // when
        int count = service.getActiveStudentsCount();
        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("isStudentExists() delegates to repo.existsByEmail")
    void isStudentExists() {
        // given
        given(studentRepo.existsByEmail("a@example.com")).willReturn(true);
        // when
        boolean exists = service.isStudentExists("a@example.com");
        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("getStudentByCourse() returns paged response")
    void getStudentByCourse() {
        // given
        Page<Student> page = new PageImpl<>(List.of(entity));
        given(studentRepo.findByCoursesName(eq("Algo"), any(Pageable.class))).willReturn(page);
        StudentPageResponse pageResp = new StudentPageResponse(
                List.of(dto), 1, 20, 1L, 1, true, true, false, false);
        given(studentMapper.toPageResponse(page)).willReturn(pageResp);
        // when
        StudentPageResponse resp = service.getStudentByCourse("Algo", 1, 20, "lastName", "asc");
        // then
        assertThat(resp).isEqualTo(pageResp);
    }

    @Test
    @DisplayName("getHighScorers() returns paged response")
    void getHighScorers() {
        // given
        Page<Student> page = new PageImpl<>(List.of(entity));
        given(studentRepo.findByCoursesNameAndCoursesMarksGreaterThanEqual(eq("Algo"), eq(80), any(Pageable.class))).willReturn(page);
        StudentPageResponse pageResp = new StudentPageResponse(
                List.of(dto), 1, 20, 1L, 1, true, true, false, false);
        given(studentMapper.toPageResponse(page)).willReturn(pageResp);
        // when
        StudentPageResponse resp = service.getHighScorers("Algo", 80, 1, 20, "lastName", "asc");
        // then
        assertThat(resp).isEqualTo(pageResp);
    }

    @Test
    @DisplayName("getStudentsByDepartment() returns paged response")
    void getStudentsByDepartment() {
        // given
        Page<Student> page = new PageImpl<>(List.of(entity));
        given(studentRepo.findByDepartment_Id(eq("d1"), any(Pageable.class))).willReturn(page);
        StudentPageResponse pageResp = new StudentPageResponse(
                List.of(dto), 1, 20, 1L, 1, true, true, false, false);
        given(studentMapper.toPageResponse(page)).willReturn(pageResp);
        // when
        StudentPageResponse resp = service.getStudentsByDepartment("d1", 1, 20, "lastName", "asc");
        // then
        assertThat(resp).isEqualTo(pageResp);
    }

    @Test
    @DisplayName("getStudentsBornBetween() returns paged response")
    void getStudentsBornBetween() {
        // given
        LocalDate start = LocalDate.of(1990, 1, 1);
        LocalDate end = LocalDate.of(2000, 12, 31);
        Page<Student> page = new PageImpl<>(List.of(entity));
        given(studentRepo.findByDobBetween(eq(start), eq(end), any(Pageable.class))).willReturn(page);
        StudentPageResponse pageResp = new StudentPageResponse(
                List.of(dto), 1, 20, 1L, 1, true, true, false, false);
        given(studentMapper.toPageResponse(page)).willReturn(pageResp);
        // when
        StudentPageResponse resp = service.getStudentsBornBetween(start, end, 1, 20, "dob", "asc");
        // then
        assertThat(resp).isEqualTo(pageResp);
    }

    @Test
    @DisplayName("getRecentEnrollments() returns top 5 students")
    void getRecentEnrollments() {
        // given
        given(studentRepo.findTop5ByOrderByEnrollmentDateDesc()).willReturn(List.of(entity));
        given(studentMapper.toDto(entity)).willReturn(dto);
        // when
        List<StudentDTO> list = service.getRecentEnrollments();
        // then
        assertThat(list).containsExactly(dto);
    }

    @Nested
    @DisplayName("save()")
    class SaveTests {
        @Test
        @DisplayName("given valid DTO, when save, then returns saved DTO")
        void saveHappyPath() {
            // given
            given(studentMapper.toEntity(dto)).willReturn(entity);
            given(departmentRepo.findById("d1")).willReturn(Optional.of(dept));
            given(studentRepo.save(entity)).willReturn(entity);
            StudentDTO savedDto = new StudentDTO("s1", "Alice", "Wong", "alice.wong@example.com",
                    LocalDate.of(2000, 1, 1), new DepartmentDTO("d1", "CS", "", null),
                    List.of(new CourseDTO("Algo", 90)), LocalDate.of(2020, 8, 20), true, 90.0);
            given(studentMapper.toDto(entity)).willReturn(savedDto);
            // when
            StudentDTO result = service.save(dto);
            // then
            assertThat(result.id()).isEqualTo("s1");
            then(departmentRepo).should().findById("d1");
            then(studentRepo).should().save(entity);
        }

        @Test
        @DisplayName("given missing department, when save, then throw ResourceNotFoundException")
        void saveDeptNotFound() {
            // given
            given(studentMapper.toEntity(dto)).willReturn(entity);
            given(departmentRepo.findById("d1")).willReturn(Optional.empty());
            // then
            assertThatThrownBy(() -> service.save(dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Department not found");
            then(studentRepo).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetByIdTests {
        @Test
        @DisplayName("given existing id, when getById, then returns DTO")
        void getByIdFound() {
            // given
            given(studentRepo.findById("s1")).willReturn(Optional.of(entity));
            given(studentMapper.toDto(entity)).willReturn(dto);
            // when
            StudentDTO result = service.getById("s1");
            // then
            assertThat(result).isEqualTo(dto);
        }

        @Test
        @DisplayName("given non-existing id, when getById, then throw ResourceNotFoundException")
        void getByIdNotFound() {
            // given
            given(studentRepo.findById("s1")).willReturn(Optional.empty());
            // then
            assertThatThrownBy(() -> service.getById("s1"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Student not found");
        }
    }

}