package dev.demo.spring_boot_with_mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("dev.demo.spring_boot_with_mongodb.repository")
public class SpringBootWithMongodbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWithMongodbApplication.class, args);
    }

}
