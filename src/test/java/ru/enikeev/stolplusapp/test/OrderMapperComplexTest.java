package ru.enikeev.stolplusapp.test;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.enikeev.stolplusapp.dto.response.OrderResponseDTO;
import ru.enikeev.stolplusapp.dto.response.OrderShortResponseDTO;
import ru.enikeev.stolplusapp.mapper.OrderMapper;
import ru.enikeev.stolplusapp.model.Order;
import ru.enikeev.stolplusapp.model.User;

import ru.enikeev.stolplusapp.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderMapperComplexTest {
    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void testOrderToResponseDTOWithoutFurniture() {
        // Given - Order БЕЗ Furniture (избегаем проблем с зависимостями)
        User user = new User();
        user.setId(UUID.randomUUID());

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setSumCost(new BigDecimal("15000.00"));
        order.setDeliveryDate(LocalDate.now().plusDays(5));
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now().minusHours(1));
        order.setStatus(OrderStatus.NEW);
        order.setCustomerPhone("+79123456789");
        order.setComment("Test order");
        // furniture не устанавливаем - будет null

        // When
        OrderResponseDTO dto = orderMapper.toResponseDTO(order);

        // Then
        assertNotNull(dto);
        assertEquals(order.getId(), dto.getId());
        assertEquals(order.getSumCost(), dto.getSumCost());
        assertEquals(order.getDeliveryDate(), dto.getDeliveryDate());
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(order.getCreatedAt(), dto.getCreatedAt());
        assertEquals(order.getStatus(), dto.getStatus());
        assertEquals(order.getCustomerPhone(), dto.getCustomerPhone());
        assertEquals(order.getComment(), dto.getComment());

    }

    @Test
    void testOrderToShortResponseDTO() {
        // Given - ShortDTO не требует Furniture
        User user = new User();
        user.setId(UUID.randomUUID());

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setSumCost(new BigDecimal("10000.00"));
        order.setDeliveryDate(LocalDate.now().plusDays(3));
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now().minusDays(1));
        order.setStatus(OrderStatus.READY);
        order.setCustomerPhone("+79001234567");

        // When
        OrderShortResponseDTO shortDTO = orderMapper.toShortResponseDTO(order);

        // Then
        assertNotNull(shortDTO);
        assertEquals(order.getId(), shortDTO.getId());
        assertEquals(order.getSumCost(), shortDTO.getSumCost());
        assertEquals(order.getDeliveryDate(), shortDTO.getDeliveryDate());
        assertEquals(user.getId(), shortDTO.getUserId());
        assertEquals(order.getCreatedAt(), shortDTO.getCreatedAt());
        assertEquals(order.getStatus(), shortDTO.getStatus());
        assertEquals(order.getCustomerPhone(), shortDTO.getCustomerPhone());
    }

}
