package de.adesso.kicker.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private UserDummy userDummy = new UserDummy();

    private User user = userDummy.defaultUser();
    private User otherUser = userDummy.alternateUser();

    @BeforeAll
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, otherUser));

        when(userRepository.findByUserId(anyString())).thenReturn(null);
        when(userRepository.findByUserId(eq(user.getUserId()))).thenReturn(user);

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.findByEmail(eq(user.getEmail()))).thenReturn(user);

        when(userRepository.save(any(User.class))).thenAnswer((Answer<User>) invocation -> {
            Object[] args = invocation.getArguments();
            return (User) args[0];
        });
    }

    @Test
    void testGetAllUsers() {
        // when
        ArrayList<User> allUsers = new ArrayList<>(userService.getAllUsers());

        // then
        assertTrue(allUsers.contains(user));
    }

    @Test
    void testGetUserById_Success() {
        // when
        User idUser = userService.getUserById(user.getUserId());

        // then
        assertEquals(idUser, user);
    }

    @Test
    void testGetUserByEmail_Success() {
        // when
        User emailUser = userService.getUserByEmail(user.getEmail());

        // then
        assertEquals(emailUser, user);
    }
}