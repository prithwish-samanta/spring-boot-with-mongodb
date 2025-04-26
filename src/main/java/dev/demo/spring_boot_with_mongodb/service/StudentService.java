package dev.demo.spring_boot_with_mongodb.service;

import dev.demo.spring_boot_with_mongodb.payload.StudentDTO;
import dev.demo.spring_boot_with_mongodb.payload.StudentPageResponse;

public interface StudentService {
    StudentDTO save(StudentDTO req);

    StudentPageResponse getAll(int page, int size, String sortField, String sortDir);

    StudentDTO getById(String id);

    StudentDTO update(String id, StudentDTO req);

    void delete(String id);
}
