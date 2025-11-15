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
import ru.enikeev.stolplusapp.dto.request.FurnitureRequestDTO;
import ru.enikeev.stolplusapp.dto.request.FurnitureUpdateRequestDTO;
import ru.enikeev.stolplusapp.dto.response.FurnitureResponseDTO;
import ru.enikeev.stolplusapp.dto.response.FurnitureShortResponseDTO;
import ru.enikeev.stolplusapp.model.enums.Material;
import ru.enikeev.stolplusapp.service.FurnitureService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(
        name = "Furniture API",
        description = "Операции для управления каталогом мебели: поиск, фильтрация, создание, обновление"
)
@RestController
@RequestMapping("/api/furniture")
@RequiredArgsConstructor
public class FurnitureController {

    private final FurnitureService furnitureService;

    @Operation(
            summary = "Получить весь каталог мебели",
            description = "Возвращает список всей мебели в каталоге с краткой информацией"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Каталог мебели получен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<FurnitureShortResponseDTO>> getAllFurniture() {
        List<FurnitureShortResponseDTO> furniture = furnitureService.getAllFurniture();
        return ResponseEntity.ok(furniture);
    }

    @Operation(
            summary = "Получить мебель по ID",
            description = "Возвращает полную информацию о конкретной мебели включая описание и категории"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель найдена"),
            @ApiResponse(responseCode = "404", description = "Мебель не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FurnitureResponseDTO> getFurnitureById(
            @Parameter(
                    description = "ID мебели",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @PathVariable UUID id) {
        FurnitureResponseDTO furniture = furnitureService.getFurnitureById(id);
        return ResponseEntity.ok(furniture);
    }
    @Operation(
            summary = "Создать новую мебель",
            description = "Добавляет новую мебель в каталог. Только для администраторов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или категории не найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<FurnitureResponseDTO> createFurniture(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания мебели",
                    required = true
            )
            @Valid @RequestBody FurnitureRequestDTO request) {
        FurnitureResponseDTO furniture = furnitureService.createFurniture(request);
        return ResponseEntity.ok(furniture);
    }

    @Operation(
            summary = "Обновить мебель",
            description = "Обновляет информацию о мебели. Только для администраторов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Мебель не найдена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FurnitureResponseDTO> updateFurniture(
            @Parameter(description = "UUID мебели", required = true)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные мебели",
                    required = true
            )
            @Valid @RequestBody FurnitureUpdateRequestDTO request) {
        FurnitureResponseDTO furniture = furnitureService.updateFurniture(id, request);
        return ResponseEntity.ok(furniture);
    }

    @Operation(
            summary = "Удалить мебель",
            description = "Удаляет мебель из каталога. Только для администраторов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Мебель успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Мебель не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFurniture(
            @Parameter(description = "ID мебели", required = true)
            @PathVariable UUID id) {
        furnitureService.deleteFurniture(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Поиск мебели по названию",
            description = "Ищет мебель по частичному совпадению названия. Поиск не чувствителен к регистру."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Результаты поиска получены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/search")
    public ResponseEntity<List<FurnitureShortResponseDTO>> searchFurniture(
            @Parameter(
                    description = "Часть названия для поиска",
                    example = "стул",
                    required = true
            )
            @RequestParam String name) {
        List<FurnitureShortResponseDTO> furniture = furnitureService.searchFurnitureByName(name);
        return ResponseEntity.ok(furniture);
    }

    @Operation(
            summary = "Фильтр мебели по материалу",
            description = "Возвращает мебель из указанного материала. Доступные материалы: GLASS, PLASTIC"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель по материалу получена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/material/{material}")
    public ResponseEntity<List<FurnitureShortResponseDTO>> getFurnitureByMaterial(
            @Parameter(
                    description = "Материал мебели",
                    example = "GLASS",
                    required = true
            )
            @PathVariable Material material) {
        List<FurnitureShortResponseDTO> furniture = furnitureService.getFurnitureByMaterial(material);
        return ResponseEntity.ok(furniture);
    }

    @Operation(
            summary = "Получить мебель по категории",
            description = "Возвращает всю мебель из указанной категории"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель по категории получена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<FurnitureShortResponseDTO>> getFurnitureByCategory(
            @Parameter(
                    description = "UUID категории",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @PathVariable UUID categoryId) {
        List<FurnitureShortResponseDTO> furniture = furnitureService.getFurnitureByCategory(categoryId);
        return ResponseEntity.ok(furniture);
    }

    @Operation(
            summary = "Фильтр по ценовому диапазону",
            description = "Возвращает мебель в указанном ценовом диапазоне"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Мебель по цене получена"),
            @ApiResponse(responseCode = "400", description = "Некорректный ценовой диапазон"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/price-range")
    public ResponseEntity<List<FurnitureShortResponseDTO>> getFurnitureByPriceRange(
            @Parameter(
                    description = "Минимальная цена",
                    example = "1000.00",
                    required = true
            )
            @RequestParam BigDecimal minPrice,
            @Parameter(
                    description = "Максимальная цена",
                    example = "10000.00",
                    required = true
            )
            @RequestParam BigDecimal maxPrice) {
        List<FurnitureShortResponseDTO> furniture = furnitureService.getFurnitureByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(furniture);
    }

    @Operation(
            summary = "Сортировка мебели по цене (по возрастанию)",
            description = "Возвращает весь каталог мебели отсортированный по цене от самой дешевой к самой дорогой"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отсортированная мебель получена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/sorted/price-asc")
    public ResponseEntity<List<FurnitureShortResponseDTO>> getFurnitureSortedByPriceAsc() {
        List<FurnitureShortResponseDTO> furniture = furnitureService.getFurnitureSortedByPriceAsc();
        return ResponseEntity.ok(furniture);
    }

    @Operation(
            summary = "Сортировка мебели по цене (по убыванию)",
            description = "Возвращает весь каталог мебели отсортированный по цене от самой дорогой к самой дешевой"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отсортированная мебель получена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/sorted/price-desc")
    public ResponseEntity<List<FurnitureShortResponseDTO>> getFurnitureSortedByPriceDesc() {
        List<FurnitureShortResponseDTO> furniture = furnitureService.getFurnitureSortedByPriceDesc();
        return ResponseEntity.ok(furniture);
    }
    @Operation(
            summary = "Получить популярную мебель",
            description = "Возвращает список популярной мебели на основе статистики заказов и добавлений в избранное"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Популярная мебель получена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/popular")
    public ResponseEntity<List<FurnitureShortResponseDTO>> getPopularFurniture() {
        List<FurnitureShortResponseDTO> furniture = furnitureService.getPopularFurniture();
        return ResponseEntity.ok(furniture);
    }

}
