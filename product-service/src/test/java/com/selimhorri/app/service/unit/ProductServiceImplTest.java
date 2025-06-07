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
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;
    private ProductDto productDto1;
    private ProductDto productDto2;
    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/electronics.jpg")
                .build();

        categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/electronics.jpg")
                .build();

        product1 = Product.builder()
                .productId(1)
                .productTitle("iPhone 13")
                .imageUrl("http://example.com/iphone13.jpg")
                .sku("IPH-13-001")
                .priceUnit(999.99)
                .quantity(50)
                .category(category)
                .build();

        product2 = Product.builder()
                .productId(2)
                .productTitle("Samsung Galaxy")
                .imageUrl("http://example.com/galaxy.jpg")
                .sku("SAM-GAL-001")
                .priceUnit(799.99)
                .quantity(30)
                .category(category)
                .build();

        productDto1 = ProductDto.builder()
                .productId(1)
                .productTitle("iPhone 13")
                .imageUrl("http://example.com/iphone13.jpg")
                .sku("IPH-13-001")
                .priceUnit(999.99)
                .quantity(50)
                .categoryDto(categoryDto)
                .build();

        productDto2 = ProductDto.builder()
                .productId(2)
                .productTitle("Samsung Galaxy")
                .imageUrl("http://example.com/galaxy.jpg")
                .sku("SAM-GAL-001")
                .priceUnit(799.99)
                .quantity(30)
                .categoryDto(categoryDto)
                .build();
    }

    @Test
    @DisplayName("Test findAll returns list of products")
    void testFindAll() {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        // When
        List<ProductDto> result = productService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("iPhone 13", result.get(0).getProductTitle());
        assertEquals("Samsung Galaxy", result.get(1).getProductTitle());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findAll returns empty list when no products exist")
    void testFindAllEmpty() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<ProductDto> result = productService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findById returns product when exists")
    void testFindByIdExists() {
        // Given
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));

        // When
        ProductDto result = productService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getProductId());
        assertEquals("iPhone 13", result.getProductTitle());
        assertEquals("IPH-13-001", result.getSku());
        assertEquals(999.99, result.getPriceUnit());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Test findById throws exception when product not found")
    void testFindByIdNotFound() {
        // Given
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, 
                () -> productService.findById(999));
        assertEquals("Product with id: 999 not found", exception.getMessage());
        verify(productRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Test save creates new product")
    void testSave() {
        // Given
        ProductDto newProductDto = ProductDto.builder()
                .productTitle("New Product")
                .imageUrl("http://example.com/new.jpg")
                .sku("NEW-001")
                .priceUnit(199.99)
                .quantity(10)
                .categoryDto(categoryDto)
                .build();

        Product newProduct = Product.builder()
                .productTitle("New Product")
                .imageUrl("http://example.com/new.jpg")
                .sku("NEW-001")
                .priceUnit(199.99)
                .quantity(10)
                .category(category)
                .build();

        Product savedProduct = Product.builder()
                .productId(3)
                .productTitle("New Product")
                .imageUrl("http://example.com/new.jpg")
                .sku("NEW-001")
                .priceUnit(199.99)
                .quantity(10)
                .category(category)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductDto result = productService.save(newProductDto);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getProductId());
        assertEquals("New Product", result.getProductTitle());
        assertEquals("NEW-001", result.getSku());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Test update modifies existing product")
    void testUpdate() {
        // Given
        ProductDto updatedDto = ProductDto.builder()
                .productId(1)
                .productTitle("Updated iPhone")
                .imageUrl("http://example.com/updated.jpg")
                .sku("IPH-13-UPD")
                .priceUnit(1099.99)
                .quantity(40)
                .categoryDto(categoryDto)
                .build();

        Product updatedProduct = Product.builder()
                .productId(1)
                .productTitle("Updated iPhone")
                .imageUrl("http://example.com/updated.jpg")
                .sku("IPH-13-UPD")
                .priceUnit(1099.99)
                .quantity(40)
                .category(category)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // When
        ProductDto result = productService.update(updatedDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated iPhone", result.getProductTitle());
        assertEquals("IPH-13-UPD", result.getSku());
        assertEquals(1099.99, result.getPriceUnit());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Test update with ID finds and updates product")
    void testUpdateWithId() {
        // Given
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // When
        ProductDto result = productService.update(1, productDto1);

        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Test deleteById removes product")
    void testDeleteById() {
        // Given
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        doNothing().when(productRepository).delete(any(Product.class));

        // When
        productService.deleteById(1);

        // Then
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).delete(any(Product.class));
    }

    @Test
    @DisplayName("Test deleteById throws exception when product not found")
    void testDeleteByIdNotFound() {
        // Given
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteById(999));
        verify(productRepository, times(1)).findById(999);
        verify(productRepository, never()).delete(any(Product.class));
    }
}
