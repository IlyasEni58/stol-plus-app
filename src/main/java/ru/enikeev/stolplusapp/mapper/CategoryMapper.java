package ru.enikeev.stolplusapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.enikeev.stolplusapp.dto.request.CategoryRequestDTO;

import ru.enikeev.stolplusapp.dto.response.CategoryResponseDTO;
import ru.enikeev.stolplusapp.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO toResponseDTO(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "furniture", ignore = true)
    Category toEntity(CategoryRequestDTO categoryRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "furniture", ignore = true)
    void updateCategoryFromDTO(CategoryRequestDTO categoryRequestDTO, @MappingTarget Category category);

}
