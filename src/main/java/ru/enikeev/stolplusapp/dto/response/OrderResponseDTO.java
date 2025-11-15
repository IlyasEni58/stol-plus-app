package ru.enikeev.stolplusapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.enikeev.stolplusapp.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private UUID id;
    private BigDecimal sumCost;
    private LocalDate deliveryDate;
    private List<FurnitureResponseDTO> furniture;
    private UUID userId;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private String customerPhone;
    private String comment;
}
