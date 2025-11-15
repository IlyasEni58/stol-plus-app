package ru.enikeev.stolplusapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.enikeev.stolplusapp.dto.request.FavoriteRequestDTO;
import ru.enikeev.stolplusapp.dto.response.FavoriteResponseDTO;
import ru.enikeev.stolplusapp.dto.response.FavoriteWithFurnitureResponse;
import ru.enikeev.stolplusapp.model.Favorite;

@Mapper(componentModel = "spring", uses = {FurnitureMapper.class})
public interface FavoriteMapper {


    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "furniture.id", target = "furnitureId")
    FavoriteResponseDTO toResponseDTO(Favorite favorite);

    @Mapping(source = "furniture", target = "furniture") // Использует FurnitureMapper
    FavoriteWithFurnitureResponse toResponseWithFurnitureDTO(Favorite favorite);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addedAt", ignore = true) // Или expression для текущего времени
    @Mapping(target = "user", ignore = true) // Устанавливается в сервисе
    @Mapping(target = "furniture", ignore = true) // Устанавливается в сервисе
    Favorite toEntity(FavoriteRequestDTO favoriteRequestDTO);
}
