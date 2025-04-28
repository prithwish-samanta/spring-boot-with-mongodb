package dev.demo.spring_boot_with_mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

/**
 * Represents a Department in the MongoDB collection "departments".
 */
@Document("departments")
public class Department {
    /**
     * Unique identifier for the department.
     */
    @Id
    private String id;
    /**
     * Name of the department. Mapped to "dept_name" in MongoDB.
     */
    @Field(name = "dept_name")
    private String name;
    /**
     * Physical location or building of the department.
     */
    private String location;
    /**
     * Date when the department was established. Mapped to "established_on".
     */
    @Field(name = "established_on")
    private LocalDate createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
