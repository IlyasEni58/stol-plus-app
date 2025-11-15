package ru.enikeev.stolplusapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.enikeev.stolplusapp.model.enums.OrderStatus;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchRequestDTO {
    private Set<UUID> userIds;
    private Set<OrderStatus> statuses;
    private LocalDate deliveryDateFrom;
    private LocalDate deliveryDateTo;
}
