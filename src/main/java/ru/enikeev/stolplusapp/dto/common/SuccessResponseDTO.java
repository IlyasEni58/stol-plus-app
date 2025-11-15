package ru.enikeev.stolplusapp.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SuccessResponseDTO {
    private String message;
    private Object data;
}
