package com.selimhorri.app.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;

import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.netflix.discovery.converters.Auto;
import com.selimhorri.app.dto.CategoryDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CategoryResourceImplTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    
    @BeforeEach
    void setUp() {
        restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                String body = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
                System.err.println("Response error: " + body);
                super.handleError(response);
            }
        });   // Set up any necessary data or configurations before each test
    }

    @Test
    public void testFindAll() {
        // Perform the GET request to fetch all categories
        String url = "http://localhost:" + port + "/api/categories";
        CategoryDto[] response = restTemplate.getForObject(url, CategoryDto[].class);

        // Assert the response
        assertNotNull(response);
        assertTrue(response.length > 0);
        for (CategoryDto category : response) {
            assertNotNull(category.getCategoryId());
            assertNotNull(category.getCategoryTitle());
            assertNotNull(category.getImageUrl());
            assertNotNull(category.getSubCategoriesDtos());
            assertNotNull(category.getProductDtos());
        }
    }

    @Test
    public void testFindById() {
        // Perform the GET request to fetch a category by ID
        String url = "http://localhost:" + port + "/api/categories/1";
        CategoryDto response = restTemplate.getForObject(url, CategoryDto.class);

        // Assert the response
        assertNotNull(response);
        assertNotNull(response.getCategoryId());
        assertNotNull(response.getCategoryTitle());
        assertNotNull(response.getImageUrl());
        assertNotNull(response.getSubCategoriesDtos());
        assertNotNull(response.getProductDtos());
        
    }
}

