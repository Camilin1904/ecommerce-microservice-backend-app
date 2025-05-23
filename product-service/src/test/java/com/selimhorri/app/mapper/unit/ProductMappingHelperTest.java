package com.selimhorri.app.mapper.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.helper.ProductMappingHelper;

public class ProductMappingHelperTest {
    
    @Test
    @DisplayName("Test mapping from Product to ProductDto with category")
    void testMapFromProductToDtoWithCategory() {
        // Create category
        Category category = Category.builder()
                .categoryId(1)
                .categoryTitle("Test Category")
                .imageUrl("http://example.com/category.jpg")
                .build();
        
        // Create product with category
        Product product = Product.builder()
                .productId(1)
                .productTitle("Test Product")
                .imageUrl("http://example.com/product.jpg")
                .sku("TEST-SKU-001")
                .priceUnit(99.99)
                .quantity(10)
                .category(category)
                .build();
        
        // Map to DTO
        ProductDto result = ProductMappingHelper.map(product);
        
        // Assertions
        assertNotNull(result);
        assertEquals(1, result.getProductId());
        assertEquals("Test Product", result.getProductTitle());
        assertEquals("http://example.com/product.jpg", result.getImageUrl());
        assertEquals("TEST-SKU-001", result.getSku());
        assertEquals(99.99, result.getPriceUnit());
        assertEquals(10, result.getQuantity());
        
        // Check category mapping
        assertNotNull(result.getCategoryDto());
        assertEquals(1, result.getCategoryDto().getCategoryId());
        assertEquals("Test Category", result.getCategoryDto().getCategoryTitle());
        assertEquals("http://example.com/category.jpg", result.getCategoryDto().getImageUrl());
    }
    
    @Test
    @DisplayName("Test mapping from ProductDto to Product with category")
    void testMapFromDtoToProductWithCategory() {
        // Create category dto
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(3)
                .categoryTitle("DTO Category")
                .imageUrl("http://example.com/dto-category.jpg")
                .build();
        
        // Create product dto with category
        ProductDto productDto = ProductDto.builder()
                .productId(3)
                .productTitle("DTO Product")
                .imageUrl("http://example.com/dto-product.jpg")
                .sku("DTO-SKU-001")
                .priceUnit(199.99)
                .quantity(20)
                .categoryDto(categoryDto)
                .build();
        
        // Map to entity
        Product result = ProductMappingHelper.map(productDto);
        
        // Assertions
        assertNotNull(result);
        assertEquals(3, result.getProductId());
        assertEquals("DTO Product", result.getProductTitle());
        assertEquals("http://example.com/dto-product.jpg", result.getImageUrl());
        assertEquals("DTO-SKU-001", result.getSku());
        assertEquals(199.99, result.getPriceUnit());
        assertEquals(20, result.getQuantity());
        
        // Check category mapping
        assertNotNull(result.getCategory());
        assertEquals(3, result.getCategory().getCategoryId());
        assertEquals("DTO Category", result.getCategory().getCategoryTitle());
        assertEquals("http://example.com/dto-category.jpg", result.getCategory().getImageUrl());
    }


    @Test
    @DisplayName("Test bidirectional mapping preserves data integrity")
    void testBidirectionalMapping() {
        // Create original product
        Product originalProduct = Product.builder()
                .productId(5)
                .productTitle("Original Product")
                .imageUrl("http://example.com/original.jpg")
                .sku("ORIGINAL-SKU-001")
                .priceUnit(149.99)
                .quantity(25)
                .category(Category.builder().categoryId(1).build())
                .build();

        // Map to DTO and back to entity
        ProductDto dto = ProductMappingHelper.map(originalProduct);
        Product mappedProduct = ProductMappingHelper.map(dto);

        // Assertions - check all fields are preserved
        assertEquals(originalProduct.getProductId(), mappedProduct.getProductId());
        assertEquals(originalProduct.getProductTitle(), mappedProduct.getProductTitle());
        assertEquals(originalProduct.getImageUrl(), mappedProduct.getImageUrl());
        assertEquals(originalProduct.getSku(), mappedProduct.getSku());
        assertEquals(originalProduct.getPriceUnit(), mappedProduct.getPriceUnit());
        assertEquals(originalProduct.getQuantity(), mappedProduct.getQuantity());
    }
}
