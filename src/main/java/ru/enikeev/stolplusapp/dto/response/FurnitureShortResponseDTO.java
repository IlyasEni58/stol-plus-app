package ru.enikeev.stolplusapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.enikeev.stolplusapp.model.enums.Material;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FurnitureShortResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal price;
    private Material material;
    private String imageUrl;
    private Integer productionDays;
    private Long popularity;
}
