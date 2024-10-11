#   Enrollment Management System
> This is an enrollment management backend application which expose multiple endpoint with security layer based on JWT token and documented with OpenAPI.

## Tools
* Java 17
* Maven 3.6.0
* Spring Boot 3.3.1
* Sprint Security
* OpenAPI
* SonarQube
* Postgres DB
* Docker

## Environment Variables
Before to run the application create a .env file in the root and put the following variables.
```properties
POSTGRES_DATABASE=myuser
POSTGRES_PASSWORD=mypassword
APP_SECRET=mysecret
```

## Deployment
You have two ways to run the application:
### A. Run the application locally:
1. You have to create an application-local.yml and put the following properties.
```yaml
spring:
  application:
    name: managementEnrollment
  datasource:
    hikari:
      connectionTimeout: 20000
      maximumPoolSize: 5
    url: jdbc:postgresql://localhost:5432/enrolldb
    username: yourUsername
    password: yourPassword

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true
    generate-ddl: true

enrollment:
  app:
    jwtCookieName: Cookie-App
    jwtSecret: yourSecretWith64Characters
    jwtExpirationMs: 1800000

logging:
  level:
    org.springframework.security: DEBUG
```

1.1. By Maven 
```shell
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

1.2. By Jar
To use this option you have to ensure the jar is already generated.
If you are not able to see the jar in the target folder, run the following command:
```shell
mvn package
```
Then run the following command:
```shell
java -jar -Dspring.profiles.active=local managementEnrollment-0.0.1-SNAPSHOT.jar
```

2. Run the application with docker:
To use the containerization with docker you have to ensure the jar is already generated

If you are not able to see the jar in the target folder, run the following command:
```shell
mvn package
```
Then run the following docker commands:
2.1 Generate the image
```shell
docker build -t yourUsername/management-enrollment:1.0.0 .
```
2.2 Run a container from the generated image:
```shell
docker run -p 8080:8080 yourUsername/management-enrollment:1.0.0
```

## Post Deploy
After initialization, insert the following roles in the roles table:
```text
ROLE_ADMIN
ROLE_USER
ROLE_MODERATOR
```

## API Documentation
- [Swagger Documentation](http://localhost:8080/swagger-ui/index.html)

