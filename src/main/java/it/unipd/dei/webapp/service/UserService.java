package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.UserDAO;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.service.exception.InvalidCredentialsException;
import it.unipd.dei.webapp.service.exception.UserAlreadyExistsException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import it.unipd.dei.webapp.util.PasswordUtil;

public class UserService {

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

        validateRegistrationInput(name, surname, email, password);

        User existingUser = userDAO.findByEmail(email);
        if (existingUser != null) {
            throw new UserAlreadyExistsException();
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);
        user.setRole(UserRole.REGISTERED);

        return userDAO.createUser(user);
    }

    public User login(final String email, final String password)
            throws Exception, ValidationException, InvalidCredentialsException {

        if (email == null || email.isBlank()) {
            throw new ValidationException("Email cannot be empty.");
        }

        if (password == null || password.isBlank()) {
            throw new ValidationException("Password cannot be empty.");
        }

        User user = userDAO.findByEmail(email);

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
    }
}