package it.unipd.dei.webapp.unit.dao;

import it.unipd.dei.webapp.dao.UserDAO;
import it.unipd.dei.webapp.dao.WalletDAO;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.model.WalletType;
import it.unipd.dei.webapp.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WalletDAOTest {

    private WalletDAO walletDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        walletDAO = new WalletDAO();
        userDAO = new UserDAO();
    }

    private User createTestUser() throws Exception {
        User user = new User();
        user.setName("Wallet");
        user.setSurname("DAOTest");
        user.setEmail("junit-wdao-" + System.currentTimeMillis() + "@test.com");
        user.setPasswordHash(PasswordUtil.hashPassword("secret123"));
        user.setRole(UserRole.REGISTERED);
        return userDAO.createUser(user);
    }

    @Test
    void createReturnsWalletWithGeneratedId() throws Exception {
        User user = createTestUser();

        Wallet wallet = walletDAO.create(user.getId(), "Test Wallet", WalletType.CASH, "EUR");

        assertNotNull(wallet.getId());
        assertEquals(user.getId(), wallet.getUserId());
        assertEquals("Test Wallet", wallet.getName());
        assertEquals(WalletType.CASH, wallet.getType());
        assertEquals("EUR", wallet.getCurrency());
        assertNotNull(wallet.getCreatedAt());
    }

    @Test
    void findByUserIdReturnsAllUserWallets() throws Exception {
        User user = createTestUser();

        walletDAO.create(user.getId(), "Wallet A", WalletType.CASH, "EUR");
        walletDAO.create(user.getId(), "Wallet B", WalletType.BANK_ACCOUNT, "USD");

        List<Wallet> wallets = walletDAO.findByUserId(user.getId());

        assertEquals(2, wallets.size());
    }

    @Test
    void findByUserIdReturnsEmptyForUnknownUser() throws Exception {
        List<Wallet> wallets = walletDAO.findByUserId(UUID.randomUUID());

        assertTrue(wallets.isEmpty());
    }

    @Test
    void findByIdReturnsCorrectWallet() throws Exception {
        User user = createTestUser();
        Wallet created = walletDAO.create(user.getId(), "Find Me", WalletType.CREDIT_CARD, "GBP");

        Wallet found = walletDAO.findById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Find Me", found.getName());
    }

    @Test
    void findByIdReturnsNullForNonexistent() throws Exception {
        Wallet found = walletDAO.findById(UUID.randomUUID());

        assertNull(found);
    }

    @Test
    void updateModifiesWalletFields() throws Exception {
        User user = createTestUser();
        Wallet created = walletDAO.create(user.getId(), "Old Name", WalletType.CASH, "EUR");

        created.setName("New Name");
        created.setType(WalletType.INVESTMENT);
        created.setCurrency("USD");

        Wallet updated = walletDAO.update(created);

        assertEquals("New Name", updated.getName());
        assertEquals(WalletType.INVESTMENT, updated.getType());
        assertEquals("USD", updated.getCurrency());
    }

    @Test
    void deleteRemovesWallet() throws Exception {
        User user = createTestUser();
        Wallet created = walletDAO.create(user.getId(), "To Delete", WalletType.CASH, "EUR");

        boolean deleted = walletDAO.delete(created.getId());

        assertTrue(deleted);
        assertNull(walletDAO.findById(created.getId()));
    }

    @Test
    void deleteReturnsFalseForNonexistent() throws Exception {
        boolean deleted = walletDAO.delete(UUID.randomUUID());

        assertFalse(deleted);
    }

    @Test
    void countTransactionsReturnsZeroForNewWallet() throws Exception {
        User user = createTestUser();
        Wallet wallet = walletDAO.create(user.getId(), "Empty", WalletType.CASH, "EUR");

        int count = walletDAO.countTransactions(wallet.getId());

        assertEquals(0, count);
    }
}
