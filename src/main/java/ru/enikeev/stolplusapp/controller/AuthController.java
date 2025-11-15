package ru.enikeev.stolplusapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.enikeev.stolplusapp.dto.request.UserLoginRequestDTO;
import ru.enikeev.stolplusapp.dto.request.UserRegistrationRequestDTO;
import ru.enikeev.stolplusapp.dto.response.UserResponseDTO;
import ru.enikeev.stolplusapp.service.UserService;

@Tag(name = "Authentification API", description = "Операции для аутентификации пользователей")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Регистрация нового пользователя", description = "Создание нового аккаунта")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegistrationRequestDTO request) {
        UserResponseDTO user = userService.register(request);
        return ResponseEntity.ok(user);
    }

        @Operation(summary = "Получить информацию о текущем пользователе", description = "Возвращает данные аутентифицированного пользователя")
        @PostMapping("/login")
        public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO loginRequest) {
            // Spring Security уже аутентифицировал пользователя через Basic Auth
            // Этот эндпоинт просто возвращает информацию о текущем пользователе
            UserResponseDTO user = userService.findByEmail(loginRequest.getEmail());
            return ResponseEntity.ok(user);
        }

    }

