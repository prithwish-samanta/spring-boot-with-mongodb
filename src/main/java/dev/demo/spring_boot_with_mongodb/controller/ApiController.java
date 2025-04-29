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
 * Exposes CRUD operations, paging, filtering, and custom search endpoints.
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
        StudentDTO created = studentService.save(req);
        LOG.info("Student created successfully with ID: {}", created.id());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
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
        LOG.info("getStudents returned {} records on page {}/{}",
                res.content().size(), res.pageNumber() + 1, res.totalPages());
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
        LOG.info("getStudentById found student: {} {}", res.firstName(), res.lastName());
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
        StudentDTO updated = studentService.update(id, req);
        LOG.info("updateStudent completed for ID: {}", updated.id());
        return ResponseEntity.ok().body(updated);
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
        LOG.info("deleteStudent successful for ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search students by name (first or last, case-insensitive substring).
     *
     * @param name the search term
     * @return map containing the search term, count, and matched students
     */
    @GetMapping("/searchByName")
    public ResponseEntity<Map<String, Object>> searchByName(@RequestParam String name) {
        LOG.info("GET /api/v1/students/searchByName - searchByName called with name={}", name);
        List<StudentDTO> students = studentService.searchByName(name);
        Map<String, Object> res = new HashMap<>();
        res.put("name", name);
        res.put("count", students.size());
        res.put("students", students);
        LOG.info("searchByName found {} students matching '{}'", students.size(), name);
        return ResponseEntity.ok(res);
    }

    /**
     * Perform a full-text search over student-first names, last names, and email addresses.
     * Uses the MongoDB text index to match the given term.
     *
     * @param term      the search term to match against the text index
     * @param page      1-based page number to retrieve (default = 1)
     * @param size      number of records per page (default = 20)
     * @param sortField the field by which to sort results (default = "lastName")
     * @param sortDir   sort a direction, either "asc" for ascending or "desc" for descending (default = "asc")
     * @return a paginated response containing the list of matching students and page metadata
     */
    @GetMapping("/search/{text}")
    public ResponseEntity<StudentPageResponse> textSearch(
            @PathVariable("text") String term,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "lastName") String sortField,
            @RequestParam(value = "dir", defaultValue = "asc") String sortDir
    ) {
        LOG.info("GET /search/{} - textSearch called with term={}, page={}, size={}, sortField={}, sortDir={} ",
                term, term, page, size, sortField, sortDir);
        StudentPageResponse res = studentService.textSearch(term, page, size, sortField, sortDir);
        LOG.info("textSearch returned {} records on page {}/{}",
                res.content().size(), res.pageNumber() + 1, res.totalPages());
        return ResponseEntity.ok(res);
    }

    /**
     * Retrieve active students with paging.
     *
     * @param page      1-based page number (default = 1)
     * @param size      number of records per page (default = 20)
     * @param sortField field to sort by (default = lastName)
     * @param sortDir   sort direction: "asc" or "desc" (default = asc)
     * @return a StudentPageResponse containing page metadata and content
     */
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
        LOG.info("activeStudents returned {} records", res.content().size());
        return ResponseEntity.ok(res);
    }

    /**
     * Retrieve the total number of active students.
     *
     * @return a 200-OK ResponseEntity containing a JSON object
     * with a single entry:
     * <ul>
     *   <li><code>count</code> &mdash; the integer count of active students</li>
     * </ul>
     */
    @GetMapping("/count-active")
    public ResponseEntity<Map<String, Integer>> countActiveStudents() {
        LOG.info("GET /api/v1/students/count-active - countActiveStudents called");
        int count = studentService.getActiveStudentsCount();
        LOG.info("Active student count: {}", count);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Check whether a student exists by their email address.
     *
     * @param email the email address to look up
     * @return a 200-OK ResponseEntity containing a JSON object with a single entry:
     * <ul>
     *   <li><code>exists</code> &mdash; <code>true</code> if a student with the specified email exists, <code>false</code> otherwise</li>
     * </ul>
     */
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> doesStudentExists(@RequestParam String email) {
        LOG.info("GET /api/v1/students/exists - doesStudentExists called");
        boolean exists = studentService.isStudentExists(email);
        LOG.info("Student exists status for '{}': {}", email, exists);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Retrieve a paginated list of students enrolled in a given course.
     *
     * @param courseName the name of the course to filter students by
     * @param page       1-based page number (default = 1)
     * @param size       number of records per page (default = 20)
     * @param sortField  property name to sort results by (default = "lastName")
     * @param sortDir    sort direction, either "asc" or "desc" (default = "asc")
     * @return a StudentPageResponse containing page metadata and content
     */
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
        LOG.info("getStudentsByCourse returned {} records", res.content().size());
        return ResponseEntity.ok(res);
    }

    /**
     * Retrieve a paginated list of students who scored at least a minimum mark in a given course.
     *
     * @param courseName the name of the course to filter by
     * @param minScore   the minimum score threshold (inclusive)
     * @param page       1-based page number (default = 1)
     * @param size       number of records per page (default = 20)
     * @param sortField  property name to sort results by (default = "lastName")
     * @param sortDir    sort direction, either "asc" or "desc" (default = "asc")
     * @return a StudentPageResponse containing page metadata and content
     */
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
        LOG.info("getHighScorers returned {} records", res.content().size());
        return ResponseEntity.ok(res);
    }

    /**
     * Retrieve a paginated list of students belonging to a specific department.
     *
     * @param deptId    the ID of the department to filter by
     * @param page      1-based page number (default = 1)
     * @param size      number of records per page (default = 20)
     * @param sortField property name to sort by (default = "lastName")
     * @param sortDir   sort direction, either "asc" or "desc" (default = "asc")
     * @return a StudentPageResponse containing page metadata and content
     */
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
        StudentPageResponse res = studentService.getStudentsByDepartment(deptId, page, size, sortField, sortDir);
        LOG.info("getStudentsByDepartment returned {} records", res.content().size());
        return ResponseEntity.ok(res);
    }

    /**
     * Retrieve a paginated list of students born between two dates (inclusive).
     *
     * @param start     the start date (inclusive) of birth range in ISO format (yyyy-MM-dd)
     * @param end       the end date (inclusive) of birth range in ISO format (yyyy-MM-dd)
     * @param page      1-based page number (default = 1)
     * @param size      number of records per page (default = 20)
     * @param sortField property name to sort by (default = "dob")
     * @param sortDir   sort direction, either "asc" or "desc" (default = "asc")
     * @return a StudentPageResponse containing page metadata and content
     */
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
        LOG.info("getStudentsBornBetween returned {} records", res.content().size());
        return ResponseEntity.ok(res);
    }

    /**
     * Retrieve the most recently enrolled students.
     * <p>
     * Returns all students ordered by enrollment date descending,
     * without pagination.
     *
     * @return a 200-OK ResponseEntity containing a JSON object with:
     * <ul>
     *   <li><code>total</code> – the total number of recent enrollments</li>
     *   <li><code>students</code> – the list of StudentDTOs</li>
     * </ul>
     */
    @GetMapping("/recent-enrollments")
    public ResponseEntity<Map<String, Object>> getRecentEnrollments() {
        LOG.info("GET /api/v1/students/recent-enrollments - getRecentEnrollments called");
        List<StudentDTO> students = studentService.getRecentEnrollments();
        Map<String, Object> res = Map.of("total", students.size(), "students", students);
        return ResponseEntity.ok(res);
    }

}
