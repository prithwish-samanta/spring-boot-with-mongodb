package dev.demo.spring_boot_with_mongodb.repository;

import dev.demo.spring_boot_with_mongodb.model.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
}
