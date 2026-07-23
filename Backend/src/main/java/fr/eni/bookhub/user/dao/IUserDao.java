package fr.eni.bookhub.user.dao;

import fr.eni.bookhub.user.entity.User;

import java.util.Optional;

public interface IUserDao {
    Optional <User> findByEmail(String email);
    User save(User user);
}
