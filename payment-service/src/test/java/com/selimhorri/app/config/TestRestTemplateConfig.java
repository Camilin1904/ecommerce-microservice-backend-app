package com.selimhorri.app.config;

import java.time.LocalDateTime;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.constant.AppConstant;
import com.selimhorri.app.dto.OrderDto;

@TestConfiguration
public class TestRestTemplateConfig {

    @Bean
    @Primary
    public RestTemplate testRestTemplate() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        
        // Mock responses for ORDER-SERVICE calls
        // Mock response for order ID 1
        OrderDto order1 = OrderDto.builder()
                .orderId(1)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order 1")
                .orderFee(99.99)
                .build();
        
        // Mock response for order ID 2
        OrderDto order2 = OrderDto.builder()
                .orderId(2)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order 2")
                .orderFee(199.99)
                .build();
        
        // Mock response for order ID 3
        OrderDto order3 = OrderDto.builder()
                .orderId(3)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order 3")
                .orderFee(299.99)
                .build();
        
        // Setup mock responses for different order IDs
        Mockito.when(restTemplate.getForObject(
                AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/1", 
                OrderDto.class))
                .thenReturn(order1);
        
        Mockito.when(restTemplate.getForObject(
                AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/2", 
                OrderDto.class))
                .thenReturn(order2);
        
        Mockito.when(restTemplate.getForObject(
                AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/3", 
                OrderDto.class))
                .thenReturn(order3);
        
        // Fallback for any other order ID
        Mockito.when(restTemplate.getForObject(
                Mockito.startsWith(AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/"), 
                Mockito.eq(OrderDto.class)))
                .thenReturn(OrderDto.builder()
                        .orderId(999)
                        .orderDate(LocalDateTime.now())
                        .orderDesc("Default Test Order")
                        .orderFee(499.99)
                        .build());
        
        return restTemplate;
    }
}
