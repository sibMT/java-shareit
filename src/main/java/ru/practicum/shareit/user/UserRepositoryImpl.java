package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> storage = new HashMap<>();
    private final Map<String, Long> idByEmail = new HashMap<>();
    private long idCounter = 0;

    @Override
    public User createUser(User user) {
        Objects.requireNonNull(user, "user is null");
        Objects.requireNonNull(user.getEmail(), "email is null");
        String email = user.getEmail().toLowerCase();

        if (idByEmail.containsKey(email)) {
            throw new IllegalArgumentException("Этот Email в базе уже зарегистрирован" + user.getEmail());
        }
        if (user.getId() == null) {
            user.setId(++idCounter);
        }
        storage.put(user.getId(), user);
        idByEmail.put(email, user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        Objects.requireNonNull(user, "user is null");
        Objects.requireNonNull(user.getId(), "id is null");

        User existing = storage.get(user.getId());
        if (existing == null) {
            throw new NoSuchElementException("User не найден, id=" + user.getId());
        }
        String oldEmail = existing.getEmail() == null ? null : existing.getEmail().toLowerCase();
        String newEmail = user.getEmail() == null ? oldEmail : user.getEmail().toLowerCase();

        if (!Objects.equals(oldEmail, newEmail)) {
            if (newEmail == null) {
                throw new IllegalArgumentException("email cannot be null");
            }
            if (idByEmail.containsKey(newEmail)) {
                throw new IllegalArgumentException("Email уже зарегистрирован: " + newEmail);
            }
            if (oldEmail != null) idByEmail.remove(oldEmail);
            idByEmail.put(newEmail, existing.getId());
            existing.setEmail(newEmail);
        }

        if (user.getName() != null) {
            existing.setName(user.getName());
        }

        storage.put(existing.getId(), existing);
        return existing;
    }

    @Override
    public void deleteUserById(Long id) {
        User removed = storage.remove(id);
        if (removed != null && removed.getEmail() != null) {
            idByEmail.remove(removed.getEmail().toLowerCase());
        }
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        if (email == null) return Optional.empty();
        Long id = idByEmail.get(email.toLowerCase());
        return id == null ? Optional.empty() : Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return email != null && idByEmail.containsKey(email.toLowerCase());
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(storage.values());
    }
}
