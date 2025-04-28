package dev.demo.spring_boot_with_mongodb.repository;

import dev.demo.spring_boot_with_mongodb.model.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Department} entities.
 * Extends Spring Data’s {@link MongoRepository} to provide standard
 * CRUD operations, pagination, and sorting on the "departments" collection.
 * You can also add custom derived‐query methods here
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
}
