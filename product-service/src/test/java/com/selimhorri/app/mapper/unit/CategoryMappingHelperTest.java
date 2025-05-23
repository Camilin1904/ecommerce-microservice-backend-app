package com.selimhorri.app.mapper.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.helper.CategoryMappingHelper;

public class CategoryMappingHelperTest {
    
    @Test
    @DisplayName("Test mapping from Category to CategoryDto with parent category")
    void testMapFromCategoryToDtoWithParent() {
        // Create parent category
        Category parentCategory = Category.builder()
                .categoryId(1)
                .categoryTitle("Parent Category")
                .imageUrl("http://example.com/parent.jpg")
                .build();
        
        // Create child category with parent
        Category category = Category.builder()
                .categoryId(2)
                .categoryTitle("Child Category")
                .imageUrl("http://example.com/child.jpg")
                .parentCategory(parentCategory)
                .build();
        
        // Map to DTO
        CategoryDto result = CategoryMappingHelper.map(category);
        
        // Assertions
        assertNotNull(result);
        assertEquals(2, result.getCategoryId());
        assertEquals("Child Category", result.getCategoryTitle());
        assertEquals("http://example.com/child.jpg", result.getImageUrl());
        
        // Check parent mapping
        assertNotNull(result.getParentCategoryDto());
        assertEquals(1, result.getParentCategoryDto().getCategoryId());
        assertEquals("Parent Category", result.getParentCategoryDto().getCategoryTitle());
        assertEquals("http://example.com/parent.jpg", result.getParentCategoryDto().getImageUrl());
    }
    
    @Test
    @DisplayName("Test mapping from Category to CategoryDto with null parent category")
    void testMapFromCategoryToDtoWithNullParent() {
        // Create category without parent
        Category category = Category.builder()
                .categoryId(3)
                .categoryTitle("Root Category")
                .imageUrl("http://example.com/root.jpg")
                .parentCategory(null)
                .build();
        
        // Map to DTO
        CategoryDto result = CategoryMappingHelper.map(category);
        
        // Assertions
        assertNotNull(result);
        assertEquals(3, result.getCategoryId());
        assertEquals("Root Category", result.getCategoryTitle());
        assertEquals("http://example.com/root.jpg", result.getImageUrl());
        
        // Check parent mapping - should have empty DTO
        assertNotNull(result.getParentCategoryDto());
        assertNull(result.getParentCategoryDto().getCategoryId());
        assertNull(result.getParentCategoryDto().getCategoryTitle());
        assertNull(result.getParentCategoryDto().getImageUrl());
    }
    
    @Test
    @DisplayName("Test mapping from CategoryDto to Category with parent category")
    void testMapFromDtoToCategoryWithParent() {
        // Create parent dto
        CategoryDto parentDto = CategoryDto.builder()
                .categoryId(4)
                .categoryTitle("Parent DTO")
                .imageUrl("http://example.com/parent-dto.jpg")
                .build();
        
        // Create child dto with parent
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(5)
                .categoryTitle("Child DTO")
                .imageUrl("http://example.com/child-dto.jpg")
                .parentCategoryDto(parentDto)
                .build();
        
        // Map to entity
        Category result = CategoryMappingHelper.map(categoryDto);
        
        // Assertions
        assertNotNull(result);
        assertEquals(5, result.getCategoryId());
        assertEquals("Child DTO", result.getCategoryTitle());
        assertEquals("http://example.com/child-dto.jpg", result.getImageUrl());
        
        // Check parent mapping
        assertNotNull(result.getParentCategory());
        assertEquals(4, result.getParentCategory().getCategoryId());
        assertEquals("Parent DTO", result.getParentCategory().getCategoryTitle());
        assertEquals("http://example.com/parent-dto.jpg", result.getParentCategory().getImageUrl());
    }
    
    @Test
    @DisplayName("Test mapping from CategoryDto to Category with null parent category")
    void testMapFromDtoToCategoryWithNullParent() {
        // Create dto without parent
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(6)
                .categoryTitle("Root DTO")
                .imageUrl("http://example.com/root-dto.jpg")
                .parentCategoryDto(null)
                .build();
        
        // Map to entity
        Category result = CategoryMappingHelper.map(categoryDto);
        
        // Assertions
        assertNotNull(result);
        assertEquals(6, result.getCategoryId());
        assertEquals("Root DTO", result.getCategoryTitle());
        assertEquals("http://example.com/root-dto.jpg", result.getImageUrl());
        
        // Check parent mapping - should have empty entity
        assertNotNull(result.getParentCategory());
        assertNull(result.getParentCategory().getCategoryId());
        assertNull(result.getParentCategory().getCategoryTitle());
        assertNull(result.getParentCategory().getImageUrl());
    }
    
    @Test
    @DisplayName("Test bidirectional mapping preserves data integrity")
    void testBidirectionalMapping() {
        // Create original category
        Category originalCategory = Category.builder()
                .categoryId(7)
                .categoryTitle("Original Category")
                .imageUrl("http://example.com/original.jpg")
                .build();
        
        // Map to DTO and back to entity
        CategoryDto dto = CategoryMappingHelper.map(originalCategory);
        Category mappedCategory = CategoryMappingHelper.map(dto);
        
        // Assertions
        assertEquals(originalCategory.getCategoryId(), mappedCategory.getCategoryId());
        assertEquals(originalCategory.getCategoryTitle(), mappedCategory.getCategoryTitle());
        assertEquals(originalCategory.getImageUrl(), mappedCategory.getImageUrl());
    }
}
