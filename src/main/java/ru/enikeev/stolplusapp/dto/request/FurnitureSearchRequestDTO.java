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
public class FurnitureSearchRequestDTO {
    private String name;
    private Set<UUID> categoryIds;
    private Set<Material> materials;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minProductionDays;
    private Integer maxProductionDays;
}
