package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserCreateDto dto) {
        if (userRepository.existsUserByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email уже существует: " + dto.getEmail());
        }
        User created = userRepository.createUser(UserMapper.toUser(dto));
        return UserMapper.toUserDto(created);
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto dto) {
        User existing = userRepository.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + userId));

        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            Optional<User> other = userRepository.findUserByEmail(dto.getEmail());
            if (other.isPresent() && !other.get().getId().equals(existing.getId())) {
                throw new IllegalArgumentException("Email уже существует: " + dto.getEmail());
            }
            existing.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        User saved = userRepository.updateUser(existing);
        return UserMapper.toUserDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUserById(id);
    }

    @Override
    public UserDto getUserById(Long id) {
        User u = userRepository.findUserById(id)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + id));
        return UserMapper.toUserDto(u);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAllUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }
}
