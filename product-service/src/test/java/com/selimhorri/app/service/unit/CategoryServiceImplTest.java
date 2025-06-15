package com.selimhorri.app.service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category1;
    private Category category2;
    private CategoryDto categoryDto1;
    private CategoryDto categoryDto2;
    private Category parentCategory;
    private CategoryDto parentCategoryDto;

    @BeforeEach
    void setUp() {
        parentCategory = Category.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/electronics.jpg")
                .build();

        parentCategoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/electronics.jpg")
                .build();

        category1 = Category.builder()
                .categoryId(2)
                .categoryTitle("Smartphones")
                .imageUrl("http://example.com/smartphones.jpg")
                .parentCategory(parentCategory)
                .build();

        category2 = Category.builder()
                .categoryId(3)
                .categoryTitle("Laptops")
                .imageUrl("http://example.com/laptops.jpg")
                .parentCategory(parentCategory)
                .build();

        categoryDto1 = CategoryDto.builder()
                .categoryId(2)
                .categoryTitle("Smartphones")
                .imageUrl("http://example.com/smartphones.jpg")
                .parentCategoryDto(parentCategoryDto)
                .build();

        categoryDto2 = CategoryDto.builder()
                .categoryId(3)
                .categoryTitle("Laptops")
                .imageUrl("http://example.com/laptops.jpg")
                .parentCategoryDto(parentCategoryDto)
                .build();
    }

    @Test
    @DisplayName("Test findAll returns list of categories")
    void testFindAll() {
        // Given
        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // When
        List<CategoryDto> result = categoryService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Smartphones", result.get(0).getCategoryTitle());
        assertEquals("Laptops", result.get(1).getCategoryTitle());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findAll returns empty list when no categories exist")
    void testFindAllEmpty() {
        // Given
        when(categoryRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<CategoryDto> result = categoryService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findById returns category when exists")
    void testFindByIdExists() {
        // Given
        when(categoryRepository.findById(2)).thenReturn(Optional.of(category1));

        // When
        CategoryDto result = categoryService.findById(2);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getCategoryId());
        assertEquals("Smartphones", result.getCategoryTitle());
        assertEquals("http://example.com/smartphones.jpg", result.getImageUrl());
        assertNotNull(result.getParentCategoryDto());
        assertEquals(1, result.getParentCategoryDto().getCategoryId());
        verify(categoryRepository, times(1)).findById(2);
    }

    @Test
    @DisplayName("Test findById throws exception when category not found")
    void testFindByIdNotFound() {
        // Given
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, 
                () -> categoryService.findById(999));
        assertEquals("Category with id: 999 not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Test save creates new category")
    void testSave() {
        // Given
        CategoryDto newCategoryDto = CategoryDto.builder()
                .categoryTitle("New Category")
                .imageUrl("http://example.com/new.jpg")
                .parentCategoryDto(parentCategoryDto)
                .build();

        Category savedCategory = Category.builder()
                .categoryId(4)
                .categoryTitle("New Category")
                .imageUrl("http://example.com/new.jpg")
                .parentCategory(parentCategory)
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // When
        CategoryDto result = categoryService.save(newCategoryDto);

        // Then
        assertNotNull(result);
        assertEquals(4, result.getCategoryId());
        assertEquals("New Category", result.getCategoryTitle());
        assertEquals("http://example.com/new.jpg", result.getImageUrl());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Test update modifies existing category")
    void testUpdate() {
        // Given
        CategoryDto updatedDto = CategoryDto.builder()
                .categoryId(2)
                .categoryTitle("Updated Smartphones")
                .imageUrl("http://example.com/updated.jpg")
                .parentCategoryDto(parentCategoryDto)
                .build();

        Category updatedCategory = Category.builder()
                .categoryId(2)
                .categoryTitle("Updated Smartphones")
                .imageUrl("http://example.com/updated.jpg")
                .parentCategory(parentCategory)
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // When
        CategoryDto result = categoryService.update(updatedDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Smartphones", result.getCategoryTitle());
        assertEquals("http://example.com/updated.jpg", result.getImageUrl());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Test update with ID finds and updates category")
    void testUpdateWithId() {
        // Given
        when(categoryRepository.findById(2)).thenReturn(Optional.of(category1));
        when(categoryRepository.save(any(Category.class))).thenReturn(category1);

        // When
        CategoryDto result = categoryService.update(2, categoryDto1);

        // Then
        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(2);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Test deleteById removes category")
    void testDeleteById() {
        // Given
        doNothing().when(categoryRepository).deleteById(2);

        // When
        categoryService.deleteById(2);

        // Then
        verify(categoryRepository, times(1)).deleteById(2);
    }

    @Test
    @DisplayName("Test save root category without parent")
    void testSaveRootCategory() {
        // Given
        CategoryDto rootCategoryDto = CategoryDto.builder()
                .categoryTitle("Root Category")
                .imageUrl("http://example.com/root.jpg")
                .build();

        Category savedRootCategory = Category.builder()
                .categoryId(5)
                .categoryTitle("Root Category")
                .imageUrl("http://example.com/root.jpg")
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedRootCategory);

        // When
        CategoryDto result = categoryService.save(rootCategoryDto);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getCategoryId());
        assertEquals("Root Category", result.getCategoryTitle());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}
