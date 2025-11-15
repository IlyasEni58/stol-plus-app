package ru.enikeev.stolplusapp.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDTO {
    private String message;
    private String errorCode;
    private String timestamp;
}
