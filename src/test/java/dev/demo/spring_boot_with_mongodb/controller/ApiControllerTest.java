package dev.demo.spring_boot_with_mongodb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.demo.spring_boot_with_mongodb.payload.CourseDTO;
import dev.demo.spring_boot_with_mongodb.payload.DepartmentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentPageResponse;
import dev.demo.spring_boot_with_mongodb.service.StudentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ApiController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiControllerTest {
    private static final String BASE_URL = "/api/v1/students";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockitoBean
    private StudentService studentService;

    private StudentDTO sampleDto(String id, String fn, String ln) {
        DepartmentDTO dept = new DepartmentDTO("dept123", "Computer Science", "Uni hall", LocalDate.now());
        CourseDTO course = new CourseDTO("Algorithms", 85);
        return new StudentDTO(
                id,
                fn,
                ln,
                fn.toLowerCase() + "." + ln.toLowerCase() + "@example.com",
                LocalDate.of(2000, 1, 1),
                dept,
                List.of(course),
                LocalDate.of(2020, 1, 1),
                true,
                90.0
        );
    }

    @Test
    @DisplayName("POST /students → 201 + body")
    @Order(1)
    void addStudent() throws Exception {
        // given
        StudentDTO req = sampleDto(null, "Alice", "Wong");
        StudentDTO res = sampleDto("abc123", "Alice", "Wong");
        given(studentService.save(any())).willReturn(res);

        // when / then
        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    @DisplayName("GET /students → 200 + paged JSON")
    @Order(2)
    void getStudents() throws Exception {
        // given
        StudentDTO one = sampleDto("id1", "Bob", "Smith");
        StudentPageResponse page = new StudentPageResponse(
                List.of(one), 1, 20, 1L, 1, true, true, false, false
        );
        given(studentService.getAll(1, 20, "lastName", "asc")).willReturn(page);

        // when / then
        mvc.perform(get(BASE_URL)
                        .param("page", "1")
                        .param("size", "20")
                        .param("sort", "lastName")
                        .param("dir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /students/{id} → 200 + JSON")
    @Order(3)
    void getStudentById() throws Exception {
        // given
        StudentDTO dto = sampleDto("xyz", "Carol", "Jones");
        given(studentService.getById("xyz")).willReturn(dto);

        // when / then
        mvc.perform(get(BASE_URL + "/xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("xyz"));
    }

    @Test
    @DisplayName("PUT /students/{id} → 200 + updated JSON")
    @Order(4)
    void updateStudent() throws Exception {
        // given
        StudentDTO req = sampleDto(null, "Dan", "Brown");
        StudentDTO updated = sampleDto("u1", "Dan", "Brown");
        given(studentService.update(eq("u1"), any())).willReturn(updated);

        // when / then
        mvc.perform(put(BASE_URL + "/u1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u1"));
    }

    @Test
    @DisplayName("DELETE /students/{id} → 204")
    @Order(5)
    void deleteStudent() throws Exception {
        // when / then
        mvc.perform(delete(BASE_URL + "/del1"))
                .andExpect(status().isNoContent());
        verify(studentService).delete("del1");
    }

    @Test
    @DisplayName("GET /students/searchByName → 200 + JSON")
    @Order(6)
    void search() throws Exception {
        // given
        StudentDTO a = sampleDto("A1", "Eve", "Lee");
        given(studentService.searchByName("E")).willReturn(List.of(a));

        // when / then
        mvc.perform(get(BASE_URL + "/searchByName").param("name", "E"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @DisplayName("GET /students/active → 200 + paged JSON")
    @Order(7)
    void activeStudents() throws Exception {
        // given
        StudentDTO active = sampleDto("act1", "Fay", "Wong");
        StudentPageResponse page = new StudentPageResponse(
                List.of(active), 1, 20, 1L, 1, true, true, false, false
        );
        given(studentService.getActiveStudents(1, 20, "lastName", "asc")).willReturn(page);

        // when / then
        mvc.perform(get(BASE_URL + "/active")
                        .param("page", "1").param("size", "20")
                        .param("sort", "lastName").param("dir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("act1"));
    }

    @Test
    @DisplayName("GET /students/count-active → 200 + JSON")
    @Order(8)
    void countActiveStudents() throws Exception {
        // given
        given(studentService.getActiveStudentsCount()).willReturn(42);

        // when / then
        mvc.perform(get(BASE_URL + "/count-active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(42));
    }

    @Test
    @DisplayName("GET /students/exists → 200 + JSON")
    @Order(9)
    void doesStudentExists() throws Exception {
        // given
        given(studentService.isStudentExists("x@y.com")).willReturn(true);

        // when / then
        mvc.perform(get(BASE_URL + "/exists").param("email", "x@y.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    @DisplayName("GET /students/by-course → 200 + paged JSON")
    @Order(10)
    void getStudentsByCourse() throws Exception {
        // given
        StudentDTO c = sampleDto("c1", "Gus", "Rhee");
        StudentPageResponse page = new StudentPageResponse(
                List.of(c), 1, 20, 1L, 1, true, true, false, false
        );
        given(studentService.getStudentByCourse("AI", 1, 20, "lastName", "asc")).willReturn(page);

        // when / then
        mvc.perform(get(BASE_URL + "/by-course")
                        .param("courseName", "AI")
                        .param("page", "1").param("size", "20")
                        .param("sort", "lastName").param("dir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("c1"));
    }

    @Test
    @DisplayName("GET /students/high-scorers → 200 + paged JSON")
    @Order(11)
    void getHighScorers() throws Exception {
        // given
        StudentDTO h = sampleDto("h1", "Ivy", "Chan");
        StudentPageResponse page = new StudentPageResponse(
                List.of(h), 1, 20, 1L, 1, true, true, false, false
        );
        given(studentService.getHighScorers("AI", 80, 1, 20, "lastName", "asc")).willReturn(page);

        // when / then
        mvc.perform(get(BASE_URL + "/high-scorers")
                        .param("courseName", "AI").param("minScore", "80")
                        .param("page", "1").param("size", "20")
                        .param("sort", "lastName").param("dir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("h1"));
    }

    @Test
    @DisplayName("GET /students/by-department → 200 + paged JSON")
    @Order(12)
    void getStudentsByDepartment() throws Exception {
        // given
        StudentDTO d = sampleDto("d1", "John", "Doe");
        StudentPageResponse page = new StudentPageResponse(
                List.of(d), 1, 20, 1L, 1, true, true, false, false
        );
        given(studentService.getStudentsByDepartment("dept1", 1, 20, "lastName", "asc")).willReturn(page);

        // when / then
        mvc.perform(get(BASE_URL + "/by-department/dept1")
                        .param("page", "1").param("size", "20")
                        .param("sort", "lastName").param("dir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("d1"));
    }

    @Test
    @DisplayName("GET /students/born-between → 200 + paged JSON")
    @Order(13)
    void getStudentsBornBetween() throws Exception {
        // given
        StudentDTO b = sampleDto("b1", "Ken", "Lim");
        StudentPageResponse page = new StudentPageResponse(
                List.of(b), 1, 20, 1L, 1, true, true, false, false
        );
        String start = "1990-01-01";
        String end = "2000-12-31";
        given(studentService.getStudentsBornBetween(LocalDate.parse(start), LocalDate.parse(end), 1, 20, "dob", "asc")).willReturn(page);

        // when / then
        mvc.perform(get(BASE_URL + "/born-between")
                        .param("start", start)
                        .param("end", end)
                        .param("page", "1").param("size", "20")
                        .param("sort", "dob").param("dir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("b1"));
    }

    @Test
    @DisplayName("GET /students/recent-enrollments → 200 + JSON array")
    @Order(14)
    void getRecentEnrollments() throws Exception {
        // given
        StudentDTO r = sampleDto("r1", "Leo", "Ngy");
        given(studentService.getRecentEnrollments()).willReturn(List.of(r));

        // when / then
        mvc.perform(get(BASE_URL + "/recent-enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.students[0].id").value("r1"));
    }
}