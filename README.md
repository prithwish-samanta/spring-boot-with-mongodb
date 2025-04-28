# Spring Boot + MongoDB Integration

This project demonstrates how to integrate MongoDB with Spring Boot, covering a wide range of data access patterns—from
simple CRUD to advanced aggregation pipelines. It’s designed as a learning repository, providing both code samples and
tests to help you master MongoDB usage in a Spring ecosystem.

## 🌟 Topics Covered

- CRUD Operations (Create, Read, Update, Delete)
- Pagination & Sorting via Spring Data Pageable
- Derived Query Methods: And, Or, In, Like, TopN
- Custom @Query methods with raw JSON (regex, text search)
- Aggregation Pipelines using MongoTemplate ($unwind, $group, $lookup)
- DBRef & Joins via $lookup
- Transient Fields & computed properties
- Docker Compose for MongoDB setup
- Integration Testing: @DataMongoTest + Testcontainers

## Prerequisites

- Java17 or higher
- Maven (or Gradle)
- Docker (for MongoDB)
- Postman (optional, for API testing)

## 🚀 Getting Started

1. Clone the repository
    ```bash
    git clone https://github.com/your-org/spring-boot-with-mongodb.git
    cd spring-boot-with-mongodb
    ```
2. Start MongoDB via Docker Compose
    ```bash
    docker-compose up -d
    ```
3. Build & run the application
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

The REST API will be available at http://localhost:8888/api/v1/students

## 🗄️ Configuration

- MongoDB URI: configured in src/main/resources/application.yml
   ```yaml
   spring:
    data:
      mongodb:
        uri: mongodb://localhost:27017/studentdb
   ```
- Docker Compose definition in docker-compose.yml launches a mongo:latest container.

## 📡 API Endpoints & Concepts

| HTTP Method | Path                                | Description                           | Concepts                             |
|-------------|-------------------------------------|---------------------------------------|--------------------------------------|
| POST        | /                                   | Create a new Student                  | CRUD                                 |
| GET         | /                                   | List Students (page, size, sort, dir) | Pagination & Sorting                 |
| GET         | /{id}                               | Get Student by ID                     | CRUD                                 |
| PUT         | /{id}                               | Update Student                        | CRUD                                 |
| DELETE      | /{id}                               | Delete Student                        | CRUD                                 |
| GET         | /active                             | List Active Students                  | Derived Query (findByActiveTrue)     |
| GET         | /count-active                       | Count Active Students                 | Derived Query (countByActiveTrue)    |
| GET         | /exists?email=<email>               | Check existence by email              | Derived Query (existsByEmail)        |
| GET         | /searchByName?name=<term>           | Regex search on first/last name       | Custom @Query (regex)                |
| GET         | /by-course?courseName=<name>        | Students by Course name               | Derived Query (findByCoursesName)    |
| GET         | /high-scorers?courseName=&minScore= | Students scoring ≥ minScore in course | Derived Query (compound)             |
| GET         | /by-department/{deptId}             | Students in Department                | Derived Query (findByDepartment_Id)  |
| GET         | /born-between?start=&end=           | Students born in date range           | Derived Query (findByDobBetween)     |
| GET         | /recent-enrollments                 | Top 5 recent enrollments              | Derived Query (findTop5ByOrderBy...) |

## TODO

- [ ] Implement native text-search endpoint (GET /text?term=...) using MongoDB text index
- [ ] Create aggregation pipeline endpoints:
    - [ ] GET /agg/avg-marks (average marks per course)
    - [ ] GET /agg/count-by-dept (student count by department)
    - [ ] GET /lookup (join students with departments)
- [ ] Define and apply MongoDB indexes (e.g., text, compound) in code or migrations

## 📬 Postman Collection

Import the Postman collection to explore and test the API:

- File: [spring-boot-with-mongodb.postman_collection.json](src/test/resources/postman/spring-boot-with-mongodb.postman_collection.json)
- Link: [Open in Postman](https://www.postman.com/)

🤝 Contributing

- Fork the repo
- Create a feature branch
- Write tests for new features
- Submit a pull request