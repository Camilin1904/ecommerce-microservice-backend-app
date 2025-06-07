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

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.resource.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;

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

        testUser = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .credential(testCredential)
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

        testUserDto = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .credentialDto(testCredentialDto)
                .build();
    }

    @Test
    void findAll_ShouldReturnListOfUsers_WhenUsersExist() {
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
        
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserDto> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
        
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<UserDto> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // When
        UserDto result = userService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void findById_ShouldThrowUserObjectNotFoundException_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        UserObjectNotFoundException exception = assertThrows(
                UserObjectNotFoundException.class,
                () -> userService.findById(99)
        );
        
        assertEquals("User with id: 99 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(99);
    }

    @Test
    void save_ShouldReturnSavedUser_WhenValidUserProvided() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.save(testUserDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_ShouldReturnUpdatedUser_WhenValidUserProvided() {
        // Given
        testUserDto.setFirstName("Updated John");
        testUser.setFirstName("Updated John");
        
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.update(testUserDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateWithId_ShouldReturnUpdatedUser_WhenValidUserProvided() {
        // Given
        UserDto updatedUserDto = UserDto.builder()
                .firstName("Updated John")
                .lastName("Updated Doe")
                .email("updated.john@example.com")
                .build();
        
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.update(1, updatedUserDto);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateWithId_ShouldThrowUserObjectNotFoundException_WhenUserDoesNotExist() {
        // Given
        UserDto updatedUserDto = UserDto.builder()
                .firstName("Updated John")
                .lastName("Updated Doe")
                .email("updated.john@example.com")
                .build();
        
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        UserObjectNotFoundException exception = assertThrows(
                UserObjectNotFoundException.class,
                () -> userService.update(99, updatedUserDto)
        );
        
        assertEquals("User with id: 99 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(99);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        // Given
        doNothing().when(userRepository).deleteById(1);

        // When
        userService.deleteById(1);

        // Then
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        String username = "johndoe";
        when(userRepository.findByCredentialUsername(username)).thenReturn(Optional.of(testUser));

        // When
        UserDto result = userService.findByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        
        verify(userRepository, times(1)).findByCredentialUsername(username);
    }

    @Test
    void findByUsername_ShouldThrowUserObjectNotFoundException_WhenUserDoesNotExist() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByCredentialUsername(username)).thenReturn(Optional.empty());

        // When & Then
        UserObjectNotFoundException exception = assertThrows(
                UserObjectNotFoundException.class,
                () -> userService.findByUsername(username)
        );
        
        assertEquals("User with username: nonexistent not found", exception.getMessage());
        verify(userRepository, times(1)).findByCredentialUsername(username);
    }
}
