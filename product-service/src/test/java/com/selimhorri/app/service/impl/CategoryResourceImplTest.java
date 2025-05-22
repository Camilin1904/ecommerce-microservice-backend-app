package com.selimhorri.app.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.selimhorri.app.dto.CategoryDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CategoryResourceImplTest {

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

        CategoryDto response = restTemplate.postForObject(url, categoryDto, CategoryDto.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getCategoryId(), "Category ID should not be null");
        assertEquals(categoryDto.getCategoryTitle(), response.getCategoryTitle(), "Category title should match");
    }
}