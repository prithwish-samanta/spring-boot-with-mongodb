package dev.demo.spring_boot_with_mongodb.repository;

import dev.demo.spring_boot_with_mongodb.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for {@link Student} entities.
 * <p>
 * Extends Spring Data MongoRepository to provide CRUD operations,
 * pagination, sorting, and custom query methods on the "students" collection.
 */
@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    /**
     * Find students whose first or last name matches the given pattern (case-insensitive).
     * Uses a MongoDB regex query via @Query annotation.
     *
     * @param nameRegex the regex pattern to match against firstName or lastName
     * @return list of matching {@link Student} entities
     */
    @Query("""
            {
                    '$or':[
                        {'firstName':{'$regex':?0,'$options':'i'}},
                        {'lastName':{'$regex':?0,'$options':'i'}}
                    ]
            }
            """)
    List<Student> getByName(String nameRegex);

    /**
     * Perform a full-text search on the students collection.
     *
     * @param criteria the TextCriteria defining the search term(s) and language
     * @param pageable pagination and sorting instructions
     * @return a Page of Student entities whose text-indexed fields match the criteria
     */
    Page<Student> findAllBy(TextCriteria criteria, Pageable pageable);

    /**
     * Retrieve only active students in the given department.
     *
     * @param deptId   the ID of the department to filter by
     * @param pageable pagination and sorting instructions
     * @return a Page of active Student entities belonging to that department
     */
    Page<Student> findByDepartment_IdAndActiveTrue(String deptId, Pageable pageable);

    /**
     * Retrieve a page of active students (where active = true).
     *
     * @param pageable pagination and sorting information
     * @return a {@link Page} of active students
     */
    Page<Student> findByActiveTrue(Pageable pageable);

    /**
     * Count the number of active students.
     *
     * @return the total count of students with active = true
     */
    Integer countByActiveTrue();

    /**
     * Check if a student exists with the given email address.
     *
     * @param email the email to check for existence
     * @return true if a student with the email exists, false otherwise
     */
    Boolean existsByEmail(String email);

    /**
     * Retrieve a page of students enrolled in the specified course.
     *
     * @param courseName the name of the course
     * @param pageable   pagination and sorting information
     * @return a {@link Page} of students taking the given course
     */
    Page<Student> findByCoursesName(String courseName, Pageable pageable);

    /**
     * Retrieve a page of students enrolled in the specified course
     * with marks greater than or equal to the given minimum score.
     *
     * @param courseName the name of the course
     * @param minScore   the minimum marks threshold (inclusive)
     * @param pageable   pagination and sorting information
     * @return a {@link Page} of students meeting the criteria
     */
    Page<Student> findByCoursesNameAndCoursesMarksGreaterThanEqual(String courseName, Integer minScore, Pageable pageable);

    /**
     * Retrieve a page of students belonging to a specific department.
     *
     * @param deptId   the department's ID
     * @param pageable pagination and sorting information
     * @return a {@link Page} of students in the department
     */
    Page<Student> findByDepartment_Id(String deptId, Pageable pageable);

    /**
     * Retrieve a page of students born between two dates (inclusive).
     *
     * @param start    start date (inclusive) of birth range
     * @param end      end date (inclusive) of birth range
     * @param pageable pagination and sorting information
     * @return a {@link Page} of students born between the dates
     */
    Page<Student> findByDobBetween(LocalDate start, LocalDate end, Pageable pageable);

    /**
     * Find the top 5 most recently enrolled students,
     * ordered by enrollment date descending.
     *
     * @return list of up to 5 students sorted by the newest enrollment
     */
    List<Student> findTop5ByOrderByEnrollmentDateDesc();
}

