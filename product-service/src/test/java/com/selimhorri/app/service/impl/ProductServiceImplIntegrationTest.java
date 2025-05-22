package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.ProductService;

@SpringBootTest
@ActiveProfiles("test")
public class ProductServiceImplIntegrationTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private ProductDto productDto1;
    private ProductDto productDto2;
    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        // Set up category
        category = new Category();
        category.setCategoryId(1);
        category.setCategoryTitle("Electronics");

        categoryDto = new CategoryDto();
        categoryDto.setCategoryId(1);
        categoryDto.setCategoryTitle("Electronics");

        // Set up products
        product1 = new Product();
        product1.setProductId(1);
        product1.setProductTitle("Laptop");
        product1.setPriceUnit(999.99);
        product1.setImageUrl("https://example.com/laptop.jpg");
        product1.setCategory(category);

        product2 = new Product();
        product2.setProductId(2);
        product2.setProductTitle("Smartphone");
        product2.setPriceUnit(599.99);
        product2.setImageUrl("https://example.com/smartphone.jpg");
        product2.setCategory(category);

        // Set up product DTOs
        productDto1 = new ProductDto();
        productDto1.setProductId(1);
        productDto1.setProductTitle("Laptop");
        productDto1.setPriceUnit(999.99);
        productDto1.setImageUrl("https://example.com/laptop.jpg");
        productDto1.setCategoryDto(categoryDto);

        productDto2 = new ProductDto();
        productDto2.setProductId(2);
        productDto2.setProductTitle("Smartphone");
        productDto2.setPriceUnit(599.99);
        productDto2.setImageUrl("https://example.com/smartphone.jpg");
        productDto2.setCategoryDto(categoryDto);

        // Configure repository mock behavior
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2)).thenReturn(Optional.of(product2));
        when(productRepository.findById(99)).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void testFindAll() {
        List<ProductDto> products = productService.findAll();
        
        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getProductTitle());
        assertEquals("Smartphone", products.get(1).getProductTitle());
        
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testFindById_ExistingId() {
        ProductDto result = productService.findById(1);
        
        assertNotNull(result);
        assertEquals("Laptop", result.getProductTitle());
        assertEquals(999.99, result.getPriceUnit());
        
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_NonExistingId() {
        assertThrows(ProductNotFoundException.class, () -> productService.findById(99));
        verify(productRepository, times(1)).findById(99);
    }

    @Test
    void testSave() {
        ProductDto newProductDto = new ProductDto();
        newProductDto.setProductTitle("Tablet");
        newProductDto.setPriceUnit(399.99);
        newProductDto.setImageUrl("https://example.com/tablet.jpg");
        newProductDto.setCategoryDto(categoryDto);
        
        ProductDto savedProduct = productService.save(newProductDto);
        
        assertNotNull(savedProduct);
        assertEquals("Tablet", savedProduct.getProductTitle());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdate() {
        productDto1.setPriceUnit(899.99);
        
        ProductDto updatedProduct = productService.update(productDto1);
        
        assertNotNull(updatedProduct);
        assertEquals(899.99, updatedProduct.getPriceUnit());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateWithId() {
        ProductDto updatedProductDto = new ProductDto();
        updatedProductDto.setProductTitle("Updated Laptop");
        updatedProductDto.setPriceUnit(1099.99);
        
        ProductDto result = productService.update(1, updatedProductDto);
        
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteById() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        doNothing().when(productRepository).delete(any(Product.class));
        
        productService.deleteById(1);
        
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).delete(any(Product.class));
    }
}
