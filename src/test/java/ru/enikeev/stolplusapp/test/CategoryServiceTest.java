package ru.enikeev.stolplusapp.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.enikeev.stolplusapp.dto.response.CategoryResponseDTO;
import ru.enikeev.stolplusapp.mapper.CategoryMapper;
import ru.enikeev.stolplusapp.model.Category;
import ru.enikeev.stolplusapp.repository.CategoryRepository;
import ru.enikeev.stolplusapp.service.CategoryService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты CategoryService")
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;
    private CategoryResponseDTO dto1;
    private CategoryResponseDTO dto2;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setId(UUID.randomUUID());
        category1.setName("Электроника");
        category1.setDescription("Техника и гаджеты");

        category2 = new Category();
        category2.setId(UUID.randomUUID());
        category2.setName("Книги");
        category2.setDescription("Художественная литература");

        dto1 = new CategoryResponseDTO(UUID.randomUUID(), "Электроника", "Техника и гаджеты");
        dto2 = new CategoryResponseDTO(UUID.randomUUID(), "Книги", "Художественная литература");
    }

    @Test
    @DisplayName("Успешное получение всех категорий")
    void getAllCategories_ShouldReturnAllCategories_WhenCategoriesExist() {
        // Arrange
        List<Category> categories = Arrays.asList(category1, category2);
        List<CategoryResponseDTO> expectedDtos = Arrays.asList(dto1, dto2);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponseDTO(category1)).thenReturn(dto1);
        when(categoryMapper.toResponseDTO(category2)).thenReturn(dto2);

        // Act
        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);

        // Проверка вызовов
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toResponseDTO(category1);
        verify(categoryMapper, times(1)).toResponseDTO(category2);

        // Альтернативная проверка с AssertJ (более читаемо)
        assertThat(result)
                .hasSize(2)
                .containsExactly(dto1, dto2);
    }

}

