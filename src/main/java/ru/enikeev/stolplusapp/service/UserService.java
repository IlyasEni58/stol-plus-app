package ru.enikeev.stolplusapp.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.enikeev.stolplusapp.dto.request.UserRegistrationRequestDTO;
import ru.enikeev.stolplusapp.dto.request.UserUpdateRequestDTO;
import ru.enikeev.stolplusapp.dto.response.UserResponseDTO;
import ru.enikeev.stolplusapp.exception.UserNotFoundException;
import ru.enikeev.stolplusapp.mapper.UserMapper;
import ru.enikeev.stolplusapp.model.User;
import ru.enikeev.stolplusapp.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    /**
     * Найти пользователя по id
     */
    public UserResponseDTO findById(UUID id) {
        log.info("Поиск пользователей по ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));
        return userMapper.toResponseDTO(user);
    }

    /**
     * Найти всех пользователей
     */
    public List<UserResponseDTO> findAll() {
        log.info("Поиск всех пользователей");
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    /**
     * Регистрация нового пользователя
     */
    @Transactional
    public UserResponseDTO register(UserRegistrationRequestDTO registrationRequestDTO) {
        log.info("Регистрация нового пользователя: {}", registrationRequestDTO.getEmail());

        //Проверяем нет ли уже пользователя с таким email
        if (userRepository.existsByEmail(registrationRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Пользователь с email " + registrationRequestDTO.getEmail() + "уже существует");
        }

        //Проверяем нет ли пользователя с таким username
        if (userRepository.existsByUserName(registrationRequestDTO.getUserName())) {
            throw new IllegalArgumentException("Пользователь с username " + registrationRequestDTO.getUserName() + "уже существует");
        }

        //Маппим DTO в Entity
        User user = userMapper.toEntityFromRegistration(registrationRequestDTO);

        //Хэшируем пароль
        user.setPassword(passwordEncoder.encode(registrationRequestDTO.getPassword()));

        //Сохраняем пользователя
        User savedUser = userRepository.save(user);
        log.info("Пользователь успешно зарегистрирован с ID: {}", savedUser.getId());

        return userMapper.toResponseDTO(savedUser);
    }

    /**
     * Обновление данных пользователя
     */
    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateRequestDTO updateRequest) {
        log.info("Обновление пользователя с ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));

        //Обновляем только разрешенные поля
        userMapper.updateUserFromDTO(updateRequest, existingUser);

        //сохраняем обновленного пользователя
        User updateUser = userRepository.save(existingUser);
        log.info("Пользователь с ID {} успешно обновлен", id);

        return userMapper.toResponseDTO(updateUser);
    }

    /**
     * Удаление пользователя
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Удаление пользователя с ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }
        userRepository.deleteById(id);
        log.info("Пользователь с ID {} успешно удален", id);
    }

    /**
     * Поиск пользователя по Email
     */
    public UserResponseDTO findByEmail(String email) {
        log.info("Поиск пользователя по email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email " + email + "не найден"));
        return userMapper.toResponseDTO(user);
    }

    /**
     * Проверка существования пользователя по Email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Изменение пароля пользователя
     */
    @Transactional
    public void changePassword(UUID userId, String newPassword) {
        log.info("Изменения пароля для пользователя с ID:{}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Пароль для пользователя с ID {} успешно изменен", userId);
    }
}

