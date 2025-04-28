package dev.demo.spring_boot_with_mongodb.service;

import dev.demo.spring_boot_with_mongodb.payload.StudentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentPageResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing Student entities.
 * Defines operations for creating, retrieving, updating, deleting,
 * and querying students with pagination, filtering, and search capabilities.
 */
public interface StudentService {
    StudentDTO save(StudentDTO req);

    StudentPageResponse getAll(int page, int size, String sortField, String sortDir);

    StudentDTO getById(String id);

    StudentDTO update(String id, StudentDTO req);

    void delete(String id);

    List<StudentDTO> searchByName(String name);

    StudentPageResponse getActiveStudents(int page, int size, String sortField, String sortDir);

    Integer getActiveStudentsCount();

    Boolean isStudentExists(String email);

    StudentPageResponse getStudentByCourse(String courseName, int page, int size, String sortField, String sortDir);

    StudentPageResponse getHighScorers(String courseName, int minScore, int page, int size, String sortField, String sortDir);

    StudentPageResponse getStudentsByDepartment(String deptId, int page, int size, String sortDir, String sortField);

    StudentPageResponse getStudentsBornBetween(LocalDate start, LocalDate end, int page, int size, String sortField, String sortDir);

    List<StudentDTO> getRecentEnrollments();
}
