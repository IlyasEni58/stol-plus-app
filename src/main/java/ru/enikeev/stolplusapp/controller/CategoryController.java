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
import ru.enikeev.stolplusapp.dto.request.CategoryRequestDTO;
import ru.enikeev.stolplusapp.dto.response.CategoryResponseDTO;
import ru.enikeev.stolplusapp.dto.response.FurnitureShortResponseDTO;
import ru.enikeev.stolplusapp.service.CategoryService;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Categories API",
        description = "Управление категориями мебели: создание, обновление, получение"
)
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Получить все категории",
            description = "Возвращает список всех категорий мебели"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категории получены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(
            summary = "Получить категорию по ID",
            description = "Возвращает информацию о категории по её идентификатору"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория найдена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
            @Parameter(
                    description = "UUID категории",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @PathVariable UUID id) {
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(
            summary = "Создать новую категорию",
            description = "Создает новую категорию мебели. Только для администраторов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или категория уже существует"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания категории",
                    required = true
            )
            @Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO category = categoryService.createCategory(request);
        return ResponseEntity.ok(category);
    }

    @Operation(
            summary = "Обновить категорию",
            description = "Обновляет информацию о категории. Только для администраторов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @Parameter(
                    description = "UUID категории",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные категории",
                    required = true
            )
            @Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(category);
    }

    @Operation(
            summary = "Удалить категорию",
            description = "Удаляет категорию. Нельзя удалить категорию если в ней есть мебель. Только для администраторов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Категория успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена"),
            @ApiResponse(responseCode = "400", description = "Невозможно удалить категорию с мебелью"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(
                    description = "UUID категории",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Найти категорию по названию",
            description = "Находит категорию по точному совпадению названия"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория найдена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByName(
            @Parameter(
                    description = "Название категории",
                    example = "Стулья",
                    required = true
            )
            @PathVariable String name) {
        CategoryResponseDTO category = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(category);
    }

    @Operation(
            summary = "Получить мебель в категории",
            description = "Возвращает всю мебель принадлежащую указанной категории"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель в категории получена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}/furniture")
    public ResponseEntity<List<FurnitureShortResponseDTO>> getFurnitureInCategory(
            @Parameter(
                    description = "UUID категории",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @PathVariable UUID id) {
        List<FurnitureShortResponseDTO> furniture = categoryService.getFurnitureInCategory(id);
        return ResponseEntity.ok(furniture);
    }
}