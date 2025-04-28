package dev.demo.spring_boot_with_mongodb.controller;

import dev.demo.spring_boot_with_mongodb.payload.StudentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentPageResponse;
import dev.demo.spring_boot_with_mongodb.service.StudentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Student resources.
 */
@RestController
@RequestMapping("/api/v1/students")
public class ApiController {
    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);

    private final StudentService studentService;

    public ApiController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Create a new student.
     *
     * @param req the StudentDTO payload
     * @return the created StudentDTO with generated ID
     */
    @PostMapping
    public ResponseEntity<StudentDTO> addStudent(@Valid @RequestBody StudentDTO req) {
        LOG.info("POST /api/v1/students - addStudent called with payload: {}", req);
        StudentDTO res = studentService.save(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }

    /**
     * Retrieve a paginated list of students.
     *
     * @param page      1-based page number (default = 1)
     * @param size      number of records per page (default = 20)
     * @param sortField field to sort by (default = lastName)
     * @param sortDir   sort direction: "asc" or "desc" (default = asc)
     * @return a StudentPageResponse containing page metadata and content
     */
    @GetMapping
    public ResponseEntity<StudentPageResponse> getStudents(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "lastName") String sortField,
            @RequestParam(value = "dir", defaultValue = "asc") String sortDir
    ) {
        LOG.info("GET /api/v1/students - getStudents called with page={}, size={}, sortField={}, sortDir={}",
                page, size, sortField, sortDir);
        StudentPageResponse res = studentService.getAll(page, size, sortField, sortDir);
        return ResponseEntity.ok(res);
    }

    /**
     * Retrieve a single student by ID.
     *
     * @param id the student ID
     * @return the corresponding StudentDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable String id) {
        LOG.info("GET /api/v1/students/{} - getStudentById called", id);
        StudentDTO res = studentService.getById(id);
        return ResponseEntity.ok().body(res);
    }

    /**
     * Update an existing student.
     *
     * @param id  the student ID
     * @param req the updated StudentDTO payload
     * @return the updated StudentDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable String id, @Valid @RequestBody StudentDTO req) {
        LOG.info("PUT /api/v1/students/{} - updateStudent called with payload: {}", id, req);
        StudentDTO res = studentService.update(id, req);
        return ResponseEntity.ok().body(res);
    }

    /**
     * Delete a student by ID.
     *
     * @param id the student ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        LOG.info("DELETE /api/v1/students/{} - deleteStudent called", id);
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searchByName")
    public ResponseEntity<Map<String, Object>> search(@RequestParam String name) {
        LOG.info("GET /api/v1/students/searchByName - search called with name={}", name);
        List<StudentDTO> students = studentService.searchByName(name);
        Map<String, Object> res = new HashMap<>();
        res.put("name", name);
        res.put("count", students.size());
        res.put("students", students);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/active")
    public ResponseEntity<StudentPageResponse> activeStudents(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "lastName") String sortField,
            @RequestParam(value = "dir", defaultValue = "asc") String sortDir
    ) {
        LOG.info("GET /api/v1/students/active - activeStudents called with page={}, size={}, sortField={}, sortDir={}",
                page, size, sortField, sortDir);
        StudentPageResponse res = studentService.getActiveStudents(page, size, sortField, sortDir);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/count-active")
    public ResponseEntity<Map<String, Integer>> countActiveStudents() {
        LOG.info("GET /api/v1/students/count-active - countActiveStudents called");
        Map<String, Integer> res = Map.of("count", studentService.getActiveStudentsCount());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> doesStudentExists(@RequestParam String email) {
        LOG.info("GET /api/v1/students/exists - doesStudentExists called");
        Map<String, Boolean> res = Map.of("exists", studentService.isStudentExists(email));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/by-course")
    public ResponseEntity<StudentPageResponse> getStudentsByCourse(
            @RequestParam String courseName,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "lastName") String sortField,
            @RequestParam(value = "dir", defaultValue = "asc") String sortDir
    ) {
        LOG.info("GET /api/v1/students/by-course - getStudentsByCourse called with courseName={}, page={}, size={}, sortField={}, sortDir={}",
                courseName, page, size, sortField, sortDir);
        StudentPageResponse res = studentService.getStudentByCourse(courseName, page, size, sortField, sortDir);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/high-scorers")
    public ResponseEntity<StudentPageResponse> getHighScorers(
            @RequestParam String courseName,
            @RequestParam int minScore,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "lastName") String sortField,
            @RequestParam(value = "dir", defaultValue = "asc") String sortDir
    ) {
        LOG.info("GET /api/v1/students/high-scorers - getHighScorers called with courseName={}, minScore={} page={}, size={}, sortField={}, sortDir={}",
                courseName, minScore, page, size, sortField, sortDir);
        StudentPageResponse res = studentService.getHighScorers(courseName, minScore, page, size, sortField, sortDir);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/by-department/{deptId}")
    public ResponseEntity<StudentPageResponse> getStudentsByDepartment(
            @PathVariable String deptId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "lastName") String sortField,
            @RequestParam(value = "dir", defaultValue = "asc") String sortDir
    ) {
        LOG.info("GET /api/v1/students/by-department/{} - getStudentsByDepartment called with page={}, size={}, sortField={}, sortDir={}",
                deptId, page, size, sortField, sortDir);
        StudentPageResponse res = studentService.getStudentsByDepartment(deptId, page, size, sortDir, sortField);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/born-between")
    public ResponseEntity<StudentPageResponse> getStudentsBornBetween(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "dob") String sortField,
            @RequestParam(value = "dir", defaultValue = "asc") String sortDir
    ) {
        LOG.info("GET /api/v1/students/born-between - getStudentsBornBetween called with start={}, end={}, page={}, size={}, sortField={}, sortDir={}",
                start, end, page, size, sortField, sortDir);
        StudentPageResponse res = studentService.getStudentsBornBetween(start, end, page, size, sortField, sortDir);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/recent-enrollments")
    public ResponseEntity<Map<String, Object>> getRecentEnrollments() {
        LOG.info("GET /api/v1/students/recent-enrollments - getRecentEnrollments called");
        List<StudentDTO> students = studentService.getRecentEnrollments();
        Map<String, Object> res = Map.of("total", students.size(), "students", students);
        return ResponseEntity.ok(res);
    }

}
