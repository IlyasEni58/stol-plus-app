package ru.enikeev.stolplusapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteWithFurnitureResponse {
    private UUID id;
    private FurnitureResponseDTO furniture;
    private LocalDateTime addedAt;
}
