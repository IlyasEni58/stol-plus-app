package ru.enikeev.stolplusapp.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class EnumResponseDTO {
    private String value;
    private String label;

    public static List<EnumResponseDTO> getMaterials() {
        return Arrays.stream(ru.enikeev.stolplusapp.model.enums.Material.values())
                .map(material -> new EnumResponseDTO(material.name(),
                        switch (material){
                            case GLASS -> "Стекло";
                            case PLASTIC -> "Пластик";
                            default -> material.name();
                        }
                ))
                .collect(Collectors.toList());
    }

    public static List<EnumResponseDTO> getOrderStatuses() {
        return Arrays.stream(ru.enikeev.stolplusapp.model.enums.OrderStatus.values())
                .map(status -> new EnumResponseDTO(status.name(),
                        switch (status){
                            case NEW -> "Новый";
                            case IN_PRODUCTION -> "В производстве";
                            case READY -> "Готов";
                            case DELIVERED -> "Доставлен";
                            case CANCELLED -> "Отменен";
                            default -> status.name();
                        }
                        ))
                .collect(Collectors.toList());

    }

    public static List<EnumResponseDTO> getRoles() {
        return Arrays.stream(ru.enikeev.stolplusapp.model.enums.Role.values())
                .map(role -> new EnumResponseDTO(role.name(),
                        switch(role){
                            case USER -> "Пользователь";
                            case ADMIN -> "Администратор";
                        }
                        ))
                .collect(Collectors.toList());
    }

}
