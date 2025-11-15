package ru.enikeev.stolplusapp.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.enikeev.stolplusapp.dto.request.OrderRequestDTO;
import ru.enikeev.stolplusapp.dto.response.OrderResponseDTO;
import ru.enikeev.stolplusapp.dto.response.OrderShortResponseDTO;
import ru.enikeev.stolplusapp.exception.OrderNotFoundException;
import ru.enikeev.stolplusapp.exception.UserNotFoundException;
import ru.enikeev.stolplusapp.mapper.OrderMapper;
import ru.enikeev.stolplusapp.model.Furniture;
import ru.enikeev.stolplusapp.model.Order;
import ru.enikeev.stolplusapp.model.User;
import ru.enikeev.stolplusapp.model.enums.OrderStatus;
import ru.enikeev.stolplusapp.repository.FurnitureRepository;
import ru.enikeev.stolplusapp.repository.OrderRepository;
import ru.enikeev.stolplusapp.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FurnitureRepository furnitureRepository;
    private final OrderMapper orderMapper;


    /**
     * Создание нового заказа
     */
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest, UUID userId) {
        log.info("Создание заказа для пользователя с ID: {}", userId);

        //Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + "не найден"));

        //Находим всю мебель по IDs
        List<Furniture> furniture = furnitureRepository.findAllById(orderRequest.getFurnitureIds());

        //Проверяем что найдена вся запрошенная мебель
        if (furniture.size() != orderRequest.getFurnitureIds().size()) {
            throw new IllegalArgumentException("Некоторые товары не найдены");
        }

        //Создаем заказ
        Order order = orderMapper.toEntity(orderRequest);
        order.setUser(user);
        order.setFurniture(furniture);
        order.setSumCost(calculateTotalCost(furniture));

        //Сохраняем заказ
        Order savedOrder = orderRepository.save(order);
        log.info("Заказ успешно создан по ID: {}", savedOrder.getId());

        return orderMapper.toResponseDTO(savedOrder);
    }

    /**
     * Получаем заказ по ID
     */
    public OrderResponseDTO getOrderById(UUID orderId) {
        log.info("Получение заказа по ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Заказ с ID " + orderId + " не найден"));

        return orderMapper.toResponseDTO(order);
    }

    /**
     * Получение всех заказов пользователя
     */
    public List<OrderShortResponseDTO> getUserOrders(UUID userId) {
        log.info("получение всех заказов пользователя с ID: {}", userId);

        //проверяем что пользователь существует
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("пользователь с ID " + userId + " не найден");

        }

        User user = new User();
        user.setId(userId); // прокси объект для поиска

        return orderRepository.findByUser(user).stream()
                .map(orderMapper::toShortResponseDTO)
                .toList();

    }


    /**
     * Получение всех заказов (для администратора)
     */
    public List<OrderShortResponseDTO> getAllOrders() {
        log.info("Получение всех заказов");

        return orderRepository.findAll().stream()
                .map(orderMapper::toShortResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Обновление статуса заказа
     */
    @Transactional
    public OrderResponseDTO updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        log.info("Обновление статуса заказа с ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Заказ с ID: " + orderId + " не найден"));

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        log.info("Статус заказа с ID: {} успешно обновлен на {}", orderId, newStatus);

        return orderMapper.toResponseDTO(updatedOrder);
    }

    /**
     * Добавление комментария к заказу (для менеджера)
     */

    @Transactional
    public OrderResponseDTO addOrderComment(UUID orderId, String comment) {
        log.info("Добавление комментария с заказом ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Заказ с ID " + orderId + " не найден"));

        order.setComment(comment);
        Order updateOrder = orderRepository.save(order);
        log.info("Комментарий к заказу с ID: {} успешно добавлен", orderId);

        return orderMapper.toResponseDTO(updateOrder);
    }

    /**
     * Отмена заказа
     */
    @Transactional
    public OrderResponseDTO cancelOrder(UUID orderId) {
        log.info("Отмена заказа с ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Заказ с ID " + orderId + " не найден"));

        //проверяем можно ли отменить заказ
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Невозможно отменить заказ со статусом: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order canccelledOrder = orderRepository.save(order);
        log.info("Заказ с ID {} успешно отменен", orderId);

        return orderMapper.toResponseDTO(canccelledOrder);
    }

    /**
     * Получение заказов со статусом
     */
    public List<OrderShortResponseDTO> getOverdueOrders() {
        log.info("Получение просроченных заказов");

        List<OrderStatus> completedStatuses = List.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED);
        return orderRepository.findOverdueOrders(completedStatuses).stream()
                .map(orderMapper::toShortResponseDTO)
                .toList();

    }

    /**
     * Получение общей стоимости заказа
     */
    private BigDecimal calculateTotalCost(List<Furniture> furniture) {
        return furniture.stream()
                .map(Furniture::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Получение статистики по заказам за период
     */
    public BigDecimal getTotalRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        log.info("Расчет выручки за период с {} по {}", start, end);

        BigDecimal revenue = orderRepository.getTotalRevenueForPeriod(start, end);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

}
