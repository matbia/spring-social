package com.matbia.service;

import com.matbia.enums.Gender;
import com.matbia.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @MockBean
    private UserService userService;

    @Before
    public void setup() {
        User user = new User();
        user.setId(1);
        user.setFirstName("Tom");
        user.setLastName("Terca");
        user.setGender(Gender.MALE);
        user.setEmail("aBc@deF.org");
        user.setPassword("12345678");

        Mockito.when(userService.findByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(userService.getOne(1)).thenReturn(Optional.of(user));
    }

    @Test
    public void getOne() {
        Optional<User> user = userService.getOne(1);
        assertTrue(user.isPresent());
        assertEquals(user.get().getFirstName(), "Tom");
        assertEquals(user.get().getLastName(), "Terca");
    }

    @Test
    public void findByEmail() {
        User user = userService.findByEmail("abc@def.org");
        assertNotNull(user);
        assertEquals("Tom", user.getFirstName());
    }

    @Test
    public void save() {
        User user = new User();
        user.setFirstName("name");
        user.setLastName("surname");
        userService.save(user);
    }
}