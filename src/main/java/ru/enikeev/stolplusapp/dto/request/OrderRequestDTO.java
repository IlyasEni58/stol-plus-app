package ru.enikeev.stolplusapp.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    @NotNull(message = "Список товаров обязателен")
    @NotEmpty(message = "Заказ должен содержать хотя бы один товар")
    private List<UUID> furnitureIds;

    @NotNull(message = "Дата доставки обязательна")
    @Future(message = "Дата доставки должна быть в будущем")
    private LocalDate deliveryDate;

    @NotBlank(message = "Телефон клиента обязателен")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Телефон должен быть в формате +7XXXXXXXXXX")
    private String customerPhone;

    @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
    private String comment;
}
