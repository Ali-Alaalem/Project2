# Project2

## Overview
This repository contains a **Spring Boot** backend for a hospital management/booking domain. It is configured to use **PostgreSQL** for persistence and includes **Spring Security + JWT** components for stateless authentication.

The current codebase exposes a single sample endpoint (`GET /hello`). Authentication endpoints referenced by security configuration (`/auth/users/login`, `/auth/users/register`) are **not implemented** in this repository yet.

## Tech Stack
- **Java**: 17
- **Framework**: Spring Boot
- **Build tool**: Maven (`mvnw`, `pom.xml`)
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security + JWT (`io.jsonwebtoken:jjwt-*`)

## Project Structure
Main entrypoint:
- `src/main/java/com/project/hospital/HospitalApplication.java`

Key packages:
- `controllers/` - REST controllers (currently only `HelloWorld`)
- `models/` - JPA entities (User, Role, Permission, Room, Appointment, Booking, TreatmentType)
- `repositorys/` - Spring Data repositories
- `security/` - JWT utilities and Spring Security configuration
- `services/` - service layer (currently `UserService`)

Other:
- `src/main/resources/application.properties` - application configuration
- `erd.png` - ERD diagram for the intended schema

## Prerequisites
- **Java 17** installed
- **PostgreSQL** running locally
- (Optional) An IDE like IntelliJ / VS Code with Java extensions

## Configuration
Configuration is currently stored in `src/main/resources/application.properties`.

### Database
Default configuration (as committed):
- `spring.datasource.url=jdbc:postgresql://localhost:5432/hospital`
- `spring.datasource.username=postgres`
- `spring.datasource.password=12345678`

Create the database in Postgres:
```sql
CREATE DATABASE hospital;
```

Hibernate is set to update the schema automatically:
- `spring.jpa.hibernate.ddl-auto=update`

### JWT
JWT settings are also present in `application.properties`:
- `jwt-secret=...`
- `jwt-expiration-ms=86400000`

If you intend to deploy this app, you should **not** commit secrets. Prefer overriding these values via environment-specific configuration.

## Running the Application
From the project root:

```bash
./mvnw spring-boot:run
```

On Windows (PowerShell/CMD):
```bat
mvnw.cmd spring-boot:run
```

The server listens on:
- `http://localhost:8080`

## API
### Public endpoints
- `GET /hello`
  - Returns a plain text message: `Hello world`

### Authentication endpoints (planned / referenced)
`SecurityConfiguration` permits requests to:
- `POST /auth/users/login`
- `POST /auth/users/register`
- `GET /auth/users`

However, there is currently **no controller implementation** for these routes in `controllers/`.

### Authenticated endpoints
All other routes are configured as **authenticated** by default:

- Requires header:
  - `Authorization: Bearer <JWT>`

## Domain Model (High-level)
The entities in `models/` indicate the intended domain:
- **User** with a **Role**
- **Role** with many **Permission** (RBAC)
- **Appointment** associated with a doctor (User) and a Room
- **Booking** associated with a patient (User) and optionally linked to an Appointment
- **Room** and **TreatmentType** for categorizing rooms/users

See `erd.png` for a visual overview.

## Troubleshooting
- **Cannot connect to database**
  - Ensure Postgres is running
  - Verify the DB exists: `hospital`
  - Update `spring.datasource.*` values as needed

- **401 Unauthorized on endpoints**
  - Only `/hello` is currently mapped; other controllers may not exist yet
  - If you add secured endpoints, ensure you supply a valid `Authorization: Bearer ...` token

## Notes / Next Steps
- Add missing `Auth` controller(s) for login/register.
- Consider moving secrets (DB password, `jwt-secret`) out of version control.
- Add API documentation (Swagger/OpenAPI) once endpoints are implemented.