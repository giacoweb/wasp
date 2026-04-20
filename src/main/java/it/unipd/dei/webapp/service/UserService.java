package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.UserDAO;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.service.exception.InvalidCredentialsException;
import it.unipd.dei.webapp.service.exception.UserAlreadyExistsException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import it.unipd.dei.webapp.util.PasswordUtil;

import java.util.Locale;

public class UserService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(final UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(final String name,
                         final String surname,
                         final String email,
                         final String password)
            throws Exception, ValidationException, UserAlreadyExistsException {

        String normalizedName = normalizeRequiredField(name);
        String normalizedSurname = normalizeRequiredField(surname);
        String normalizedEmail = normalizeEmail(email);

        validateRegistrationInput(normalizedName, normalizedSurname, normalizedEmail, password);

        User existingUser = userDAO.findByEmail(normalizedEmail);
        if (existingUser != null) {
            throw new UserAlreadyExistsException();
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User();
        user.setName(normalizedName);
        user.setSurname(normalizedSurname);
        user.setEmail(normalizedEmail);
        user.setPasswordHash(hashedPassword);
        user.setRole(UserRole.REGISTERED);

        return userDAO.createUser(user);
    }

    public User login(final String email, final String password)
            throws Exception, ValidationException, InvalidCredentialsException {

        String normalizedEmail = normalizeEmail(email);

        if (password == null || password.isBlank()) {
            throw new ValidationException("Password cannot be empty.");
        }

        User user = userDAO.findByEmail(normalizedEmail);

        if (user == null) {
            throw new InvalidCredentialsException();
        }

        boolean passwordMatches = PasswordUtil.verifyPassword(password, user.getPasswordHash());

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    private void validateRegistrationInput(final String name,
                                           final String surname,
                                           final String email,
                                           final String password) throws ValidationException {

        if (name == null || name.isBlank()) {
            throw new ValidationException("Name cannot be empty.");
        }

        if (surname == null || surname.isBlank()) {
            throw new ValidationException("Surname cannot be empty.");
        }

        if (email == null || email.isBlank()) {
            throw new ValidationException("Email cannot be empty.");
        }

        if (password == null || password.isBlank()) {
            throw new ValidationException("Password cannot be empty.");
        }

        validatePasswordStrength(password);
    }

    private void validatePasswordStrength(final String password) throws ValidationException {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Password must contain at least 8 characters, one digit, and one special character.");
        }

        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        if (!hasDigit || !hasSpecial) {
            throw new ValidationException("Password must contain at least 8 characters, one digit, and one special character.");
        }
    }

    private String normalizeRequiredField(final String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    private String normalizeEmail(final String email) throws ValidationException {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email cannot be empty.");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
