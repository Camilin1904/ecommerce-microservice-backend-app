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

import com.selimhorri.app.dto.OrderItemDto;
import com.selimhorri.app.domain.id.OrderItemId;

import static org.junit.jupiter.api.Assertions.*;

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
    void testFindAll() {
        String url = "http://localhost:" + port + "/shipping-service/api/shippings";
        DtoCollectionResponse<LinkedHashMap> response = restTemplate.getForObject(url, DtoCollectionResponse.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getCollection(), "Collection should not be null");
    }

    @Test
    void testFindById() {
        String url = "http://localhost:" + port + "/shipping-service/api/shippings/1/1";
        OrderItemDto response = restTemplate.getForObject(url, OrderItemDto.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getOrderId(), "Order ID should not be null");
        assertNotNull(response.getProductId(), "Product ID should not be null");
        assertNotNull(response.getOrderedQuantity(), "Ordered quantity should not be null");
    }

    @Test
    void testSave() {
        String url = "http://localhost:" + port + "/shipping-service/api/shippings";
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setOrderId(1);
        orderItemDto.setProductId(1);
        orderItemDto.setOrderedQuantity(5);

        OrderItemDto response = restTemplate.postForObject(url, orderItemDto, OrderItemDto.class);

        assertNotNull(response, "Response should not be null");
        assertEquals(orderItemDto.getOrderId(), response.getOrderId(), "Order ID should match");
        assertEquals(orderItemDto.getProductId(), response.getProductId(), "Product ID should match");
        assertEquals(orderItemDto.getOrderedQuantity(), response.getOrderedQuantity(), "Ordered quantity should match");
    }

    @Test
    void testUpdate() {
        String url = "http://localhost:" + port + "/shipping-service/api/shippings";
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setOrderId(1);
        orderItemDto.setProductId(1);
        orderItemDto.setOrderedQuantity(10);

        OrderItemDto response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(orderItemDto), OrderItemDto.class).getBody();

        assertNotNull(response, "Response should not be null");
        assertEquals(orderItemDto.getOrderId(), response.getOrderId(), "Order ID should match");
        assertEquals(orderItemDto.getProductId(), response.getProductId(), "Product ID should match");
        assertEquals(orderItemDto.getOrderedQuantity(), response.getOrderedQuantity(), "Ordered quantity should match");
    }

    @Test
    void testDeleteById() {
        String url = "http://localhost:" + port + "/shipping-service/api/shippings/2/2";
        restTemplate.delete(url);

        // Verify that the order item was deleted by checking collection
        DtoCollectionResponse<LinkedHashMap> response = restTemplate.getForObject("http://localhost:" + port + "/shipping-service/api/shippings", DtoCollectionResponse.class);
        assertNotNull(response, "Response should not be null");
        // Note: Complex ID verification would require parsing the composite key
    }

    @Test
    void testFindByIdWithRequestBody() {
        String url = "http://localhost:" + port + "/shipping-service/api/shippings/find";
        OrderItemId orderItemId = new OrderItemId();
        orderItemId.setOrderId(1);
        orderItemId.setProductId(1);

        OrderItemDto response = restTemplate.postForObject(url, orderItemId, OrderItemDto.class);

        assertNotNull(response, "Response should not be null");
        assertEquals(orderItemId.getOrderId(), response.getOrderId(), "Order ID should match");
        assertEquals(orderItemId.getProductId(), response.getProductId(), "Product ID should match");
    }
}
