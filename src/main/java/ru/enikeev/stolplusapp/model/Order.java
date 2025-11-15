package ru.enikeev.stolplusapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.enikeev.stolplusapp.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sum_cost" , nullable = false)
    private BigDecimal sumCost;

    //дата доставки
    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;


    @ManyToMany // один заказ может содержать -> несколько товаров
                // один товар может быть в -> нескольких заказах
    @JoinTable(
            name = "order_furniture", // промежуточная таблица
            joinColumns = @JoinColumn(name = "order_id"),  // ID этого класса (Order)
            inverseJoinColumns = @JoinColumn(name = "furniture_id") // ID связанного класса
    )
    private List<Furniture> furniture = new ArrayList<>();

    @ManyToOne // Несколько заказов -> один пользователь
    @JoinColumn(name = "user_id", nullable = false) // владелец Order (содержит внешний ключ user_id)
    private User user;


    private LocalDateTime createdAt = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW; // "НОВЫЙ", "В РАБОТЕ", "ВЫПОЛНЕН"
    private String customerPhone; // телефон клиента (может отличаться от user.phone)
    private String comment; //комментарий менеджера


}
