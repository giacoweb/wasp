package it.unipd.dei.webapp.unit.service;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.service.UserService;
import it.unipd.dei.webapp.service.exception.InvalidCredentialsException;
import it.unipd.dei.webapp.service.exception.UserAlreadyExistsException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void registerWithNullNameThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register(null, "Surname", "a@b.com", "pass123"));
    }

    @Test
    void registerWithBlankNameThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("", "Surname", "a@b.com", "pass123"));
    }

    @Test
    void registerWithNullSurnameThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("Name", null, "a@b.com", "pass123"));
    }

    @Test
    void registerWithBlankEmailThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("Name", "Surname", "", "pass123"));
    }

    @Test
    void registerWithNullPasswordThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("Name", "Surname", "a@b.com", null));
    }

    @Test
    void loginWithNullEmailThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.login(null, "pass123"));
    }

    @Test
    void loginWithBlankEmailThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.login("", "pass123"));
    }

    @Test
    void loginWithNullPasswordThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.login("a@b.com", null));
    }

    @Test
    void loginWithBlankPasswordThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.login("a@b.com", ""));
    }

    @Test
    void registerAndLoginSucceeds() throws Exception {
        String email = "junit-user-" + System.currentTimeMillis() + "@test.com";

        User user = userService.register("Test", "User", email, "secret123");

        assertNotNull(user.getId());
        assertEquals("Test", user.getName());
        assertEquals("User", user.getSurname());
        assertEquals(email, user.getEmail());
        assertEquals(UserRole.REGISTERED, user.getRole());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void loginWithCorrectCredentialsReturnsUser() throws Exception {
        String email = "junit-login-" + System.currentTimeMillis() + "@test.com";
        userService.register("Test", "Login", email, "secret123");

        User loggedIn = userService.login(email, "secret123");

        assertNotNull(loggedIn);
        assertEquals(email, loggedIn.getEmail());
    }

    @Test
    void loginWithWrongPasswordThrowsInvalidCredentials() throws Exception {
        String email = "junit-wrongpw-" + System.currentTimeMillis() + "@test.com";
        userService.register("Test", "WrongPw", email, "secret123");

        assertThrows(InvalidCredentialsException.class, () ->
                userService.login(email, "wrongpassword"));
    }

    @Test
    void loginWithNonexistentEmailThrowsInvalidCredentials() {
        assertThrows(InvalidCredentialsException.class, () ->
                userService.login("nonexistent-" + System.currentTimeMillis() + "@test.com", "pass123"));
    }

    @Test
    void registerDuplicateEmailThrowsAlreadyExists() throws Exception {
        String email = "junit-dup-" + System.currentTimeMillis() + "@test.com";
        userService.register("Test", "Dup", email, "secret123");

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.register("Test", "Dup2", email, "secret456"));
    }

    @Test
    void registeredUserPasswordIsHashed() throws Exception {
        String email = "junit-hash-" + System.currentTimeMillis() + "@test.com";
        User user = userService.register("Test", "Hash", email, "secret123");

        assertNotEquals("secret123", user.getPasswordHash());
        assertNotNull(user.getPasswordHash());
    }
}
