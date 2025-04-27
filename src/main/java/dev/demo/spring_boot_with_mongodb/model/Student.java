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

@Document("students")
public class Student {
    @Id
    private String id;
    @Field(name = "first_name")
    private String firstName;
    @Field(name = "last_name")
    private String lastName;
    @Field(name = "email_address")
    private String email;
    @Field(name = "date_of_birth")
    private LocalDate dob;
    @DBRef
    private Department department;
    private List<Course> courses;
    @Field(name = "enrollment_date")
    private LocalDate enrollmentDate;
    @Field(name = "is_active")
    private Boolean active;
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

    public Double getPercentage() {
        if (courses == null || courses.isEmpty()) return 0.0;
        double sum = courses.stream()
                .mapToDouble(Course::getMarks)
                .sum();
        double average = sum / courses.size();
        // Round to 2 decimal places
        percentage = BigDecimal.valueOf(average)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        return percentage;
    }
}
