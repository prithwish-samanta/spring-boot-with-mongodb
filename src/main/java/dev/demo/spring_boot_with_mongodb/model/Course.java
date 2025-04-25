package dev.demo.spring_boot_with_mongodb.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Course {
    @Field(name = "course_name")
    private String name;
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
