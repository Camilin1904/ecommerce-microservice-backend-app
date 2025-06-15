package com.selimhorri.app.service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.CartNotFoundException;
import com.selimhorri.app.repository.CartRepository;
import com.selimhorri.app.resource.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart testCart;
    private CartDto testCartDto;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        testCart = Cart.builder()
                .cartId(1)
                .userId(1)
                .build();

        testCartDto = CartDto.builder()
                .cartId(1)
                .userId(1)
                .userDto(testUserDto)
                .build();
    }

    @Test
    void findAll_ShouldReturnListOfCarts_WhenCartsExist() {
        // Given
        Cart cart2 = Cart.builder()
                .cartId(2)
                .userId(2)
                .build();
        
        List<Cart> carts = Arrays.asList(testCart, cart2);
        when(cartRepository.findAll()).thenReturn(carts);
        when(restTemplate.getForObject(anyString(), eq(UserDto.class))).thenReturn(testUserDto);

        // When
        List<CartDto> result = cartService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getCartId());
        assertEquals(2, result.get(1).getCartId());
        
        verify(cartRepository, times(1)).findAll();
        verify(restTemplate, times(2)).getForObject(anyString(), eq(UserDto.class));
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoCartsExist() {
        // Given
        when(cartRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<CartDto> result = cartService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(cartRepository, times(1)).findAll();
        verify(restTemplate, never()).getForObject(anyString(), eq(UserDto.class));
    }

    @Test
    void findById_ShouldReturnCart_WhenCartExists() {
        // Given
        when(cartRepository.findById(1)).thenReturn(Optional.of(testCart));
        when(restTemplate.getForObject(anyString(), eq(UserDto.class))).thenReturn(testUserDto);

        // When
        CartDto result = cartService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getCartId());
        assertEquals(1, result.getUserId());
        assertNotNull(result.getUserDto());
        assertEquals("John", result.getUserDto().getFirstName());
        
        verify(cartRepository, times(1)).findById(1);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(UserDto.class));
    }

    @Test
    void findById_ShouldThrowCartNotFoundException_WhenCartDoesNotExist() {
        // Given
        when(cartRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        CartNotFoundException exception = assertThrows(
                CartNotFoundException.class,
                () -> cartService.findById(99)
        );
        
        assertEquals("Cart with id: 99 not found", exception.getMessage());
        verify(cartRepository, times(1)).findById(99);
        verify(restTemplate, never()).getForObject(anyString(), eq(UserDto.class));
    }

    @Test
    void save_ShouldReturnSavedCart_WhenValidCartProvided() {
        // Given
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        CartDto result = cartService.save(testCartDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getCartId());
        assertEquals(1, result.getUserId());
        
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void update_ShouldReturnUpdatedCart_WhenValidCartProvided() {
        // Given
        testCartDto.setUserId(2);
        testCart.setUserId(2);
        
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        CartDto result = cartService.update(testCartDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getCartId());
        assertEquals(2, result.getUserId());
        
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void updateWithId_ShouldReturnUpdatedCart_WhenValidCartProvided() {
        // Given
        CartDto updatedCartDto = CartDto.builder()
                .userId(3)
                .userDto(testUserDto)
                .build();
        
        when(cartRepository.findById(1)).thenReturn(Optional.of(testCart));
        when(restTemplate.getForObject(anyString(), eq(UserDto.class))).thenReturn(testUserDto);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        CartDto result = cartService.update(1, updatedCartDto);

        // Then
        assertNotNull(result);
        verify(cartRepository, times(1)).findById(1);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(UserDto.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void updateWithId_ShouldThrowCartNotFoundException_WhenCartDoesNotExist() {
        // Given
        CartDto updatedCartDto = CartDto.builder()
                .userId(3)
                .userDto(testUserDto)
                .build();
        
        when(cartRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        CartNotFoundException exception = assertThrows(
                CartNotFoundException.class,
                () -> cartService.update(99, updatedCartDto)
        );
        
        assertEquals("Cart with id: 99 not found", exception.getMessage());
        verify(cartRepository, times(1)).findById(99);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        // Given
        doNothing().when(cartRepository).deleteById(1);

        // When
        cartService.deleteById(1);

        // Then
        verify(cartRepository, times(1)).deleteById(1);
    }
}
