package dev.demo.spring_boot_with_mongodb.service;

import dev.demo.spring_boot_with_mongodb.exception.ResourceNotFoundException;
import dev.demo.spring_boot_with_mongodb.mapper.CourseMapper;
import dev.demo.spring_boot_with_mongodb.mapper.StudentMapper;
import dev.demo.spring_boot_with_mongodb.model.Course;
import dev.demo.spring_boot_with_mongodb.model.Department;
import dev.demo.spring_boot_with_mongodb.model.Student;
import dev.demo.spring_boot_with_mongodb.payload.StudentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentPageResponse;
import dev.demo.spring_boot_with_mongodb.repository.DepartmentRepository;
import dev.demo.spring_boot_with_mongodb.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger LOG = LoggerFactory.getLogger(StudentServiceImpl.class);
    private static final String RESOURCE_NAME = "Student";
    private static final String FETCHED_RESOURCE_LOG = "Fetched {} students ({} total pages)";

    private final StudentRepository studentRepo;
    private final DepartmentRepository departmentRepo;
    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;

    public StudentServiceImpl(StudentRepository studentRepo, DepartmentRepository departmentRepo, StudentMapper studentMapper, CourseMapper courseMapper) {
        this.studentRepo = studentRepo;
        this.departmentRepo = departmentRepo;
        this.studentMapper = studentMapper;
        this.courseMapper = courseMapper;
    }

    /**
     * Create a new Student record.
     *
     * @param req DTO payload from a client
     * @return saved Student as DTO
     */
    @Override
    public StudentDTO save(StudentDTO req) {
        LOG.info("save() called with payload: {}", req);
        // Map DTO to the entity and ensure ID is null (new record)
        Student student = studentMapper.toEntity(req);
        student.setId(null);
        String deptId = req.department().id();
        Department dept = departmentRepo.findById(deptId).orElseThrow(() -> {
            LOG.warn("save() did not find department ID: {}", deptId);
            return new ResourceNotFoundException("Department", "id", deptId);
        });
        student.setDepartment(dept);
        // Persist entity
        Student saved = studentRepo.save(student);
        StudentDTO dto = studentMapper.toDto(saved);
        LOG.info("save() completed, new student ID: {}", dto.id());
        return dto;
    }

    /**
     * Retrieve a page of students with sorting.
     *
     * @param page      1-based page number
     * @param size      number of items per page
     * @param sortField field to sort by
     * @param sortDir   "asc" or "desc"
     * @return paged response DTO
     */
    @Override
    public StudentPageResponse getAll(int page, int size, String sortField, String sortDir) {
        // Build Page request
        Pageable pageReq = getPageRequest(page, size, sortField, sortDir);
        // Fetch paged data
        Page<Student> studentPage = studentRepo.findAll(pageReq);
        LOG.debug(FETCHED_RESOURCE_LOG, studentPage.getNumberOfElements(), studentPage.getTotalPages());
        // Map entities to DTOs and wrap in the response object
        StudentPageResponse response = studentMapper.toPageResponse(studentPage);
        LOG.info("getAll() returning page {} of {}, {} items", response.pageNumber() + 1, response.totalPages(), response.content().size());
        return response;
    }

    /**
     * Retrieve a single student by ID.
     *
     * @param id student identifier
     * @return found Student as DTO
     */
    @Override
    public StudentDTO getById(String id) {
        LOG.info("getById() called for ID: {}", id);
        // Lookup student or throw 404
        Student student = studentRepo.findById(id).orElseThrow(() -> {
            LOG.warn("getById() did not find student with ID: {}", id);
            return new ResourceNotFoundException(RESOURCE_NAME, "id", id);
        });
        StudentDTO dto = studentMapper.toDto(student);
        LOG.info("getById() found student: {}", dto);
        return dto;
    }

    /**
     * Update an existing student. Fields not in the DTO will remain unchanged.
     *
     * @param id  student identifier
     * @param req DTO containing updated values
     * @return updated Student as DTO
     */
    @Override
    public StudentDTO update(String id, StudentDTO req) {
        LOG.info("update() called for ID: {}, payload: {}", id, req);
        // Fetch existing record or throw
        Student student = studentRepo.findById(id).orElseThrow(() -> {
            LOG.warn("update() did not find student with ID: {}", id);
            return new ResourceNotFoundException(RESOURCE_NAME, "id", id);
        });
        // Update simple fields
        student.setFirstName(req.firstName());
        student.setLastName(req.lastName());
        student.setEmail(req.email());
        student.setDob(req.dob());
        student.setEnrollmentDate(req.enrollmentDate());
        student.setActive(req.active());
        // If department ID provided, validate and set reference
        if (req.department() != null && req.department().id() != null) {
            Department dept = departmentRepo.findById(req.department().id()).orElseThrow(() -> {
                LOG.warn("update() did not find department ID: {}", req.department().id());
                return new ResourceNotFoundException("Department", "id", req.department().id());
            });
            student.setDepartment(dept);
        }
        // Update courses list
        List<Course> courses = req.courses().stream().map(courseMapper::toEntity).toList();
        student.setCourses(courses);
        // Persist changes
        Student updated = studentRepo.save(student);
        StudentDTO dto = studentMapper.toDto(updated);
        LOG.info("update() completed for ID: {}, updated DTO: {}", id, dto);
        return dto;
    }

    /**
     * Delete a student by ID.
     *
     * @param id student identifier
     */
    @Override
    public void delete(String id) {
        LOG.info("delete() called for ID: {}", id);
        // Ensure the student exists
        Student student = studentRepo.findById(id).orElseThrow(() -> {
            LOG.warn("delete() did not find student with ID: {}", id);
            return new ResourceNotFoundException(RESOURCE_NAME, "id", id);
        });
        // Perform deletion
        studentRepo.delete(student);
        LOG.info("delete() successful for ID: {}", id);
    }

    @Override
    public List<StudentDTO> searchByName(String name) {
        List<Student> students = studentRepo.getByName(name);
        return students.stream().map(studentMapper::toDto).toList();
    }

    @Override
    public StudentPageResponse getActiveStudents(int page, int size, String sortField, String sortDir) {
        Pageable pageReq = getPageRequest(page, size, sortField, sortDir);
        Page<Student> studentPage = studentRepo.findByActiveTrue(pageReq);
        LOG.debug("Fetched {} active students ({} total pages)", studentPage.getNumberOfElements(), studentPage.getTotalPages());
        // Map entities to DTOs and wrap in the response object
        StudentPageResponse response = studentMapper.toPageResponse(studentPage);
        LOG.info("getActiveStudents() returning page {} of {}, {} items", response.pageNumber() + 1, response.totalPages(), response.content().size());
        return response;
    }

    @Override
    public Integer getActiveStudentsCount() {
        return studentRepo.countByActiveTrue();
    }

    @Override
    public Boolean isStudentExists(String email) {
        return studentRepo.existsByEmail(email);
    }

    @Override
    public StudentPageResponse getStudentByCourse(String courseName, int page, int size, String sortField, String sortDir) {
        Pageable pageReq = getPageRequest(page, size, sortField, sortDir);
        Page<Student> studentPage = studentRepo.findByCoursesName(courseName, pageReq);
        LOG.debug(FETCHED_RESOURCE_LOG, studentPage.getNumberOfElements(), studentPage.getTotalPages());
        // Map entities to DTOs and wrap in the response object
        StudentPageResponse response = studentMapper.toPageResponse(studentPage);
        LOG.info("getStudentByCourse() returning page {} of {}, {} items", response.pageNumber() + 1, response.totalPages(), response.content().size());
        return response;
    }

    @Override
    public StudentPageResponse getHighScorers(String courseName, int minScore, int page, int size, String sortField, String sortDir) {
        Pageable pageReq = getPageRequest(page, size, sortField, sortDir);
        Page<Student> studentPage = studentRepo.findByCoursesNameAndCoursesMarksGreaterThanEqual(courseName, minScore, pageReq);
        LOG.debug(FETCHED_RESOURCE_LOG, studentPage.getNumberOfElements(), studentPage.getTotalPages());
        // Map entities to DTOs and wrap in the response object
        StudentPageResponse response = studentMapper.toPageResponse(studentPage);
        LOG.info("getHighScorers() returning page {} of {}, {} items", response.pageNumber() + 1, response.totalPages(), response.content().size());
        return response;
    }

    @Override
    public StudentPageResponse getStudentsByDepartment(String deptId, int page, int size, String sortDir, String sortField) {
        Pageable pageReq = getPageRequest(page, size, sortField, sortDir);
        Page<Student> studentPage = studentRepo.findByDepartment_Id(deptId, pageReq);
        LOG.debug(FETCHED_RESOURCE_LOG, studentPage.getNumberOfElements(), studentPage.getTotalPages());
        // Map entities to DTOs and wrap in the response object
        StudentPageResponse response = studentMapper.toPageResponse(studentPage);
        LOG.info("getStudentsByDepartment() returning page {} of {}, {} items", response.pageNumber() + 1, response.totalPages(), response.content().size());
        return response;
    }

    @Override
    public StudentPageResponse getStudentsBornBetween(LocalDate start, LocalDate end, int page, int size, String sortField, String sortDir) {
        Pageable pageReq = getPageRequest(page, size, sortField, sortDir);
        Page<Student> studentPage = studentRepo.findByDobBetween(start, end, pageReq);
        LOG.debug(FETCHED_RESOURCE_LOG, studentPage.getNumberOfElements(), studentPage.getTotalPages());
        // Map entities to DTOs and wrap in the response object
        StudentPageResponse response = studentMapper.toPageResponse(studentPage);
        LOG.info("getStudentsBornBetween() returning page {} of {}, {} items", response.pageNumber() + 1, response.totalPages(), response.content().size());
        return response;
    }

    @Override
    public List<StudentDTO> getRecentEnrollments() {
        List<Student> students = studentRepo.findTop5ByOrderByEnrollmentDateDesc();
        return students.stream().map(studentMapper::toDto).toList();
    }

    private Pageable getPageRequest(int page, int size, String sortField, String sortDir) {
        LOG.info("Page details - page={}, size={}, sortField={}, sortDir={}", page, size, sortField, sortDir);
        // Determine a sort direction: asc → ASC, otherwise DESC
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        // Build and return Pageable (convert to zero-based page index)
        return PageRequest.of(page - 1, size, Sort.by(direction, sortField));
    }
}
