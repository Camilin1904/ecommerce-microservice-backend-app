package com.selimhorri.app.service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.domain.OrderItem;
import com.selimhorri.app.domain.id.OrderItemId;
import com.selimhorri.app.dto.OrderItemDto;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.OrderItemNotFoundException;
import com.selimhorri.app.repository.OrderItemRepository;
import com.selimhorri.app.resource.impl.OrderItemServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private OrderItem testOrderItem;
    private OrderItemDto testOrderItemDto;
    private OrderItemId testOrderItemId;
    private OrderDto testOrderDto;
    private ProductDto testProductDto;

    @BeforeEach
    void setUp() {
        testOrderItemId = new OrderItemId(1, 1);

        testOrderDto = OrderDto.builder()
                .orderId(1)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order")
                .orderFee(99.99)
                .build();

        testProductDto = ProductDto.builder()
                .productId(1)
                .productTitle("Test Product")
                .imageUrl("test-image.jpg")
                .sku("TEST-SKU-001")
                .priceUnit(49.99)
                .quantity(10)
                .build();

        testOrderItem = OrderItem.builder()
                .orderId(1)
                .productId(1)
                .orderedQuantity(2)
                .build();

        testOrderItemDto = OrderItemDto.builder()
                .productId(1)
                .orderId(1)
                .orderedQuantity(2)
                .orderDto(testOrderDto)
                .productDto(testProductDto)
                .build();
    }

    @Test
    void findAll_ShouldReturnListOfOrderItems_WhenOrderItemsExist() {
        // Given
        OrderItemId orderItemId2 = new OrderItemId(2, 2);

        OrderItem orderItem2 = OrderItem.builder()
                .orderId(2)
                .productId(2)
                .orderedQuantity(3)
                .build();
        
        List<OrderItem> orderItems = Arrays.asList(testOrderItem, orderItem2);
        when(orderItemRepository.findAll()).thenReturn(orderItems);
        when(restTemplate.getForObject(contains("product-service"), eq(ProductDto.class))).thenReturn(testProductDto);
        when(restTemplate.getForObject(contains("order-service"), eq(OrderDto.class))).thenReturn(testOrderDto);

        // When
        List<OrderItemDto> result = orderItemService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getOrderedQuantity());
        assertEquals(3, result.get(1).getOrderedQuantity());
        
        verify(orderItemRepository, times(1)).findAll();
        verify(restTemplate, times(2)).getForObject(contains("product-service"), eq(ProductDto.class));
        verify(restTemplate, times(2)).getForObject(contains("order-service"), eq(OrderDto.class));
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoOrderItemsExist() {
        // Given
        when(orderItemRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<OrderItemDto> result = orderItemService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(orderItemRepository, times(1)).findAll();
        verify(restTemplate, never()).getForObject(anyString(), eq(ProductDto.class));
        verify(restTemplate, never()).getForObject(anyString(), eq(OrderDto.class));
    }

    @Test
    void findById_ShouldReturnOrderItem_WhenOrderItemExists() {
        // Given
        when(orderItemRepository.findById(testOrderItemId)).thenReturn(Optional.of(testOrderItem));

        // When
        OrderItemDto result = orderItemService.findById(testOrderItemId);

        // Then
        assertNotNull(result);
        assertEquals(testOrderItemId.getOrderId(), result.getOrderId());
        assertEquals(testOrderItemId.getProductId(), result.getProductId());
        assertEquals(2, result.getOrderedQuantity());
        
        verify(orderItemRepository, times(1)).findById(testOrderItemId);
    }

    @Test
    void findById_ShouldThrowOrderItemNotFoundException_WhenOrderItemDoesNotExist() {
        // Given
        OrderItemId nonExistentId = new OrderItemId(99, 99);
        
        when(orderItemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        OrderItemNotFoundException exception = assertThrows(
                OrderItemNotFoundException.class,
                () -> orderItemService.findById(nonExistentId)
        );
        
        assertTrue(exception.getMessage().contains("OrderItem with id:"));
        assertTrue(exception.getMessage().contains("not found"));
        verify(orderItemRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void save_ShouldReturnSavedOrderItem_WhenValidOrderItemProvided() {
        // Given
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // When
        OrderItemDto result = orderItemService.save(testOrderItemDto);

        // Then
        assertNotNull(result);
        assertEquals(testOrderItemId.getOrderId(), result.getOrderId());
        assertEquals(testOrderItemId.getProductId(), result.getProductId());
        assertEquals(2, result.getOrderedQuantity());
        
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void update_ShouldReturnUpdatedOrderItem_WhenValidOrderItemProvided() {
        // Given
        testOrderItemDto.setOrderedQuantity(5);
        testOrderItem.setOrderedQuantity(5);
        
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // When
        OrderItemDto result = orderItemService.update(testOrderItemDto);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getOrderedQuantity());
        assertEquals(testOrderItemId.getOrderId(), result.getOrderId());
        assertEquals(testOrderItemId.getProductId(), result.getProductId());
        
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        // Given
        doNothing().when(orderItemRepository).deleteById(testOrderItemId);

        // When
        orderItemService.deleteById(testOrderItemId);

        // Then
        verify(orderItemRepository, times(1)).deleteById(testOrderItemId);
    }

    @Test
    void save_ShouldHandleZeroQuantity_WhenSaving() {
        // Given
        OrderItemDto orderItemWithZeroQuantity = OrderItemDto.builder()
                .productId(1)
                .orderId(1)
                .orderedQuantity(0)
                .orderDto(testOrderDto)
                .productDto(testProductDto)
                .build();

        OrderItem savedOrderItem = OrderItem.builder()
                .orderId(1)
                .productId(1)
                .orderedQuantity(0)
                .build();

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedOrderItem);

        // When
        OrderItemDto result = orderItemService.save(orderItemWithZeroQuantity);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getOrderedQuantity());
        assertEquals(1, result.getOrderId());
        assertEquals(1, result.getProductId());
        
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void update_ShouldHandleNegativeQuantity_WhenUpdating() {
        // Given
        testOrderItemDto.setOrderedQuantity(-1);
        testOrderItem.setOrderedQuantity(-1);
        
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // When
        OrderItemDto result = orderItemService.update(testOrderItemDto);

        // Then
        assertNotNull(result);
        assertEquals(-1, result.getOrderedQuantity());
        assertEquals(testOrderItemId.getOrderId(), result.getOrderId());
        assertEquals(testOrderItemId.getProductId(), result.getProductId());
        
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void save_ShouldHandleNullOrderDto_WhenSaving() {
        // Given
        OrderItemDto orderItemWithNullOrder = OrderItemDto.builder()
                .productId(1)
                .orderId(1)
                .orderedQuantity(2)
                .orderDto(null)
                .productDto(testProductDto)
                .build();

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // When
        OrderItemDto result = orderItemService.save(orderItemWithNullOrder);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getOrderedQuantity());
        assertEquals(testOrderItemId.getOrderId(), result.getOrderId());
        assertEquals(testOrderItemId.getProductId(), result.getProductId());
        
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void update_ShouldHandleNullProductDto_WhenUpdating() {
        // Given
        OrderItemDto orderItemWithNullProduct = OrderItemDto.builder()
                .productId(1)
                .orderId(1)
                .orderedQuantity(2)
                .orderDto(testOrderDto)
                .productDto(null)
                .build();

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // When
        OrderItemDto result = orderItemService.update(orderItemWithNullProduct);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getOrderedQuantity());
        assertEquals(testOrderItemId.getOrderId(), result.getOrderId());
        assertEquals(testOrderItemId.getProductId(), result.getProductId());
        
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }
}
