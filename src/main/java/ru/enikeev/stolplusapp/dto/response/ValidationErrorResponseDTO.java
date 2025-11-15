package ru.enikeev.stolplusapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationErrorResponseDTO {
    private String field;
    private String message;
    private String rejectedValue;
}
