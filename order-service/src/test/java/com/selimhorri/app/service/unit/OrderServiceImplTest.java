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

import com.selimhorri.app.domain.Order;
import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.exception.wrapper.OrderNotFoundException;
import com.selimhorri.app.repository.OrderRepository;
import com.selimhorri.app.resource.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderDto testOrderDto;
    private Cart testCart;
    private CartDto testCartDto;

    @BeforeEach
    void setUp() {
        testCart = Cart.builder()
                .cartId(1)
                .userId(1)
                .build();

        testCartDto = CartDto.builder()
                .cartId(1)
                .userId(1)
                .build();

        testOrder = Order.builder()
                .orderId(1)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order")
                .orderFee(99.99)
                .cart(testCart)
                .build();

        testOrderDto = OrderDto.builder()
                .orderId(1)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order")
                .orderFee(99.99)
                .cartDto(testCartDto)
                .build();
    }

    @Test
    void findAll_ShouldReturnListOfOrders_WhenOrdersExist() {
        // Given
        Order order2 = Order.builder()
                .orderId(2)
                .orderDate(LocalDateTime.now())
                .orderDesc("Second Order")
                .orderFee(149.99)
                .cart(testCart)
                .build();
        
        List<Order> orders = Arrays.asList(testOrder, order2);
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        List<OrderDto> result = orderService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Order", result.get(0).getOrderDesc());
        assertEquals("Second Order", result.get(1).getOrderDesc());
        
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoOrdersExist() {
        // Given
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<OrderDto> result = orderService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnOrder_WhenOrderExists() {
        // Given
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));

        // When
        OrderDto result = orderService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        assertEquals("Test Order", result.getOrderDesc());
        assertEquals(99.99, result.getOrderFee());
        assertNotNull(result.getOrderDate());
        assertEquals(1, result.getCartDto().getCartId());
        
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {
        // Given
        when(orderRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.findById(99)
        );
        
        assertEquals("Order with id: 99 not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99);
    }

    @Test
    void save_ShouldReturnSavedOrder_WhenValidOrderProvided() {
        // Given
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderDto result = orderService.save(testOrderDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        assertEquals("Test Order", result.getOrderDesc());
        assertEquals(99.99, result.getOrderFee());
        assertNotNull(result.getOrderDate());
        
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void update_ShouldReturnUpdatedOrder_WhenValidOrderProvided() {
        // Given
        testOrderDto.setOrderDesc("Updated Order");
        testOrder.setOrderDesc("Updated Order");
        
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderDto result = orderService.update(testOrderDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Order", result.getOrderDesc());
        assertEquals(99.99, result.getOrderFee());
        
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateWithId_ShouldReturnUpdatedOrder_WhenValidOrderProvided() {
        // Given
        OrderDto updatedOrderDto = OrderDto.builder()
                .orderDesc("Updated Order Description")
                .orderFee(199.99)
                .cartDto(testCartDto)
                .build();
        
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderDto result = orderService.update(1, updatedOrderDto);

        // Then
        assertNotNull(result);
        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateWithId_ShouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {
        // Given
        OrderDto updatedOrderDto = OrderDto.builder()
                .orderDesc("Updated Order Description")
                .orderFee(199.99)
                .cartDto(testCartDto)
                .build();
        
        when(orderRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.update(99, updatedOrderDto)
        );
        
        assertEquals("Order with id: 99 not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete_WhenOrderExists() {
        // Given
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).delete(any(Order.class));

        // When
        orderService.deleteById(1);

        // Then
        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).delete(any(Order.class));
    }

    @Test
    void deleteById_ShouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {
        // Given
        when(orderRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.deleteById(99)
        );
        
        assertEquals("Order with id: 99 not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(99);
        verify(orderRepository, never()).delete(any(Order.class));
    }
}
