package fr.eni.bookhub.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be less than 50 chars")
    private String firstname;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be less than 50 chars")
    private String lastname;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 chars")
    private String username;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Please provide a valid phone number")
    private String phone;

    @NotBlank(message = "Email is required")
    @Size(max = 30, message = "Email must be less than 30 chars")
    @Email(message = "Please use a valid Email")
    private String email;

    @NotBlank(message = "Password is Required")
    @Size(min = 8, message = "Password must be at least 8 chars")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;
}