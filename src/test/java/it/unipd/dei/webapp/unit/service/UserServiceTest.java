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

    private static final String VALID_PASSWORD = "Secret123!";

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void registerWithNullNameThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register(null, "Surname", "a@b.com", VALID_PASSWORD));
    }

    @Test
    void registerWithBlankNameThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("", "Surname", "a@b.com", VALID_PASSWORD));
    }

    @Test
    void registerWithNullSurnameThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("Name", null, "a@b.com", VALID_PASSWORD));
    }

    @Test
    void registerWithBlankEmailThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("Name", "Surname", "", VALID_PASSWORD));
    }

    @Test
    void registerWithNullPasswordThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("Name", "Surname", "a@b.com", null));
    }

    @Test
    void registerWithShortPasswordThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("Name", "Surname", "a@b.com", "Ab1!xyz"));
    }

    @Test
    void registerWithPasswordMissingDigitThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("Name", "Surname", "a@b.com", "Password!"));
    }

    @Test
    void registerWithPasswordMissingSpecialCharThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                userService.register("Name", "Surname", "a@b.com", "Password1"));
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

        User registered = userService.register("Test", "User", email, VALID_PASSWORD);
        User loggedIn = userService.login(email, VALID_PASSWORD);

        assertNotNull(registered.getId());
        assertEquals("Test", registered.getName());
        assertEquals("User", registered.getSurname());
        assertEquals(email, registered.getEmail());
        assertEquals(UserRole.REGISTERED, registered.getRole());
        assertNotNull(registered.getCreatedAt());

        assertNotNull(loggedIn);
        assertEquals(registered.getId(), loggedIn.getId());
        assertEquals(email, loggedIn.getEmail());
    }

    @Test
    void loginWithCorrectCredentialsReturnsUser() throws Exception {
        String email = "junit-login-" + System.currentTimeMillis() + "@test.com";
        userService.register("Test", "Login", email, VALID_PASSWORD);

        User loggedIn = userService.login(email, VALID_PASSWORD);

        assertNotNull(loggedIn);
        assertEquals(email, loggedIn.getEmail());
    }

    @Test
    void loginWithWrongPasswordThrowsInvalidCredentials() throws Exception {
        String email = "junit-wrongpw-" + System.currentTimeMillis() + "@test.com";
        userService.register("Test", "WrongPw", email, VALID_PASSWORD);

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
        userService.register("Test", "Dup", email, VALID_PASSWORD);

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.register("Test", "Dup2", email, "Secret456!"));
    }

    @Test
    void registeredUserPasswordIsHashed() throws Exception {
        String email = "junit-hash-" + System.currentTimeMillis() + "@test.com";
        User user = userService.register("Test", "Hash", email, VALID_PASSWORD);

        assertNotEquals(VALID_PASSWORD, user.getPasswordHash());
        assertNotNull(user.getPasswordHash());
    }

    @Test
    void registerNormalizesEmailAndNames() throws Exception {
        String rawEmail = "  Mixed.Case-" + System.currentTimeMillis() + "@Test.COM  ";

        User user = userService.register("  Test  ", "  User  ", rawEmail, VALID_PASSWORD);

        assertEquals("Test", user.getName());
        assertEquals("User", user.getSurname());
        assertEquals(rawEmail.trim().toLowerCase(), user.getEmail());
    }

    @Test
    void loginNormalizesEmailBeforeLookup() throws Exception {
        String email = "normalized-" + System.currentTimeMillis() + "@test.com";
        userService.register("Test", "Login", email, VALID_PASSWORD);

        User loggedIn = userService.login("  " + email.toUpperCase() + "  ", VALID_PASSWORD);

        assertNotNull(loggedIn);
        assertEquals(email, loggedIn.getEmail());
    }

    @Test
    void registerDuplicateEmailIsDetectedAfterNormalization() throws Exception {
        String email = "dup-normalized-" + System.currentTimeMillis() + "@test.com";
        userService.register("Test", "Dup", email, VALID_PASSWORD);

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.register("Test", "Dup2", "  " + email.toUpperCase() + "  ", "Secret456!"));
    }
}
