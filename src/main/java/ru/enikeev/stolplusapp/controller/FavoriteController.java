package ru.enikeev.stolplusapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.enikeev.stolplusapp.dto.request.FavoriteRequestDTO;
import ru.enikeev.stolplusapp.dto.response.FavoriteResponseDTO;
import ru.enikeev.stolplusapp.dto.response.FavoriteWithFurnitureResponse;
import ru.enikeev.stolplusapp.dto.response.FurnitureShortResponseDTO;
import ru.enikeev.stolplusapp.service.FavoriteService;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Favorites API",
        description = "Управление избранными товарами: добавление, удаление, получение"
)
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "Добавить мебель в избранное",
            description = "Добавляет указанную мебель в избранное текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель добавлена в избранное"),
            @ApiResponse(responseCode = "400", description = "Мебель уже в избранном или не найдена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<FavoriteResponseDTO> addToFavorite(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для добавления в избранное",
                    required = true
            )
            @Valid @RequestBody FavoriteRequestDTO request,
            @Parameter(
                    description = "ID пользователя (из контекста безопасности)",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @RequestHeader("X-User-Id") UUID userId) {
        FavoriteResponseDTO favorite = favoriteService.addToFavorite(request, userId);
        return ResponseEntity.ok(favorite);
    }

    @Operation(
            summary = "Удалить мебель из избранного",
            description = "Удаляет указанную мебель из избранного текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Мебель удалена из избранного"),
            @ApiResponse(responseCode = "404", description = "Пользователь или мебель не найдены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping
    public ResponseEntity<Void> removeFromFavorite(
            @Parameter(
                    description = "ID мебели для удаления из избранного",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @RequestParam UUID furnitureId,
            @Parameter(
                    description = "ID пользователя (из контекста безопасности)",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @RequestHeader("X-User-Id") UUID userId) {
        favoriteService.removeFromFavorites(furnitureId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Получить избранное пользователя",
            description = "Возвращает список избранных товаров пользователя с полной информацией о мебели"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Избранное пользователя получено"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<FavoriteWithFurnitureResponse>> getUserFavorites(
            @Parameter(
                    description = "ID пользователя (из контекста безопасности)",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @RequestHeader("X-User-Id") UUID userId) {
        List<FavoriteWithFurnitureResponse> favorites = favoriteService.getUserFavorite(userId);
        return ResponseEntity.ok(favorites);
    }

    @Operation(
            summary = "Проверить наличие в избранном",
            description = "Проверяет находится ли указанная мебель в избранном у пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Результат проверки получен"),
            @ApiResponse(responseCode = "404", description = "Пользователь или мебель не найдены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/check")
    public ResponseEntity<Boolean> isInFavorites(
            @Parameter(
                    description = "ID мебели для проверки",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @RequestParam UUID furnitureId,
            @Parameter(
                    description = "ID пользователя (из контекста безопасности)",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @RequestHeader("X-User-Id") UUID userId) {
        boolean isInFavorites = favoriteService.isInFavorites(furnitureId, userId);
        return ResponseEntity.ok(isInFavorites);
    }

    @Operation(
            summary = "Получить популярную мебель",
            description = "Возвращает список популярной мебели на основе количества добавлений в избранное"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Популярная мебель получена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/popular")
    public ResponseEntity<List<FurnitureShortResponseDTO>> getPopularFurniture() {
        List<FurnitureShortResponseDTO> popularFurniture = favoriteService.getPopularFurniture();
        return ResponseEntity.ok(popularFurniture);
    }

    @Operation(
            summary = "Очистить всё избранное",
            description = "Удаляет все товары из избранного текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Избранное очищено"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearUserFavorites(
            @Parameter(
                    description = "ID пользователя (из контекста безопасности)",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @RequestHeader("X-User-Id") UUID userId) {
        favoriteService.clearUserFavorites(userId);
        return ResponseEntity.noContent().build();
    }
}