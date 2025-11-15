package ru.enikeev.stolplusapp.dto.request;

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
public class FurnitureUpdateRequestDTO {
    private String name;
    private BigDecimal price;
    private Material material;
    private Set<UUID> categoryIds;
    private String description;
    private String imageUrl;
    private Integer productionDays;
}
