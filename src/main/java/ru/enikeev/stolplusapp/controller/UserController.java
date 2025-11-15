package ru.enikeev.stolplusapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.enikeev.stolplusapp.dto.request.UserRegistrationRequestDTO;
import ru.enikeev.stolplusapp.dto.request.UserUpdateRequestDTO;
import ru.enikeev.stolplusapp.dto.response.UserResponseDTO;
import ru.enikeev.stolplusapp.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(
        name = "Users API",
        description = "Операции для управления пользователями: регистрация, обновление, удаление"
)
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех зарегистрированных пользователей. Только для администраторов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешное получение списка"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }


    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает детальную информацию о пользователе по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@Parameter(
            description = "UUID пользователя",
            example = "123e4567-e89b-12d3-a456-426614174000",
            required = true
    )
                                                       @PathVariable UUID id) {
        UserResponseDTO user = userService.findById(id);
        return ResponseEntity.ok(user);

    }

    @Operation(
            summary = "Обновление данных пользователя",
            description = "Обновляет информацию о пользователе. Можно изменить имя, email, телефон и адрес."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")

    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные пользователя",
                    required = true
            )
            @Valid @RequestBody UserUpdateRequestDTO request) {
        UserResponseDTO updatedUser = userService.update(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Полностью удаляет пользователя из системы. Операция необратима."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Поиск пользователя по email",
            description = "Находит пользователя по email адресу. Email должен быть точным совпадением."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @Parameter(
                    description = "Email адрес пользователя",
                    example = "ivan@mail.ru",
                    required = true
            )
            @PathVariable String email) {
        UserResponseDTO user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }
    @Operation(
            summary = "Смена пароля",
            description = "Изменяет пароль пользователя. Новый пароль должен быть не менее 6 символов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль успешно изменен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный пароль"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "ID пользователя",required = true)
            @PathVariable UUID id,
                                               @Parameter(description = "Новый пароль", required = true)
                                               @RequestBody String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }
}
