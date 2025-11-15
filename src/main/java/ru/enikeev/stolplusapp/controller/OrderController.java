package ru.enikeev.stolplusapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.enikeev.stolplusapp.dto.request.OrderRequestDTO;
import ru.enikeev.stolplusapp.dto.response.OrderResponseDTO;
import ru.enikeev.stolplusapp.dto.response.OrderShortResponseDTO;
import ru.enikeev.stolplusapp.model.enums.OrderStatus;
import ru.enikeev.stolplusapp.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Orders API",
        description = "Операции для управления заказами: создание, отслеживание, обновление статусов"
)
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "Создание нового заказа",
            description = "Создает новый заказ для пользователя. В заказ можно добавить несколько товаров."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заказ успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или товары не найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания заказа",
                    required = true
            )
            @Valid @RequestBody OrderRequestDTO request,
            @Parameter(
                    description = "ID пользователя из заголовка",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @RequestHeader("X-User-Id") UUID userId) {
        OrderResponseDTO order = orderService.createOrder(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @Operation(
            summary = "Получить заказ по ID",
            description = "Возвращает полную информацию о заказе включая список товаров и детали доставки"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ найден"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(
                    description = "UUID заказа",
                    example = "123e4567-e89b-12d3-a456-426614174000",
                    required = true
            )
            @PathVariable UUID id) {
        OrderResponseDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @Operation(
            summary = "Получить заказы пользователя",
            description = "Возвращает список всех заказов конкретного пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заказов получен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderShortResponseDTO>> getUserOrders(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable UUID userId) {
        List<OrderShortResponseDTO> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Получить все заказы",
            description = "Возвращает список всех заказов в системе. Только для администраторов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заказов получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<OrderShortResponseDTO>> getAllOrders() {
        List<OrderShortResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Обновить статус заказа",
            description = "Изменяет статус заказа. Доступные статусы: NEW, IN_PRODUCTION, READY, DELIVERED, CANCELLED"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус заказа обновлен"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный статус"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @Parameter(description = "ID заказа", required = true)
            @PathVariable UUID id,
            @Parameter(
                    description = "Новый статус заказа",
                    example = "IN_PRODUCTION",
                    required = true
            )
            @RequestBody OrderStatus newStatus) {
        OrderResponseDTO order = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(order);
    }

    @Operation(
            summary = "Добавить комментарий к заказу",
            description = "Добавляет или обновляет комментарий менеджера к заказу. Только для менеджеров."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий добавлен"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PatchMapping("/{id}/comment")
    public ResponseEntity<OrderResponseDTO> addOrderComment(
            @Parameter(description = "ID заказа", required = true)
            @PathVariable UUID id,

            @Parameter(
                    description = "Комментарий менеджера",
                    example = "Клиент просит доставить к 18:00",
                    required = true
            )
            @RequestBody String comment) {
        OrderResponseDTO order = orderService.addOrderComment(id, comment);
        return ResponseEntity.ok(order);
    }

    @Operation(
            summary = "Отменить заказ",
            description = "Отменяет заказ. Нельзя отменить доставленный или уже отмененный заказ."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ отменен"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "400", description = "Невозможно отменить заказ"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @Parameter(description = "ID заказа", required = true)
            @PathVariable UUID id) {
        OrderResponseDTO order = orderService.cancelOrder(id);
        return ResponseEntity.ok(order);
    }

    @Operation(
            summary = "Получить просроченные заказы",
            description = "Возвращает список заказов с просроченной датой доставки. Только для администраторов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список просроченных заказов получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/overdue")
    public ResponseEntity<List<OrderShortResponseDTO>> getOverdueOrders() {
        List<OrderShortResponseDTO> orders = orderService.getOverdueOrders();
        return ResponseEntity.ok(orders);
    }
}
