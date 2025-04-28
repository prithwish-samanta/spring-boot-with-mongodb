package dev.demo.spring_boot_with_mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a Student document in the "students" collection.
 * Includes personal details, department reference, course marks,
 * enrollment information, and a transient calculated percentage.
 */
@Document("students")
public class Student {
    /**
     * Unique identifier for the student.
     */
    @Id
    private String id;
    /**
     * Student's first name; mapped as "first_name" in MongoDB.
     */
    @Field(name = "first_name")
    private String firstName;
    /**
     * Student's last name; mapped as "last_name" in MongoDB.
     */
    @Field(name = "last_name")
    private String lastName;
    /**
     * Student's email address; mapped as "email_address".
     */
    @Field(name = "email_address")
    private String email;
    /**
     * Date of birth; mapped as "date_of_birth".
     */
    @Field(name = "date_of_birth")
    private LocalDate dob;
    /**
     * Reference to the Department document.
     */
    @DBRef
    private Department department;
    /**
     * List of courses (embedded sub-documents).
     */
    private List<Course> courses;
    /**
     * Enrollment date; mapped as "enrollment_date".
     */
    @Field(name = "enrollment_date")
    private LocalDate enrollmentDate;
    /**
     * Active status flag; mapped as "is_active".
     */
    @Field(name = "is_active")
    private Boolean active;
    /**
     * Transient field to hold the computed average marks percentage.
     * Not persisted to MongoDB.
     */
    @Transient
    private Double percentage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // --- Business Logic ---

    /**
     * Calculates and returns the student's average marks percentage,
     * rounded to two decimal places.
     * If there are no courses, returns 0.00.
     *
     * @return the average course marks percentage, or 0.00 if no courses
     */
    public Double getPercentage() {
        if (courses == null || courses.isEmpty()) return 0.0;
        double sum = courses.stream()
                .mapToDouble(Course::getMarks)
                .sum();
        double average = sum / courses.size();
        // Round to 2 decimal places (HALF_UP)
        percentage = BigDecimal.valueOf(average)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        return percentage;
    }
}
