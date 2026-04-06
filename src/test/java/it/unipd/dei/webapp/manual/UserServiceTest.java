package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.model.User;

public class UserServiceTest {

    public static void main(String[] args) {
        UserService userService = new UserService();

        try {
            User registeredUser = userService.register(
                    "Danil",
                    "Service",
                    "danil.service@example.com",
                    "secret123"
            );

            System.out.println("Registered user: " + registeredUser.getEmail());

            User loggedInUser = userService.login(
                    "danil.service@example.com",
                    "secret123"
            );

            System.out.println("Logged in user: " + loggedInUser.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}