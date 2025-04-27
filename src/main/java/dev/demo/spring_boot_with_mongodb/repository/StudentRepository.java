package dev.demo.spring_boot_with_mongodb.repository;

import dev.demo.spring_boot_with_mongodb.model.Student;
import dev.demo.spring_boot_with_mongodb.payload.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    @Query("""
            {
                    '$or':[
                        {'firstName':{'$regex':?0,'$options':'i'}},
                        {'lastName':{'$regex':?0,'$options':'i'}}
                    ]
            }
            """)
    List<StudentDTO> getByName(String name);

    Page<Student> findByActiveTrue(Pageable pageable);

    Integer countByActiveTrue();

    Boolean existsByEmail(String email);

    Page<Student> findByCoursesName(String courseName, Pageable pageable);
}

