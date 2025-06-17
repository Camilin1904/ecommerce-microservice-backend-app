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

import com.selimhorri.app.domain.Favourite;
import com.selimhorri.app.domain.id.FavouriteId;
import com.selimhorri.app.dto.FavouriteDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.FavouriteNotFoundException;
import com.selimhorri.app.repository.FavouriteRepository;
import com.selimhorri.app.resource.impl.FavouriteServiceImpl;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceImplTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FavouriteServiceImpl favouriteService;

    private Favourite testFavourite;
    private FavouriteDto testFavouriteDto;
    private FavouriteId testFavouriteId;
    private UserDto testUserDto;
    private ProductDto testProductDto;

    @BeforeEach
    void setUp() {
        testFavouriteId = new FavouriteId(1, 1, LocalDateTime.now());

        testUserDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        testProductDto = ProductDto.builder()
                .productId(1)
                .productTitle("Test Product")
                .imageUrl("test-image.jpg")
                .sku("TEST-SKU-001")
                .priceUnit(99.99)
                .quantity(10)
                .build();

        testFavourite = Favourite.builder()
                .userId(1)
                .productId(1)
                .likeDate(LocalDateTime.now())
                .build();

        testFavouriteDto = FavouriteDto.builder()
                .userId(1)
                .productId(1)
                .likeDate(LocalDateTime.now())
                .userDto(testUserDto)
                .productDto(testProductDto)
                .build();
    }

    @Test
    void findAll_ShouldReturnListOfFavourites_WhenFavouritesExist() {
        // Given
        FavouriteId favouriteId2 = new FavouriteId(2, 2, LocalDateTime.now());

        Favourite favourite2 = Favourite.builder()
                .userId(2)
                .productId(2)
                .likeDate(LocalDateTime.now())
                .build();
        
        List<Favourite> favourites = Arrays.asList(testFavourite, favourite2);
        when(favouriteRepository.findAll()).thenReturn(favourites);
        when(restTemplate.getForObject(contains("user-service"), eq(UserDto.class))).thenReturn(testUserDto);
        when(restTemplate.getForObject(contains("product-service"), eq(ProductDto.class))).thenReturn(testProductDto);

        // When
        List<FavouriteDto> result = favouriteService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getUserId());
        assertEquals(2, result.get(1).getUserId());
        
        verify(favouriteRepository, times(1)).findAll();
        verify(restTemplate, times(2)).getForObject(contains("user-service"), eq(UserDto.class));
        verify(restTemplate, times(2)).getForObject(contains("product-service"), eq(ProductDto.class));
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoFavouritesExist() {
        // Given
        when(favouriteRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<FavouriteDto> result = favouriteService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(favouriteRepository, times(1)).findAll();
        verify(restTemplate, never()).getForObject(anyString(), eq(UserDto.class));
        verify(restTemplate, never()).getForObject(anyString(), eq(ProductDto.class));
    }

    @Test
    void findById_ShouldReturnFavourite_WhenFavouriteExists() {
        // Given
        when(favouriteRepository.findById(testFavouriteId)).thenReturn(Optional.of(testFavourite));
        when(restTemplate.getForObject(contains("user-service"), eq(UserDto.class))).thenReturn(testUserDto);
        when(restTemplate.getForObject(contains("product-service"), eq(ProductDto.class))).thenReturn(testProductDto);

        // When
        FavouriteDto result = favouriteService.findById(testFavouriteId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getProductId());
        assertNotNull(result.getLikeDate());
        assertNotNull(result.getUserDto());
        assertNotNull(result.getProductDto());
        assertEquals("John", result.getUserDto().getFirstName());
        assertEquals("Test Product", result.getProductDto().getProductTitle());
        
        verify(favouriteRepository, times(1)).findById(testFavouriteId);
        verify(restTemplate, times(1)).getForObject(contains("user-service"), eq(UserDto.class));
        verify(restTemplate, times(1)).getForObject(contains("product-service"), eq(ProductDto.class));
    }

    @Test
    void findById_ShouldThrowFavouriteNotFoundException_WhenFavouriteDoesNotExist() {
        // Given
        when(favouriteRepository.findById(testFavouriteId)).thenReturn(Optional.empty());

        // When & Then
        FavouriteNotFoundException exception = assertThrows(
                FavouriteNotFoundException.class,
                () -> favouriteService.findById(testFavouriteId)
        );
        
        assertTrue(exception.getMessage().contains("Favourite with id:"));
        assertTrue(exception.getMessage().contains("not found!"));
        verify(favouriteRepository, times(1)).findById(testFavouriteId);
        verify(restTemplate, never()).getForObject(anyString(), eq(UserDto.class));
        verify(restTemplate, never()).getForObject(anyString(), eq(ProductDto.class));
    }

    @Test
    void save_ShouldReturnSavedFavourite_WhenValidFavouriteProvided() {
        // Given
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(testFavourite);

        // When
        FavouriteDto result = favouriteService.save(testFavouriteDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getProductId());
        assertNotNull(result.getLikeDate());
        
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    void update_ShouldReturnUpdatedFavourite_WhenValidFavouriteProvided() {
        // Given
        LocalDateTime newDate = LocalDateTime.now().plusDays(1);
        testFavouriteDto.setLikeDate(newDate);
        testFavourite.setLikeDate(newDate);
        
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(testFavourite);

        // When
        FavouriteDto result = favouriteService.update(testFavouriteDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getProductId());
        assertEquals(newDate, result.getLikeDate());
        
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        // Given
        doNothing().when(favouriteRepository).deleteById(testFavouriteId);

        // When
        favouriteService.deleteById(testFavouriteId);

        // Then
        verify(favouriteRepository, times(1)).deleteById(testFavouriteId);
    }

    @Test
    void save_ShouldHandleNullUserDto_WhenSaving() {
        // Given
        FavouriteDto favouriteWithNullUser = FavouriteDto.builder()
                .userId(1)
                .productId(1)
                .likeDate(LocalDateTime.now())
                .userDto(null)
                .productDto(testProductDto)
                .build();

        when(favouriteRepository.save(any(Favourite.class))).thenReturn(testFavourite);

        // When
        FavouriteDto result = favouriteService.save(favouriteWithNullUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getProductId());
        
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    void update_ShouldHandleNullProductDto_WhenUpdating() {
        // Given
        FavouriteDto favouriteWithNullProduct = FavouriteDto.builder()
                .userId(1)
                .productId(1)
                .likeDate(LocalDateTime.now())
                .userDto(testUserDto)
                .productDto(null)
                .build();

        when(favouriteRepository.save(any(Favourite.class))).thenReturn(testFavourite);

        // When
        FavouriteDto result = favouriteService.update(favouriteWithNullProduct);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getProductId());
        
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }
}
