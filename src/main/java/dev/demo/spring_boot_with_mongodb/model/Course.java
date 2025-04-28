package dev.demo.spring_boot_with_mongodb.model;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Embedded sub-document representing a Course and its marks.
 */
public class Course {
    /**
     * The name, of course.
     */
    @Field(name = "course_name")
    private String name;
    /**
     * The marks obtained in this course (0–100).
     */
    private Integer marks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }
}
