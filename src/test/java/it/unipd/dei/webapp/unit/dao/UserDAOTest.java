package it.unipd.dei.webapp.unit.dao;

import it.unipd.dei.webapp.dao.UserDAO;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    private User buildTestUser() {
        User user = new User();
        user.setName("Test");
        user.setSurname("DAO");
        user.setEmail("junit-dao-" + System.currentTimeMillis() + "@test.com");
        user.setPasswordHash(PasswordUtil.hashPassword("secret123"));
        user.setRole(UserRole.REGISTERED);
        return user;
    }

    @Test
    void createUserReturnsUserWithGeneratedId() throws Exception {
        User user = buildTestUser();

        User created = userDAO.createUser(user);

        assertNotNull(created.getId());
        assertEquals(user.getEmail(), created.getEmail());
        assertEquals(user.getName(), created.getName());
        assertEquals(user.getSurname(), created.getSurname());
        assertEquals(UserRole.REGISTERED, created.getRole());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    void findByEmailReturnsCorrectUser() throws Exception {
        User user = buildTestUser();
        User created = userDAO.createUser(user);

        User found = userDAO.findByEmail(created.getEmail());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getEmail(), found.getEmail());
    }

    @Test
    void findByEmailReturnsNullForNonexistent() throws Exception {
        User found = userDAO.findByEmail("nonexistent-" + System.currentTimeMillis() + "@test.com");

        assertNull(found);
    }

    @Test
    void findByIdReturnsCorrectUser() throws Exception {
        User user = buildTestUser();
        User created = userDAO.createUser(user);

        User found = userDAO.findById(created.getId());

        assertNotNull(found);
        assertEquals(created.getEmail(), found.getEmail());
    }

    @Test
    void findByIdReturnsNullForNonexistent() throws Exception {
        User found = userDAO.findById(UUID.randomUUID());

        assertNull(found);
    }
}
