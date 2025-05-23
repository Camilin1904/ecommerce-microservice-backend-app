package com.selimhorri.app.resource.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.selimhorri.app.dto.CategoryDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CategoryResourceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                System.err.println("Response error: " + body);
                super.handleError(response);
            }
        });
    }

    @Test
    void testFindAll() {
        String url = "http://localhost:" + port + "/product-service/api/categories";
        DtoCollectionResponse<LinkedHashMap> response = restTemplate.getForObject(url, DtoCollectionResponse.class);

        assertNotNull(response, "Response should not be null");
        assertFalse(response.getCollection().isEmpty(), "Response should contain at least one category");
        assert response.getCollection().size() == 7;
    }

    @Test
    void testFindById() {
        String url = "http://localhost:" + port + "/product-service/api/categories/4";
        CategoryDto response = restTemplate.getForObject(url, CategoryDto.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getCategoryId(), "Category ID should not be null");
        assertNotNull(response.getCategoryTitle(), "Category title should not be null");
        assertNotNull(response.getImageUrl(), "Image URL should not be null");

    }

    @Test
    void testSave() {
        String url = "http://localhost:" + port + "/product-service/api/categories";
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryTitle("Test Category");
        categoryDto.setImageUrl("http://example.com/image.jpg");
        categoryDto.setParentCategoryDto(CategoryDto.builder().categoryId(1).build()); // Assuming category ID 1 exists

        CategoryDto response = restTemplate.postForObject(url, categoryDto, CategoryDto.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getCategoryId(), "Category ID should not be null");
        assertEquals(categoryDto.getCategoryTitle(), response.getCategoryTitle(), "Category title should match");
    }

    @Test
    void testUpdate() {
        String url = "http://localhost:" + port + "/product-service/api/categories";
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryId(4);
        categoryDto.setCategoryTitle("Updated Category");
        categoryDto.setImageUrl("http://example.com/updated_image.jpg");
        categoryDto.setParentCategoryDto(CategoryDto.builder().categoryId(1).build());

        CategoryDto response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(categoryDto), CategoryDto.class).getBody();

        assertNotNull(response, "Response should not be null");
        assertEquals(categoryDto.getCategoryTitle(), response.getCategoryTitle(), "Category title should match");
    }

    @Test
    void testDelete() {
        String url = "http://localhost:" + port + "/product-service/api/categories/2"; // Assuming category ID 1 exists
        restTemplate.delete(url);

        // Verify that the category was deleted
        DtoCollectionResponse<LinkedHashMap> response = restTemplate.getForObject("http://localhost:" + port + "/product-service/api/categories", DtoCollectionResponse.class);
        assertNotNull(response, "Response should not be null");
        assertFalse(response.getCollection().stream().anyMatch(category -> category.get("categoryId").equals(2)), "Category should be deleted");
    }
}