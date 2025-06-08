package com.selimhorri.app.resource.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;


import com.selimhorri.app.domain.id.OrderItemId;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.OrderItemDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrderItemResourceIntegrationTest {

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
    @DisplayName("Test findById returns correct order item")
    void testFindById() {
        // Arrange
        String url = "http://localhost:" + port + "/shipping-service/api/shippings/1/1";
        
        // Act
        ResponseEntity<OrderItemDto> response = restTemplate.getForEntity(url, OrderItemDto.class);
        
        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response should be successful");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(1, response.getBody().getProductId(), "Product ID should match");
        assertEquals(1, response.getBody().getOrderId(), "Order ID should match");
        assertNotNull(response.getBody().getOrderedQuantity(), "Ordered quantity should not be null");
    }
    
    
    @Test
    @DisplayName("Test save creates new order item")
    void testSave() {
        // Arrange
        String url = "http://localhost:" + port + "/shipping-service/api/shippings";
        OrderItemDto orderItemDto = OrderItemDto.builder()
                .productId(10)
                .orderId(10)
                .orderedQuantity(3)
                .productDto(ProductDto.builder().productId(10).build())
                .orderDto(OrderDto.builder().orderId(10).build())
                .build();
        
        // Act
        ResponseEntity<OrderItemDto> response = restTemplate.postForEntity(url, orderItemDto, OrderItemDto.class);
        
        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response should be successful");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(10, response.getBody().getProductId(), "Product ID should match");
        assertEquals(10, response.getBody().getOrderId(), "Order ID should match");
        assertEquals(3, response.getBody().getOrderedQuantity(), "Ordered quantity should match");
    }
    
    @Test
    @DisplayName("Test update modifies existing order item")
    void testUpdate() {
        // Arrange
        String url = "http://localhost:" + port + "/shipping-service/api/shippings";
        
        // First get the existing item
        ResponseEntity<OrderItemDto> getResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/shipping-service/api/shippings/1/1", 
                OrderItemDto.class
        );
        
        OrderItemDto existingItem = getResponse.getBody();
        assertNotNull(existingItem, "Existing item should not be null");
        
        // Modify the item
        Integer originalQuantity = existingItem.getOrderedQuantity();
        Integer newQuantity = originalQuantity + 5;
        existingItem.setOrderedQuantity(newQuantity);
        
        // Act
        ResponseEntity<OrderItemDto> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(existingItem),
                OrderItemDto.class
        );
        
        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response should be successful");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(newQuantity, response.getBody().getOrderedQuantity(), "Ordered quantity should be updated");
        
        // Verify by getting the item again
        ResponseEntity<OrderItemDto> verifyResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/shipping-service/api/shippings/1/1", 
                OrderItemDto.class
        );
        
        assertNotNull(verifyResponse.getBody(), "Verified item should not be null");
        assertEquals(newQuantity, verifyResponse.getBody().getOrderedQuantity(), "Verified quantity should match updated value");
    }
    
    @Test
    @DisplayName("Test deleteById with path variables removes the order item")
    void testDeleteByIdWithPathVariables() {
        // Arrange
        String url = "http://localhost:" + port + "/shipping-service/api/shippings/2/1";
        
        // Act
        restTemplate.delete(url);
        
        // Assert - Verify it's deleted by trying to get it, which should return an error
        try {
            restTemplate.getForEntity(url, OrderItemDto.class);
            fail("Should have thrown an exception for deleted item");
        } catch (Exception e) {
            // Expected behavior
            assertTrue(true, "Exception should be thrown for deleted item");
        }
    }
    
    
}
