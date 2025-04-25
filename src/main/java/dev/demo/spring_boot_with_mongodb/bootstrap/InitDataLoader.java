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

/**
 * Loads initial data for Departments and Students from JSON files on application startup.
 */
@Component
public class InitDataLoader implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(InitDataLoader.class);

    private final StudentRepository studentRepo;
    private final DepartmentRepository deptRepo;
    private final ObjectMapper objectMapper;

    public InitDataLoader(StudentRepository studentRepo, DepartmentRepository deptRepo, ObjectMapper objectMapper) {
        this.studentRepo = studentRepo;
        this.deptRepo = deptRepo;
        this.objectMapper = objectMapper;
    }

    /**
     * Entry point for CommandLineRunner.
     * Delegates seeding of departments and students.
     */
    @Override
    public void run(String... args) throws Exception {
        LOG.info("=== Data Seeding: START ===");
        seedDepartments();
        seedStudents();
        LOG.info("=== Data Seeding: COMPLETE ===");
    }

    /**
     * Seed Department collection if empty.
     * Logs timing and count information.
     */
    private void seedDepartments() throws IOException {
        long start = System.currentTimeMillis();
        long count = deptRepo.count();
        if (count > 0) {
            LOG.info("→ Departments already exist (count = {}), skipping load.", count);
        } else {
            LOG.info("→ No departments found, loading from JSON…");
            try {
                List<Department> departments = loadDepartments();
                LOG.debug("Loaded {} departments from JSON", departments.size());
                deptRepo.saveAll(departments);
                LOG.info("✔ Inserted {} departments in {} ms", departments.size(), System.currentTimeMillis() - start);
            } catch (IOException e) {
                LOG.error("✘ Failed to load departments JSON", e);
            }
        }
    }

    /**
     * Seed Student collection if empty.
     * Logs timing and count information.
     */
    private void seedStudents() throws IOException {
        long start = System.currentTimeMillis();
        long count = studentRepo.count();
        if (count > 0) {
            LOG.info("→ Students already exist (count = {}), skipping load.", count);
        } else {
            LOG.info("→ No students found, loading from JSON…");
            try {
                List<Student> students = loadStudents();
                LOG.debug("Loaded {} students from JSON", students.size());
                studentRepo.saveAll(students);
                LOG.info("✔ Inserted {} students in {} ms", students.size(), System.currentTimeMillis() - start);
            } catch (IOException e) {
                LOG.error("✘ Failed to load students JSON", e);
            }
        }
    }

    /**
     * Reads the departments.json file from classpath and deserializes into a List<Department>.
     *
     * @throws IOException if the file cannot be read or parsed
     */
    public List<Department> loadDepartments() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/departments.json");
        LOG.debug("Loading departments from classpath resource: {}", resource.getFilename());
        return objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });
    }

    /**
     * Reads the students.json file from classpath and deserializes into a List<Student>.
     *
     * @throws IOException if the file cannot be read or parsed
     */
    private List<Student> loadStudents() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/students.json");
        LOG.debug("Loading students from classpath resource: {}", resource.getFilename());
        return objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });
    }
}
