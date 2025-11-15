package ru.enikeev.stolplusapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.enikeev.stolplusapp.model.enums.OrderStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequestDTO {
    private OrderStatus status;
    private String comment;
    private LocalDate deliveryDate;
}
