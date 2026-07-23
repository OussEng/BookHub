package fr.eni.bookhub.user.dao;

import fr.eni.bookhub.user.entity.User;
import fr.eni.bookhub.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserDaoImpl implements IUserDao {

    private final UserRepository userRepository;



    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
