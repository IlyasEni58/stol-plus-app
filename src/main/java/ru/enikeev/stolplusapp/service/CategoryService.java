package ru.enikeev.stolplusapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.enikeev.stolplusapp.dto.request.CategoryRequestDTO;
import ru.enikeev.stolplusapp.dto.response.CategoryResponseDTO;
import ru.enikeev.stolplusapp.dto.response.FurnitureShortResponseDTO;
import ru.enikeev.stolplusapp.exception.CategoryNotFoundException;
import ru.enikeev.stolplusapp.mapper.CategoryMapper;
import ru.enikeev.stolplusapp.mapper.FurnitureMapper;
import ru.enikeev.stolplusapp.model.Category;
import ru.enikeev.stolplusapp.repository.CategoryRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FurnitureMapper furnitureMapper;

    /**
     * Получение всех категорий
     */
    public List<CategoryResponseDTO> getAllCategories() {
        log.info("Получение всех категорий");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponseDTO)
                .toList();
    }

    /**
     * Получение категории по ID
     */
    public CategoryResponseDTO getCategoryById(UUID id) {
        log.info("Получение категории по ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Категория с ID " + id + " не найдена"));
        return categoryMapper.toResponseDTO(category);
    }

    /**
     * Создание новой категории
     */
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequest) {
        log.info("Создание новой категории: {}", categoryRequest);

        //Проверять уникальность названия
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new IllegalArgumentException("Категория с названием '" + categoryRequest.getName() + "' уже существует");
        }
        Category category = categoryMapper.toEntity(categoryRequest);
        Category savedCategory = categoryRepository.save(category);
        log.info("Категория успешно создана с ID: {}", savedCategory.getId());

        return categoryMapper.toResponseDTO(savedCategory);
    }
    /**
     * Обновление категории
     */
    @Transactional
    public CategoryResponseDTO updateCategory(UUID id, CategoryRequestDTO categoryRequest){
        log.info("Обновление категории с ID: {}", id);

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(()-> new CategoryNotFoundException("Категория с ID " + id + " не найдена"));

        //проверяем уникальность названия (если оно изменилось)
        if(!existingCategory.getName().equals(categoryRequest.getName()) &&
        categoryRepository.existsByName(categoryRequest.getName())){
            throw new IllegalArgumentException("Категория с названием " + categoryRequest.getName() + " уже существует");
        }
        categoryMapper.updateCategoryFromDTO(categoryRequest, existingCategory);
        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Категория с ID {} успешно обновлена", id);

        return categoryMapper.toResponseDTO(updatedCategory);
    }

    /**
     * Удаление категории
     */
    @Transactional
    public void deleteCategory(UUID id){
        log.info("Удаление категории с ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new CategoryNotFoundException("Категория с ID " + id + " не найдена"));

        // проверяем что в категории нет мебели
        if(!category.getFurniture().isEmpty()){
            throw new IllegalStateException("Невозможно удалить категорию, так как в ней есть мебель");
        }
        categoryRepository.delete(category);
        log.info("Категория с ID {} успешно удалена", id);

    }

    /**
     * Поиск категории по названию
     */
    public CategoryResponseDTO getCategoryByName(String name){
        log.info("Поиск категории по названию: {}", name);
        Category category = categoryRepository.findByName(name)
                .orElseThrow(()-> new CategoryNotFoundException("Категория с названием '" + name + "' не найдена"));
        return categoryMapper.toResponseDTO(category);
    }

    /**
     * Получение мебели в категории
     */
    public List<FurnitureShortResponseDTO> getFurnitureInCategory(UUID categoryId){
        log.info("Получение мебели в категории с ID: {}",categoryId);

        //проверяем что категория существует
        if(!categoryRepository.existsById(categoryId)){
            throw new CategoryNotFoundException("Категория с ID " + categoryId + " не найдена");
        }
        return categoryRepository.findFurnitureByCategoryID(categoryId).stream()
                .map(furnitureMapper::toShortResponseDTO)
                .toList();
    }

}

