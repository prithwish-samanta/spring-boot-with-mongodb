package dev.demo.spring_boot_with_mongodb.bootstrap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.demo.spring_boot_with_mongodb.model.Department;
import dev.demo.spring_boot_with_mongodb.model.Student;
import dev.demo.spring_boot_with_mongodb.repository.DepartmentRepository;
import dev.demo.spring_boot_with_mongodb.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class InitDataLoader implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(InitDataLoader.class);

    private final StudentRepository studentRepo;
    private final DepartmentRepository deptRepo;
    private final ObjectMapper objectMapper;

    public InitDataLoader(
            StudentRepository studentRepo,
            DepartmentRepository deptRepo,
            ObjectMapper objectMapper) {
        this.studentRepo = studentRepo;
        this.deptRepo = deptRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("=== Data Seeding: START ===");
        seedDepartments();
        seedStudents();
        LOG.info("=== Data Seeding: COMPLETE ===");
    }

    private void seedDepartments() throws IOException {
        long start = System.currentTimeMillis();
        long count = deptRepo.count();
        if (count > 0) {
            LOG.info("→ Departments already exist (count = {}), skipping load.", count);
        } else {
            LOG.info("→ No departments found, loading from JSON…");
            List<Department> departments = loadDepartments();
            deptRepo.saveAll(departments);
            LOG.info("✔ Inserted {} departments in {} ms",
                    departments.size(),
                    System.currentTimeMillis() - start);
        }
    }

    private void seedStudents() throws IOException {
        long start = System.currentTimeMillis();
        long count = studentRepo.count();
        if (count > 0) {
            LOG.info("→ Students already exist (count = {}), skipping load.", count);
        } else {
            LOG.info("→ No students found, loading from JSON…");
            List<Student> students = loadStudents();
            studentRepo.saveAll(students);
            LOG.info("✔ Inserted {} students in {} ms",
                    students.size(),
                    System.currentTimeMillis() - start);
        }
    }

    public List<Department> loadDepartments() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/departments.json");
        return objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });
    }

    private List<Student> loadStudents() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/students.json");
        return objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });
    }
}
