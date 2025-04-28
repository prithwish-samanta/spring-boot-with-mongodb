package dev.demo.spring_boot_with_mongodb.config;

import dev.demo.spring_boot_with_mongodb.model.Student;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;

@Configuration
public class MongoIndexConfig {
    @Bean
    ApplicationRunner initIndexes(MongoTemplate mongo) {
        return args -> {
            // ensure unique email
            mongo.indexOps(Student.class)
                    .ensureIndex(new Index().on("email_address", Sort.Direction.ASC).unique());

            // compound: dept + active
            mongo.indexOps(Student.class)
                    .ensureIndex(new Index()
                            .on("department.$id", Sort.Direction.ASC)
                            .on("is_active", Sort.Direction.ASC)
                            .named("dept_active_idx")
                    );

            // text index on name/email
            mongo.indexOps(Student.class)
                    .ensureIndex(new TextIndexDefinition.TextIndexDefinitionBuilder()
                            .onField("first_name")
                            .onField("last_name")
                            .onField("email_address")
                            .named("student_text_idx")
                            .build()
                    );
        };
    }
}
