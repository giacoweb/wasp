package it.unipd.dei.webapp.dao;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.util.PasswordUtil;

public class UserDAOTest {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        try {
            User user = new User();
            user.setName("Danil");
            user.setSurname("Test");
            user.setEmail("danil.test@example.com");
            user.setPasswordHash(PasswordUtil.hashPassword("secret123"));
            user.setRole(UserRole.REGISTERED);

            User createdUser = userDAO.createUser(user);
            System.out.println("Created user ID: " + createdUser.getId());

            User foundByEmail = userDAO.findByEmail("danil.test@example.com");
            System.out.println("Found by email: " + foundByEmail.getEmail());

            User foundById = userDAO.findById(createdUser.getId());
            System.out.println("Found by id: " + foundById.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}