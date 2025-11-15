package ru.enikeev.stolplusapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.enikeev.stolplusapp.dto.request.FurnitureRequestDTO;
import ru.enikeev.stolplusapp.dto.request.FurnitureUpdateRequestDTO;
import ru.enikeev.stolplusapp.dto.response.FurnitureResponseDTO;
import ru.enikeev.stolplusapp.dto.response.FurnitureShortResponseDTO;
import ru.enikeev.stolplusapp.model.Furniture;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface FurnitureMapper {

    // Маппинг из Furniture в FurnitureResponseDTO
    @Mapping(source = "categories", target = "categories")
    FurnitureResponseDTO toResponseDTO(Furniture furniture);

    // Маппинг из Furniture в FurnitureShortResponseDTO
    FurnitureShortResponseDTO toShortResponseDTO(Furniture furniture);

    // Маппинг из FurnitureRequestDTO в Furniture (для создания)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Furniture toEntity(FurnitureRequestDTO furnitureRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    void updateFurnitureFromDTO(FurnitureUpdateRequestDTO updateRequestDTO, @MappingTarget Furniture furniture);
    // Маппинг для обновления Furniture
}
