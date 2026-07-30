# BookHub
A full-stack community library management platform, an Angular frontend and a Spring Boot REST API running as separate services, communicating over REST API.
 
[![Angular](https://img.shields.io/badge/Angular-%23DD0031.svg?logo=angular&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-%236DB33F.svg?logo=springboot&logoColor=white)](#)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-%23336791.svg?logo=microsoftsqlserver&logoColor=white)](#)
[![TypeScript](https://img.shields.io/badge/TypeScript-%233178C6.svg?logo=typescript&logoColor=white)](#)
[![TailwindCSS](https://img.shields.io/badge/Tailwind%20CSS-%2306B6D4.svg?logo=tailwindcss&logoColor=white)](#)
 
---
 
## Architecture

![Architecture Diagram](https://github.com/user-attachments/assets/4a0dc311-71db-4d33-ac36-acd609654e82)
 
The Angular frontend follows **MVVM**, the component (ViewModel) binds to the template (View) and delegates HTTP calls to services (Model), with modal dialogs used throughout for create/edit/confirm flows and a fully responsive layout. The Spring Boot backend follows **MVC** with a Layered Architecture (Controller → Service → Repository), with Controllers handling requests, Services carrying business logic, and Repositories abstracting data access via Spring Data JPA.
 
---
 
## Tech Stack
 
| Layer     | Technology                                      | Pattern  |
|-----------|--------------------------------------------------|----------|
| Frontend  | Angular 21, TypeScript , Tailwind CSS      | MVVM     |
| Backend   | Spring Boot 3.2+, Java 21, Spring Data JPA       | MVC      |
| Auth      | Spring Security + JWT (access + refresh tokens)   |          |
| Database  | SQL Server 2019+                                  |          |
| API Docs  | Swagger / OpenAPI                                 |          |
 
---
 
## Project Structure
 
```
bookhub/
├── frontend/        # Angular SPA
├── backend/         # Spring Boot REST API
└── docs/            # UML diagrams, MCD/MLD, wireframes
```
 
### Backend highlights
- Layered architecture: Controller → Service → Repository
- DTOs for request/response payloads, decoupled from JPA entities
- JWT authentication with role-based access control (`USER`, `LIBRARIAN`, `ADMIN`)
- Bean Validation (`@Valid`, Jakarta Validation) on all inputs
- Global exception handling with consistent error responses
- Business rules enforced at the service layer (loan limits, reservation queue, late-return blocking)
- Swagger/OpenAPI documentation
### Frontend highlights
- Responsive catalogue with search, filters, and pagination
- Role-based dashboards (reader, librarian, admin)
- Reactive forms with client-side validation mirroring backend rules
- JWT stored client-side, attached via HTTP interceptor
- Angular's built-in sanitization against XSS
---
 
## Core Modules
 
| Module          | Description                                              |
|------------------|------------------------------------------------------------|
| Authentication   | Registration, login, profile management |
| Catalogue        | Book listing, detail view, search/filters, librarian CRUD |
| Book Copies      | Physical copies of a book, dependent entity (cannot exist without a parent book), drives real-time availability |
| Loans            | Borrow/return books, loan history, 3-loan limit, 14-day duration |
| Reservations     | Reserve unavailable books, queue position, cancellation    |
| Ratings          | 1–5 star ratings, comments, admin moderation           |
| Dashboards       | librarian statistics                    |
 
---
 
## API
 
Base URL: `http://localhost:8080/api`
 
| Method | Endpoint                       | Description                                  | Role      |
|--------|----------------------------------|-----------------------------------------------|-----------|
| POST   | `/auth/register`                | Register a new user                          | Public    |
| POST   | `/auth/login`                    | Login, returns access token + sets refresh token | Public    |
| POST   | `/auth/refresh`                  | Issue a new access token (CSRF-protected)     | Public    |
| GET    | `/books/all`                     | List/search/filter books (paginated, 20/page) | Public    |
| GET    | `/books/{id}`                    | Get book detail                               | Public    |
| POST   | `/books`                         | Create book                                    | LIBRARIAN |
| PUT    | `/books/{id}`                    | Update book                                    | LIBRARIAN |
| DELETE | `/books/{id}`                    | Delete book                                    | ADMIN     |
| GET    | `/book-copies/book/{bookId}`             | List copies of a book (paginated)              | Public    |
| POST   | `/create`             | Add a copy to a book                           | LIBRARIAN |
| POST   | `/loans`                         | Borrow a book                                  | USER      |
| GET    | `/loans/my`                      | Get my loans (paginated)                       | USER      |
| GET    | `/loans`                         | Get all loans (paginated)                      | LIBRARIAN |
| PUT    | `/loans/{id}/return`             | Register a return                              | LIBRARIAN |
| POST   | `/reservations`                  | Reserve a book                                 | USER      |
| GET    | `/reservations/my`                | Get my reservations (paginated)                | USER      |
| DELETE | `/reservations/{id}`              | Cancel a reservation                           | USER      |
| POST   | `/books/{id}/ratings`             | Rate/comment a book                            | USER      |
| PUT    | `/ratings/{id}`                   | Update a rating                                | USER      |
| DELETE | `/ratings/{id}`                   | Delete a rating                                | LIBRARIAN |
 
Every list endpoint above is paginated (20 items per page). Search and filtering on books (title, author, ISBN, category, availability) are all handled through `/books/all` via query params, there's no separate `/search` endpoint.
 
Full interactive documentation is available via Swagger UI once the backend is running:
**http://localhost:8080/swagger-ui/index.html**
 
---
 
## Configuration
 
Before running the backend, copy `backend/src/main/resources/application.properties.example` paste it and change its name to application.properties and fill in the blank fields (datasource credentials and JWT secret):
 
```properties
spring.datasource.url=jdbc:sqlserver://;serverName=localhost;databaseName=BookHub;encrypt=true;trustServerCertificate=true;
spring.datasource.username=
spring.datasource.password=
 
app.upload.dir=./uploads/books
app.base-url=http://localhost:8080
 
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
 
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
 
jwt.secret=
jwt.expiration=900000
```
 
- `jwt.secret` needs a 256-bit (32+ char) random value, generate with `openssl rand -base64 32`.
- `jwt.expiration` is in milliseconds — `900000` ms = 15 minutes for the access token.
---
 
## Running Locally
 
### Backend
```bash
cd backend
./gradlew bootRun
```
Requires SQL Server running with a `BookHub` database and the connection details filled in `application.properties` above.
 
### Frontend
```bash
cd frontend
npm install
ng serve
```
 
- Frontend: http://localhost:4200
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/swagger-ui.html
---
 
## Security
 
**Authentication flow**
- On login, the backend issues an **access token** (15 min) and a **refresh token** (7 days).
- The access token is kept in memory only (a TypeScript variable in the Angular app), never persisted to storage ,it's lost on page refresh by design.
- The refresh token is what survives a page reload. On app init, Angular calls `/auth/refresh` to silently obtain a new access token before the user can interact with anything protected.
- `/auth/refresh` is the **only** endpoint protected by a CSRF token (it's the one state-changing call not carrying a bearer token in the header, since the browser sends the refresh token automatically). Every other protected endpoint is authenticated via a `Bearer` access token in the `Authorization` header, no CSRF check needed there since there's nothing for a browser to attach automatically.
**Other measures**
- Passwords hashed with BCrypt (cost 12), enforced strong password policy (12+ chars, upper/lower/digit/special)
- JWT signed with HS256, `email` and `role` claims in the payload
- SQL injection prevention via Spring Data JPA parameterized queries
- XSS mitigation through Angular's template sanitization
- Bean Validation (`@Valid`, Jakarta Validation) on all request payloads
---
 
## Default Accounts
 
| Role        | Email               | Password  |
|-------------|---------------------|-----------|
| Admin       | admin@bookhub.com   | Pa$$w0rd1234 |
| Librarian   | librarian@bookhub.com | Pa$$w0rd1234 |
| Reader      | reader@bookhub.com  | Pa$$w0rd1234 |
