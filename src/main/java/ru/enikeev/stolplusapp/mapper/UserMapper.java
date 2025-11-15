package ru.enikeev.stolplusapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.enikeev.stolplusapp.dto.request.UserRegistrationRequestDTO;
import ru.enikeev.stolplusapp.dto.request.UserRequestDTO;
import ru.enikeev.stolplusapp.dto.request.UserUpdateRequestDTO;
import ru.enikeev.stolplusapp.dto.response.UserResponseDTO;
import ru.enikeev.stolplusapp.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "discount", expression = "java(java.math.BigDecimal.ZERO)")
    User toEntity(UserRequestDTO userRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(ru.enikeev.stolplusapp.model.enums.Role.USER)")
    @Mapping(target = "discount", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "orders", ignore = true)
    User toEntityFromRegistration(UserRegistrationRequestDTO registrationDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Пароль обновляется отдельно
    @Mapping(target = "role", ignore = true) // Роль обычно не меняется через update
    @Mapping(target = "discount", ignore = true) // Скидку меняет админ отдельно
    @Mapping(target = "orders", ignore = true)
    void updateUserFromDTO(UserUpdateRequestDTO updateDTO, @MappingTarget User user);

}
