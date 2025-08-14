package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User createUser(User user);

    User updateUser(User user);

    void deleteUserById(Long id);

    Optional<User> findUserById(Long id);

    Optional<User> findUserByEmail(String email);

    boolean existsUserByEmail(String email);

    List<User> findAllUsers();
}
