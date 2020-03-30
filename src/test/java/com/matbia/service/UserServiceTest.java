package com.matbia.service;

import com.matbia.enums.Gender;
import com.matbia.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;
    private User user;

    @Before
    public void setup() {
        user = new User();
        user.setFirstName("Testfirstname");
        user.setLastName("Testlastname");
        user.setGender(Gender.MALE);
        user.setEmail("aBcD123@test.xyz");
        user.setPassword("12345678");
        userService.save(user);
    }

    @Test
    public void getOne() {
        System.out.println(user.getId());
        Optional<User> found = userService.getOne(user.getId());
        assertTrue(found.isPresent());
        assertEquals(user.getFirstName(), found.get().getFirstName());
        assertEquals(user.getLastName(), found.get().getLastName());
    }

    @Test
    public void findByEmail() {
        User found = userService.findByEmail(user.getEmail().toLowerCase());
        assertNotNull(found);
        assertEquals(user.getFirstName(), found.getFirstName());
        assertEquals(user.getId(), found.getId());
    }

    @Test
    public void save() {
        assertTrue(user.getId() > 0);
    }

    @Test
    public void getAll() {
        assertTrue(userService.getAll().stream().anyMatch(u ->
                u.getEmail().equals(user.getEmail()) && u.getFirstName().equals(user.getFirstName()) && u.getLastName().equals(user.getLastName())
        ));
    }

    @Test
    public void getSize() {
        assertTrue(userService.getSize() > 0);
    }
}