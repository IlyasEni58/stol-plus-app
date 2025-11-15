package ru.enikeev.stolplusapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.enikeev.stolplusapp.dto.request.FurnitureRequestDTO;
import ru.enikeev.stolplusapp.dto.request.FurnitureUpdateRequestDTO;
import ru.enikeev.stolplusapp.dto.response.FurnitureResponseDTO;
import ru.enikeev.stolplusapp.dto.response.FurnitureShortResponseDTO;
import ru.enikeev.stolplusapp.exception.FurnitureNotFoundException;
import ru.enikeev.stolplusapp.mapper.FurnitureMapper;
import ru.enikeev.stolplusapp.model.Category;
import ru.enikeev.stolplusapp.model.Furniture;
import ru.enikeev.stolplusapp.model.enums.Material;
import ru.enikeev.stolplusapp.repository.CategoryRepository;
import ru.enikeev.stolplusapp.repository.FurnitureRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FurnitureService {

    private final FurnitureRepository furnitureRepository;
    private final CategoryRepository categoryRepository;
    private final FurnitureMapper furnitureMapper;

    /**
     * Получение всей мебели
     */

    public List<FurnitureShortResponseDTO> getAllFurniture() {
        log.info("Получение всего каталога мебели");
        return furnitureRepository.findAll().stream().map(furnitureMapper::toShortResponseDTO).toList();
    }

    /**
     * Получение мебели по ID
     */
    public FurnitureResponseDTO getFurnitureById(UUID id) {
        log.info("Получение мебели по ID: {}", id);

        Furniture furniture = furnitureRepository.findById(id).orElseThrow(() -> new FurnitureNotFoundException("Мебель с ID " + id + " не найдена"));
        return furnitureMapper.toResponseDTO(furniture);
    }

    /**
     * Создание новой мебели
     */
    @Transactional
    public FurnitureResponseDTO createFurniture(FurnitureRequestDTO furnitureRequest) {
        log.info("Создание новой мебели {}", furnitureRequest.getName());

        //проверяем существование категории
        Set<Category> categories = categoryRepository.findAllById(furnitureRequest.getCategoryIds()).stream().collect(Collectors.toSet());

        if (categories.size() != furnitureRequest.getCategoryIds().size()) {
            throw new IllegalArgumentException("Некоторые категории не найдены");
        }

        Furniture furniture = furnitureMapper.toEntity(furnitureRequest);
        furniture.setCategories(categories);

        Furniture savedFurniture = furnitureRepository.save(furniture);
        log.info("Мебель успешно создана по ID: {}", savedFurniture.getId());

        return furnitureMapper.toResponseDTO(savedFurniture);
    }

    /**
     * Обновление мебели
     */
    @Transactional
    public FurnitureResponseDTO updateFurniture(UUID id, FurnitureUpdateRequestDTO updateRequest) {
        log.info("Обновление мебели с ID: {}", id);

        Furniture existingFurniture = furnitureRepository.findById(id).orElseThrow(() -> new FurnitureNotFoundException("мебель с ID " + id + " не найдена"));

        //обновляем поля
        furnitureMapper.updateFurnitureFromDTO(updateRequest, existingFurniture);

        //Если обновляются категории
        if (updateRequest.getCategoryIds() != null && !updateRequest.getCategoryIds().isEmpty()) {
            Set<Category> categories = categoryRepository.findAllById(updateRequest.getCategoryIds()).stream().collect(Collectors.toSet());
            existingFurniture.setCategories(categories);
        }

        Furniture updateFurniture = furnitureRepository.save(existingFurniture);
        log.info("Мебель с ID {} успешно обновлена", id);

        return furnitureMapper.toResponseDTO(updateFurniture);
    }

    /**
     * Удаление мебели
     */

    @Transactional
    public void deleteFurniture(UUID id) {
        log.info("Удаление мебели с ID: {}", id);
        if (!furnitureRepository.existsById(id)) {
            throw new FurnitureNotFoundException("Мебель с ID " + id + " не найдена");
        }

        furnitureRepository.deleteById(id);
        log.info("Мебель с ID {} успешно удалена", id);
    }

    /**
     * Поиск мебели по названию
     */
    public List<FurnitureShortResponseDTO> searchFurnitureByName(String name) {
        log.info("Поиск мебели по названию: {}", name);

        return furnitureRepository.findByNameContainingIgnoreCase(name).stream().map(furnitureMapper::toShortResponseDTO).toList();
    }

    /**
     * Поиск мебели по материалу
     */
    public List<FurnitureShortResponseDTO> getFurnitureByMaterial(Material material) {
        log.info("Поиск мебели по материалу: {}", material);
        return furnitureRepository.findByMaterial(material).stream().map(furnitureMapper::toShortResponseDTO).toList();
    }

    /**
     * Поиск мебели по категории
     */
    public List<FurnitureShortResponseDTO> getFurnitureByCategory(UUID categoryId) {
        log.info("Поиск мебели по категории с ID: {}", categoryId);
        return furnitureRepository.findByCategoryId(categoryId).stream().map(furnitureMapper::toShortResponseDTO).toList();
    }

    /**
     * Поиск мебели в ценовом диапазоне
     */
    public List<FurnitureShortResponseDTO> getFurnitureByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Поиск мебели в ценовом диапазоне: {} - {}", maxPrice, minPrice);

        return furnitureRepository.findByPriceBetween(minPrice, maxPrice).stream().map(furnitureMapper::toShortResponseDTO).toList();

    }

    /**
     * Сортировка мебели по цене (по возрастанию)
     */
    public List<FurnitureShortResponseDTO> getFurnitureSortedByPriceAsc() {
        log.info("Сортировка мебели по цене (возрастание)");

        return furnitureRepository.findAllByOrderByPriceAsc().stream().map(furnitureMapper::toShortResponseDTO).toList();
    }

    /**
     * Сортировка мебели по цене (по убыванию)
     */
    public List<FurnitureShortResponseDTO> getFurnitureSortedByPriceDesc() {
        log.info("Сортировка мебели по цене (убывание)");

        return furnitureRepository.findAllByOrderByPriceDesc().stream().map(furnitureMapper::toShortResponseDTO).toList();
    }

    /**
     * Получение популярной мебели (на основе избранного)
     */
    public List<FurnitureShortResponseDTO> getPopularFurniture() {
        log.info("Получение популярной мебели");
        // Здесь можно добавить логику для получения популярной мебели
        // Пока вернем всю мебель отсортированную по цене (как заглушка)
        return furnitureRepository.findAllByOrderByPriceDesc().stream().map(furnitureMapper::toShortResponseDTO).toList();
    }
}
