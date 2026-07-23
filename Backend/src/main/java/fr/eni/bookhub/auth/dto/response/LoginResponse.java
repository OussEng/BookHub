package fr.eni.bookhub.auth.dto.response;

import fr.eni.bookhub.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String phone;
    private Role role;
    private String accessToken;
    private String refreshToken;
}
