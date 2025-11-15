package ru.enikeev.stolplusapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private UUID id;
    private String name;
    private String description;
    // Можно добавить: Integer furnitureCount (вычисляемое поле)
}
