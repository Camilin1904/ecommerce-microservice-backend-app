package com.selimhorri.app.resource.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.domain.PaymentStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PaymentResourceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @MockBean
    private RestTemplate mockRestTemplate;

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
        
        // Setup mock responses for ORDER-SERVICE calls
        OrderDto order1 = OrderDto.builder()
                .orderId(1)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order 1")
                .orderFee(99.99)
                .build();
        
        OrderDto order2 = OrderDto.builder()
                .orderId(2)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order 2")
                .orderFee(199.99)
                .build();
        
        OrderDto order3 = OrderDto.builder()
                .orderId(3)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order 3")
                .orderFee(299.99)
                .build();
        
        // Mock responses for specific order IDs
        Mockito.when(mockRestTemplate.getForObject(
                AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/1", 
                OrderDto.class))
                .thenReturn(order1);
        
        Mockito.when(mockRestTemplate.getForObject(
                AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/2", 
                OrderDto.class))
                .thenReturn(order2);
        
        Mockito.when(mockRestTemplate.getForObject(
                AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/3", 
                OrderDto.class))
                .thenReturn(order3);
        
        // Fallback for any other order ID
        Mockito.when(mockRestTemplate.getForObject(
                Mockito.startsWith(AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/"), 
                Mockito.eq(OrderDto.class)))
                .thenReturn(OrderDto.builder()
                        .orderId(999)
                        .orderDate(LocalDateTime.now())
                        .orderDesc("Default Test Order")
                        .orderFee(499.99)
                        .build());
    }

    @Test
    void testFindAll() {
        String url = "http://localhost:" + port + "/payment-service/api/payments";
        @SuppressWarnings("unchecked")
        DtoCollectionResponse<LinkedHashMap<String, Object>> response = restTemplate.getForObject(url, DtoCollectionResponse.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getCollection(), "Collection should not be null");
    }

    @Test
    void testFindById() {
        String url = "http://localhost:" + port + "/payment-service/api/payments/1";
        PaymentDto response = restTemplate.getForObject(url, PaymentDto.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getPaymentId(), "Payment ID should not be null");
        assertNotNull(response.getPaymentStatus(), "Payment status should not be null");
        assertNotNull(response.getIsPayed(), "IsPayed should not be null");
    }

    @Test
    void testSave() {
        String url = "http://localhost:" + port + "/payment-service/api/payments";
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        paymentDto.setIsPayed(false);

        PaymentDto response = restTemplate.postForObject(url, paymentDto, PaymentDto.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getPaymentId(), "Payment ID should not be null");
        assertEquals(paymentDto.getPaymentStatus(), response.getPaymentStatus(), "Payment status should match");
        assertEquals(paymentDto.getIsPayed(), response.getIsPayed(), "IsPayed should match");
    }

    @Test
    void testUpdate() {
        String url = "http://localhost:" + port + "/payment-service/api/payments";
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(1);
        paymentDto.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentDto.setIsPayed(true);

        PaymentDto response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(paymentDto), PaymentDto.class).getBody();

        assertNotNull(response, "Response should not be null");
        assertEquals(paymentDto.getPaymentStatus(), response.getPaymentStatus(), "Payment status should match");
        assertEquals(paymentDto.getIsPayed(), response.getIsPayed(), "IsPayed should match");
    }

    @Test
    void testDelete() {
        String url = "http://localhost:" + port + "/payment-service/api/payments/2";
        restTemplate.delete(url);

        // Verify that the payment was deleted by checking collection
        @SuppressWarnings("unchecked")
        DtoCollectionResponse<LinkedHashMap<String, Object>> response = restTemplate.getForObject("http://localhost:" + port + "/payment-service/api/payments", DtoCollectionResponse.class);
        assertNotNull(response, "Response should not be null");
        assertFalse(response.getCollection().stream().anyMatch(payment -> payment.get("paymentId").equals(2)), "Payment should be deleted");
    }
}
