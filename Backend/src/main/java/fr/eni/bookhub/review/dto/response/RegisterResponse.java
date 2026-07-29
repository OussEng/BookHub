package fr.eni.bookhub.review.dto.response;

import fr.eni.bookhub.user.entity.Role;
import fr.eni.bookhub.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String email;
    private Role role;



    public static RegisterResponse fromUserEntity(User user) {
        return new RegisterResponse(user.getId(), user.getEmail(), user.getRole());
    }

}
