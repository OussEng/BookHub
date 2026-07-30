package fr.eni.bookhub.auth.controller;

import fr.eni.bookhub.auth.dto.request.LoginRequest;
import fr.eni.bookhub.auth.dto.request.RegisterRequest;
import fr.eni.bookhub.auth.dto.response.LoginResponse;
import fr.eni.bookhub.auth.dto.response.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

/**
 * OpenAPI documentation interface for {@link AuthController}.
 *
 * <p>All endpoints here are public and do not require a JWT token,
 * as they are the entry points to obtain one.</p>
 *
 * @see AuthController
 */
@Tag(
        name = "Authentication",
        description = "User registration, login, token refresh and logout. All endpoints are public."
)
public interface AuthControllerApi {

    @Operation(
            summary = "Register a new user",
            description = "Creates a new account with the ROLE_USER role. Email, username and phone number must each be unique across all accounts.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Account successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "id": 1,
                                                      "email": "john@example.com",
                                                      "role": "ROLE_USER"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "error": "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email, username or phone number already in use",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Email taken", value = "{\"error\": \"Email already in use\"}"),
                                            @ExampleObject(name = "Username taken", value = "{\"error\": \"Username already in use\"}"),
                                            @ExampleObject(name = "Phone taken", value = "{\"error\": \"Phone number already in use\"}")
                                    }
                            )
                    )
            }
    )
    ResponseEntity<RegisterResponse> register(@Valid RegisterRequest request);

    @Operation(
            summary = "Login",
            description = """
                    Authenticates a user by email and password.
                    
                    On success, the access token is returned in the response body and the refresh token is set as an HttpOnly cookie (`refresh_token`, 7-day expiry, `Strict` SameSite, scoped to `/api/auth`). The `refreshToken` field in the response body is always `null`.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "id": 1,
                                                      "firstname": "John",
                                                      "lastname": "Doe",
                                                      "username": "john",
                                                      "email": "john@example.com",
                                                      "phone": "+33612345678",
                                                      "role": "ROLE_USER",
                                                      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                      "refreshToken": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Email is required\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials. The same message is returned whether the email doesn't exist or the password is wrong, to avoid leaking which one is incorrect.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Invalid email or password\"}")
                            )
                    )
            }
    )
    ResponseEntity<LoginResponse> login(@Valid LoginRequest request, HttpServletResponse response);

    @Operation(
            summary = "Refresh the access token",
            description = "Issues a new access token and rotates the refresh token cookie, based on the `refresh_token` HttpOnly cookie sent with the request. No request body.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token successfully refreshed",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "id": 1,
                                                      "firstname": "John",
                                                      "lastname": "Doe",
                                                      "username": "john",
                                                      "email": "john@example.com",
                                                      "phone": "+33612345678",
                                                      "role": "ROLE_USER",
                                                      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                                      "refreshToken": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing `refresh_token` cookie. No response body.",
                            content = @Content
                    )
            }
    )
    ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response);

    @Operation(
            summary = "Logout",
            description = "Revokes the refresh token (if the `refresh_token` cookie is present) and clears the cookie. Returns 200 even if no cookie was sent.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content)
            }
    )
    ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);
}