package fr.eni.bookhub.loan.dto.response;

import fr.eni.bookhub.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private Long id;
    private String email;
    private Role role;
    private String accessToken;
    private String refreshToken;

}
