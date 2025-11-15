package ru.enikeev.stolplusapp.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.enikeev.stolplusapp.model.enums.Material;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FurnitureRequestDTO {
    @NotBlank(message = "Название товара обязательно")
    @Size(max = 200, message = "Название товара не должно превышать 200 символов")
    private String name;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    private BigDecimal price;

    @NotNull(message = "Материал обязателен")
    private Material material;

    @NotNull(message = "Список категорий обязателен")
    @NotEmpty(message = "Товар должен принадлежать хотя бы к одной категории")
    private Set<UUID> categoryIds;

    @NotBlank(message = "Описание обязательно")
    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @NotBlank(message = "URL изображения обязателен")
    private String imageUrl;

    @NotNull(message = "Срок производства обязателен")
    @Min(value = 1, message = "Срок производства должен быть не менее 1 дня")
    @Max(value = 365, message = "Срок производства не должен превышать 365 дней")
    private Integer productionDays;
}
