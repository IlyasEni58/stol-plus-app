package ru.enikeev.stolplusapp.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.enikeev.stolplusapp.dto.request.UserRegistrationRequestDTO;
import ru.enikeev.stolplusapp.dto.request.UserUpdateRequestDTO;
import ru.enikeev.stolplusapp.dto.response.UserResponseDTO;
import ru.enikeev.stolplusapp.exception.UserNotFoundException;
import ru.enikeev.stolplusapp.mapper.UserMapper;
import ru.enikeev.stolplusapp.model.User;
import ru.enikeev.stolplusapp.model.enums.Role;
import ru.enikeev.stolplusapp.repository.UserRepository;
import ru.enikeev.stolplusapp.service.UserService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegistrationUser_Success(){
        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setUserName("newuser");
        request.setPassword("password123");
        request.setEmail("new@mail.com");
        request.setPhone("+79123456789");
        request.setAddress("Test Address");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserName("newuser");
        user.setPassword("encodedPassword");
        user.setEmail("new@mail.com");
        user.setPhone("+79123456789");
        user.setRole(Role.USER);
        user.setDiscount(BigDecimal.ZERO);

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setId(user.getId());
        expectedResponse.setUserName("newuser");
        expectedResponse.setEmail("new@mail.com");

        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(userRepository.existsByUserName("newuser")).thenReturn(false);
        when(userMapper.toEntityFromRegistration(request)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.register(request);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getUserName(), result.getUserName());

        verify(userRepository).existsByEmail("new@mail.com");
        verify(userRepository).existsByUserName("newuser");
        verify(userMapper).toEntityFromRegistration(request);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(user);
        verify(userMapper).toResponseDTO(user);
    }



    @Test
    void testUpdateUser_Success() {
        // Given
        UUID userId = UUID.randomUUID();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserName("oldname");
        existingUser.setEmail("old@mail.com");
        existingUser.setPhone("+79000000000");
        existingUser.setAddress("Old Address");

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO();
        updateRequest.setUserName("newname");
        updateRequest.setEmail("new@mail.com");
        updateRequest.setPhone("+79123456789");
        updateRequest.setAddress("New Address");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUserName("newname");
        updatedUser.setEmail("new@mail.com");
        updatedUser.setPhone("+79123456789");
        updatedUser.setAddress("New Address");

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setId(userId);
        expectedResponse.setUserName("newname");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser); // ← ИСПРАВЛЕНО: убрал any()
        when(userMapper.toResponseDTO(updatedUser)).thenReturn(expectedResponse);

        // When
        UserResponseDTO result = userService.update(userId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("newname", result.getUserName());

        verify(userRepository).findById(userId);
        verify(userMapper).updateUserFromDTO(updateRequest, existingUser);
        verify(userRepository).save(existingUser); // ← ИСПРАВЛЕНО
        verify(userMapper).toResponseDTO(updatedUser);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void testFindByEmail_Success() {
        // Given
        String email = "test@mail.com";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setUserName("testuser");

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setId(user.getId());
        expectedResponse.setEmail(email);
        expectedResponse.setUserName("testuser");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(expectedResponse);

        // When
        UserResponseDTO result = userService.findByEmail(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("testuser", result.getUserName());

        verify(userRepository).findByEmail(email);
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void testChangePassword_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        String newPassword = "newPassword123";

        User user = new User();
        user.setId(userId);
        user.setPassword("oldEncodedPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");

        // When
        userService.changePassword(userId, newPassword);

        // Then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
        assertEquals("newEncodedPassword", user.getPassword());
    }

}
