package ru.enikeev.stolplusapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.enikeev.stolplusapp.dto.request.FavoriteRequestDTO;
import ru.enikeev.stolplusapp.dto.response.FavoriteResponseDTO;
import ru.enikeev.stolplusapp.dto.response.FavoriteWithFurnitureResponse;
import ru.enikeev.stolplusapp.dto.response.FurnitureShortResponseDTO;
import ru.enikeev.stolplusapp.exception.FurnitureNotFoundException;
import ru.enikeev.stolplusapp.exception.UserNotFoundException;
import ru.enikeev.stolplusapp.mapper.FavoriteMapper;;
import ru.enikeev.stolplusapp.mapper.FurnitureMapper;
import ru.enikeev.stolplusapp.model.Favorite;
import ru.enikeev.stolplusapp.model.Furniture;
import ru.enikeev.stolplusapp.model.User;
import ru.enikeev.stolplusapp.repository.FavoriteRepository;
import ru.enikeev.stolplusapp.repository.FurnitureRepository;
import ru.enikeev.stolplusapp.repository.UserRepository;

import java.lang.module.FindException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final FurnitureRepository furnitureRepository;
    private final FavoriteMapper favoriteMapper;
    private final FurnitureMapper furnitureMapper;

    /**
     * Добавление в избранное
     */
    @Transactional
    public FavoriteResponseDTO addToFavorite(FavoriteRequestDTO favoriteRequest, UUID userId) {
        log.info("Добавление мебели в избранное для пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        Furniture furniture = furnitureRepository.findById(favoriteRequest.getFurnitureId())
                .orElseThrow(() -> new FurnitureNotFoundException("Мебель с ID " + favoriteRequest.getFurnitureId() + " не найдена"));

        //проверяем не добавлено ли уже в избранное
        if (favoriteRepository.existsByUserAndFurniture(user, furniture)) {
            throw new IllegalArgumentException("Мебель уже добавлена в избранное");
        }

        Favorite favorite = favoriteMapper.toEntity(favoriteRequest);
        favorite.setUser(user);
        favorite.setFurniture(furniture);

        Favorite savedFavorite = favoriteRepository.save(favorite);
        log.info("Мебель добавлена в избранное с ID {}", savedFavorite.getId());

        return favoriteMapper.toResponseDTO(savedFavorite);
    }

    /**
     * Удаление из избранного
     */
    @Transactional
    public void removeFromFavorites(UUID furnitureId, UUID userId) {
        log.info("Удаление мебели из избранного для пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID {}" + userId + " не найден"));

        Furniture furniture = furnitureRepository.findById(furnitureId)
                .orElseThrow(() -> new FurnitureNotFoundException("Мебель с ID " + furnitureId + " не найдена"));

        favoriteRepository.deleteByUserAndFurniture(user, furniture);
        log.info("мебель удалена из избранного");
    }

    /**
     * Получение избранного пользователя
     */

    public List<FavoriteWithFurnitureResponse> getUserFavorite(UUID userId) {
        log.info("Получение избранного пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID" + userId + " не найден"));

        return favoriteRepository.findByUser(user).stream()
                .map(favoriteMapper::toResponseWithFurnitureDTO)
                .toList();
    }

    /**
     * Проверка наличия в избранном
     */
    public boolean isInFavorites(UUID furnitureId, UUID userId) {
        log.info("Проверка наличия мебели в избранном для пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        Furniture furniture = furnitureRepository.findById(furnitureId)
                .orElseThrow(() -> new FurnitureNotFoundException("Мебель с ID " + furnitureId + " не найден"));

        return favoriteRepository.existsByUserAndFurniture(user, furniture);
    }

    /**
     * Получение популярной мебели (по количеству добавлений в избранное)
     */

    public List<FurnitureShortResponseDTO> getPopularFurniture(){
        log.info("Получение популярной мебели");
        return favoriteRepository.findPopularFurniture().stream()
                .map(result ->{
                    Furniture furniture = (Furniture) result[0];
                    Long popularity = (Long) result[1];
                    FurnitureShortResponseDTO dto = furnitureMapper.toShortResponseDTO(furniture);
                    dto.setPopularity(popularity);
                    return dto;
                })
                .toList();
    }

    /**
     * Очистка всего избранного пользователя
     */

    @Transactional
    public void clearUserFavorites(UUID userId){
        log.info("Очистка всего избранного пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("Пользователя с ID " + userId + " не найден"));


        List<Favorite> favorites = favoriteRepository.findByUser(user);
        favoriteRepository.deleteAll(favorites);
        log.info("Избранное пользователя с ID {} полностью очищен", userId);
    }
}
