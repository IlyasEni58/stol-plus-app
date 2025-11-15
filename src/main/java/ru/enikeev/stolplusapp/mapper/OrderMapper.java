package ru.enikeev.stolplusapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.enikeev.stolplusapp.dto.request.OrderRequestDTO;
import ru.enikeev.stolplusapp.dto.response.OrderResponseDTO;
import ru.enikeev.stolplusapp.dto.response.OrderShortResponseDTO;
import ru.enikeev.stolplusapp.model.Order;
import ru.enikeev.stolplusapp.model.enums.OrderStatus;


@Mapper(componentModel = "spring", uses = {FurnitureMapper.class})// uses - используем другие мапперы
public interface OrderMapper {

    // Маппинг из Order в OrderResponseDTO
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "furniture", target = "furniture") // Использует FurnitureMapper через uses
    OrderResponseDTO toResponseDTO(Order order);

    // Маппинг из Order в OrderShortResponseDTO
    @Mapping(source = "user.id", target = "userId")
    OrderShortResponseDTO toShortResponseDTO(Order order);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sumCost", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "furniture", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status" , expression = "java(ru.enikeev.stolplusapp.model.enums.OrderStatus.NEW)")
    Order toEntity(OrderRequestDTO orderRequestDTO);
}
