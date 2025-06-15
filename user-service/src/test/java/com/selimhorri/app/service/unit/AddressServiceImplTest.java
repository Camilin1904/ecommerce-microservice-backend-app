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

import com.selimhorri.app.domain.Address;
import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.AddressNotFoundException;
import com.selimhorri.app.repository.AddressRepository;
import com.selimhorri.app.resource.impl.AddressServiceImpl;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address testAddress;
    private AddressDto testAddressDto;

    @BeforeEach
    void setUp() {
        Credential testCredential = Credential.builder()
                .credentialId(1)
                .username("johndoe")
                .password("password123")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        User testUser = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .credential(testCredential)
                .build();

        testAddress = Address.builder()
                .addressId(1)
                .fullAddress("123 Main Street")
                .postalCode("12345")
                .city("New York")
                .user(testUser)
                .build();

        CredentialDto testCredentialDto = CredentialDto.builder()
                .credentialId(1)
                .username("johndoe")
                .password("password123")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        UserDto testUserDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .credentialDto(testCredentialDto)
                .build();

        testAddressDto = AddressDto.builder()
                .addressId(1)
                .fullAddress("123 Main Street")
                .postalCode("12345")
                .city("New York")
                .userDto(testUserDto)
                .build();
    }

    @Test
    void findAll_ShouldReturnListOfAddresses_WhenAddressesExist() {
        // Given
        Credential credential2 = Credential.builder()
                .credentialId(2)
                .username("janesmith")
                .password("password456")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        User user2 = User.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("0987654321")
                .credential(credential2)
                .build();

        Address address2 = Address.builder()
                .addressId(2)
                .fullAddress("456 Oak Avenue")
                .postalCode("54321")
                .city("Los Angeles")
                .user(user2)
                .build();
        
        List<Address> addresses = Arrays.asList(testAddress, address2);
        when(addressRepository.findAll()).thenReturn(addresses);

        // When
        List<AddressDto> result = addressService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("123 Main Street", result.get(0).getFullAddress());
        assertEquals("456 Oak Avenue", result.get(1).getFullAddress());
        
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoAddressesExist() {
        // Given
        when(addressRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<AddressDto> result = addressService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnAddress_WhenAddressExists() {
        // Given
        when(addressRepository.findById(1)).thenReturn(Optional.of(testAddress));

        // When
        AddressDto result = addressService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getAddressId());
        assertEquals("123 Main Street", result.getFullAddress());
        assertEquals("12345", result.getPostalCode());
        assertEquals("New York", result.getCity());
        
        verify(addressRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldThrowAddressNotFoundException_WhenAddressDoesNotExist() {
        // Given
        when(addressRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        AddressNotFoundException exception = assertThrows(
                AddressNotFoundException.class,
                () -> addressService.findById(99)
        );
        
        assertEquals("#### Address with id: 99 not found! ####", exception.getMessage());
        verify(addressRepository, times(1)).findById(99);
    }

    @Test
    void save_ShouldReturnSavedAddress_WhenValidAddressProvided() {
        // Given
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When
        AddressDto result = addressService.save(testAddressDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getAddressId());
        assertEquals("123 Main Street", result.getFullAddress());
        assertEquals("12345", result.getPostalCode());
        assertEquals("New York", result.getCity());
        
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void update_ShouldReturnUpdatedAddress_WhenValidAddressProvided() {
        // Given
        testAddressDto.setFullAddress("123 Updated Street");
        testAddress.setFullAddress("123 Updated Street");
        
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When
        AddressDto result = addressService.update(testAddressDto);

        // Then
        assertNotNull(result);
        assertEquals("123 Updated Street", result.getFullAddress());
        assertEquals("12345", result.getPostalCode());
        assertEquals("New York", result.getCity());
        
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void updateWithId_ShouldReturnUpdatedAddress_WhenValidAddressProvided() {
        // Given
        AddressDto updatedAddressDto = AddressDto.builder()
                .fullAddress("789 Updated Boulevard")
                .postalCode("99999")
                .city("Updated City")
                .build();
        
        when(addressRepository.findById(1)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When
        AddressDto result = addressService.update(1, updatedAddressDto);

        // Then
        assertNotNull(result);
        verify(addressRepository, times(1)).findById(1);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void updateWithId_ShouldThrowAddressNotFoundException_WhenAddressDoesNotExist() {
        // Given
        AddressDto updatedAddressDto = AddressDto.builder()
                .fullAddress("789 Updated Boulevard")
                .postalCode("99999")
                .city("Updated City")
                .build();
        
        when(addressRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        AddressNotFoundException exception = assertThrows(
                AddressNotFoundException.class,
                () -> addressService.update(99, updatedAddressDto)
        );
        
        assertEquals("#### Address with id: 99 not found! ####", exception.getMessage());
        verify(addressRepository, times(1)).findById(99);
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        // Given
        doNothing().when(addressRepository).deleteById(1);

        // When
        addressService.deleteById(1);

        // Then
        verify(addressRepository, times(1)).deleteById(1);
    }
}
