package fr.eni.bookhub.genre.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Size(max = 30, message = "Email must be less than 30 chars")
    @Email(message = "Please use a valid Email")
    private String email;

    @NotBlank(message = "Password is Required")
    @Size(min = 8, message = "Password must be at least 8 chars")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;
}