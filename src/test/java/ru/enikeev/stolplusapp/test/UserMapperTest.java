package ru.enikeev.stolplusapp.test;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.enikeev.stolplusapp.dto.request.UserRegistrationRequestDTO;
import ru.enikeev.stolplusapp.dto.request.UserUpdateRequestDTO;
import ru.enikeev.stolplusapp.dto.response.UserResponseDTO;
import ru.enikeev.stolplusapp.mapper.UserMapper;
import ru.enikeev.stolplusapp.model.User;
import ru.enikeev.stolplusapp.model.enums.Role;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testToResponseDTO(){
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserName("testUser");
        user.setEmail("test@mail.com");
        user.setPhone("+79374487783");
        user.setAddress("Test Address");
        user.setRole(Role.USER);
        user.setDiscount(new BigDecimal("10.00"));

        UserResponseDTO dto = userMapper.toResponseDTO(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getUserName(), dto.getUserName());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getPhone(), dto.getPhone());
        assertEquals(user.getAddress(), dto.getAddress());
        assertEquals(user.getRole(), dto.getRole());
        assertEquals(user.getDiscount(), dto.getDiscount());

    }

    @Test
    void testToEntityFromRegistration(){
        UserRegistrationRequestDTO requestDTO = new UserRegistrationRequestDTO();
        requestDTO.setUserName("newUser");
        requestDTO.setPassword("password123");
        requestDTO.setEmail("new@mail.com");
        requestDTO.setPhone("+79273631290");
        requestDTO.setAddress("New Address");

        User user = userMapper.toEntityFromRegistration(requestDTO);

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals(user.getUserName(), requestDTO.getUserName());
        assertEquals(user.getPassword(), requestDTO.getPassword());
        assertEquals(user.getEmail(), requestDTO.getEmail());
        assertEquals(user.getPhone(), requestDTO.getPhone());
        assertEquals(user.getAddress(), requestDTO.getAddress());
        assertEquals(Role.USER, user.getRole());
        assertEquals(BigDecimal.ZERO, user.getDiscount());
        assertNotNull(user.getOrders());
    }

    @Test
    void testUpdateUserFromDTO(){
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setUserName("oldName");
        existingUser.setPassword("oldPassword");
        existingUser.setEmail("old@mail.com");
        existingUser.setPhone("+79000000000");
        existingUser.setAddress("Old Address");
        existingUser.setRole(Role.USER);
        existingUser.setDiscount(new BigDecimal("5.00"));

        UserUpdateRequestDTO updateDTO = new UserUpdateRequestDTO();
        updateDTO.setUserName("newName");
        updateDTO.setEmail("new@mail.com");
        updateDTO.setPhone("+79123456789");
        updateDTO.setAddress("New Address");

        userMapper.updateUserFromDTO(updateDTO, existingUser);

        // Then
        assertEquals("newName", existingUser.getUserName());
        assertEquals("new@mail.com", existingUser.getEmail());
        assertEquals("+79123456789", existingUser.getPhone());
        assertEquals("New Address", existingUser.getAddress());
        // Эти поля не должны измениться:
        assertEquals("oldPassword", existingUser.getPassword());
        assertEquals(Role.USER, existingUser.getRole());
        assertEquals(new BigDecimal("5.00"), existingUser.getDiscount());
    }
}
