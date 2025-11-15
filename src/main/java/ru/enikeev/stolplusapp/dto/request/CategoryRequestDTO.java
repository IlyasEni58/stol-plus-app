package ru.enikeev.stolplusapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {

    @NotBlank(message = "Название категории обязательно")
    @Size(max = 100, message = "Название категории не должно превышать 100 символов")
    private String name;        // "Столы", "Стулья"

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;
}
