package ru.enikeev.stolplusapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.enikeev.stolplusapp.model.Order;
import ru.enikeev.stolplusapp.model.User;
import ru.enikeev.stolplusapp.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    //найти все заказы пользователя
    List<Order> findByUser(User user);

    //найти заказы по статусу
    List<Order> findByStatus(OrderStatus status);

    //найти заказы за период
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    //найти просроченные заказы(кастомный запрос)
    @Query("SELECT o FROM Order o WHERE o.deliveryDate < CURRENT_DATE AND o.status NOT IN :completedStatuses")
    List<Order>findOverdueOrders(@Param("completedStatuses")
    List<OrderStatus> completedStatuses);

    //посчитать общую выручку за период
    @Query("SELECT SUM(o.sumCost) FROM Order o WHERE o.createdAt BETWEEN :start AND :end ")
    BigDecimal getTotalRevenueForPeriod(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    //найти заказы с определенным товаром
    @Query("SELECT o FROM Order o JOIN o.furniture f WHERE f.id = :furnitureId")
    List<Order> findOrderByFurnitureId(@Param("furnitureId") UUID furnitureId);

    //статистика по статусам заказа
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderStatusStatistics();

}
