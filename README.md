# Citizenship Compass

A full-stack civics study application created by Vanessa Nava for the Western Governors University Software Engineering capstone. The application helps users study for the United States naturalization civics test through question review, quizzes, study notes, and progress reports.

## Features

- User registration and login with Spring Security
- Searchable civics questions and practice quizzes
- Favorite questions and personal study notes
- Quiz history and progress reporting
- English and Spanish interface resources

## Technologies

Java 21, Spring Boot 3, Spring Security, Spring Data JPA, Thymeleaf, HTML, CSS, JavaScript, PostgreSQL, and H2 for local development.

## Run locally

Install JDK 21, then run:

```sh
./mvnw spring-boot:run
```

Open http://localhost:8080. Local development uses an H2 database under `data/`. Local databases and generated files are excluded from this repository.

## Tests

```sh
./mvnw test
```

Tests cover registration, localization, application services, and quiz behavior.

## Deployment

The project includes a Dockerfile and a Render Blueprint. The production profile uses PostgreSQL. Configure `SPRING_PROFILES_ACTIVE=prod`, `DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, and `REMEMBER_ME_KEY` in the hosting environment. Configure the Blueprint branch to match the branch deployed by your host.

## Study content

Verify study questions and answers against current USCIS materials before relying on them for an examination. Government officials and examination requirements can change.
