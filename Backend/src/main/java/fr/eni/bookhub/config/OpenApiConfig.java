package fr.eni.bookhub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.1 documentation configuration for the BookHub API.
 *
 * <p>Configures the Swagger UI metadata via {@link OpenAPIDefinition}
 * and registers a global Bearer token security scheme via {@link SecurityScheme},
 * allowing JWT authentication directly from the Swagger UI interface.</p>
 *
 * <p>No Redoc documentation for this project — Swagger UI only.</p>
 *
 * @see <a href="https://swagger.io/specification/">OpenAPI Specification</a>
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "BookHub API",
                version = "1.0.0",
                description = """
                        ## BookHub REST API
                        
                        A library management API built with **Spring Boot**, **JWT authentication** and role-based access control.
                        
                        ### Authentication
                        
                        All protected endpoints require a valid JWT token passed as a Bearer token in the `Authorization` header:
                        ```
                        Authorization: Bearer <your-token>
                        ```
                        
                        To obtain a token:
                        1. Register a new account via `POST /api/auth/register`
                        2. Or login via `POST /api/auth/login`
                        3. Copy the `accessToken` from the response
                        4. Click the **Authorize** button above and paste the token
                        
                        The refresh token is set automatically as an HttpOnly cookie (`refresh_token`, 7-day expiry) on login. Use `POST /api/auth/refresh` to obtain a new access token when it expires.
                        
                        ### Roles
                        
                        Three roles exist: `ROLE_USER` (default on registration), `ROLE_ADMIN` and `ROLE_LIBRARIAN`. Some endpoints (loan listing, review moderation) are restricted to `ROLE_ADMIN` and `ROLE_LIBRARIAN`.
                        
                        ### Error responses
                        
                        All error responses follow a single-field structure:
                        ```json
                        {
                          "error": "Book not found"
                        }
                        ```
                        
                        On validation errors, only the first failing field's message is returned, not a full list.
                        """,
                contact = @Contact(
                        name = "Matthieu ROBERT, Maxime BOURRET, Mehdi ROCHEREAU, Mykhailo TIELIEHIN, Oussama AIT LAHSSAINE",
                        url = "https://github.com/OussEng/BookHub"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local development server")
                // TODO: replace 8080 with the actual local port, and add the production server URL once deployed
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Bearer token obtained from POST /api/auth/login or POST /api/auth/register"
)
public class OpenApiConfig {
}