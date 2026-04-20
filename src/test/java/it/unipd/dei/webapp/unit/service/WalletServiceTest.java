package it.unipd.dei.webapp.unit.service;

import it.unipd.dei.webapp.dao.WalletDAO;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.model.WalletType;
import it.unipd.dei.webapp.service.UserService;
import it.unipd.dei.webapp.service.WalletService;
import it.unipd.dei.webapp.service.exception.SingleCurrencyRequiredException;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import it.unipd.dei.webapp.service.exception.WalletHasTransactionsException;
import it.unipd.dei.webapp.service.exception.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WalletServiceTest {

    private WalletService walletService;
    private UserService userService;
    private WalletDAO walletDAO;

    @BeforeEach
    void setUp() {
        walletService = new WalletService();
        userService = new UserService();
        walletDAO = new WalletDAO();
    }

    private User createTestUser() throws Exception {
        String email = "junit-wallet-" + System.currentTimeMillis() + "@test.com";
        return userService.register("Wallet", "Tester", email, "secret123");
    }

    @Test
    void createWalletWithNullNameThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                walletService.createWallet(UUID.randomUUID(), null, "CASH", "EUR"));
    }

    @Test
    void createWalletWithBlankNameThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                walletService.createWallet(UUID.randomUUID(), "", "CASH", "EUR"));
    }

    @Test
    void createWalletWithNullTypeThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                walletService.createWallet(UUID.randomUUID(), "My Wallet", null, "EUR"));
    }

    @Test
    void createWalletWithInvalidTypeThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                walletService.createWallet(UUID.randomUUID(), "My Wallet", "INVALID", "EUR"));
    }

    @Test
    void createWalletWithNullCurrencyThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                walletService.createWallet(UUID.randomUUID(), "My Wallet", "CASH", null));
    }

    @Test
    void createWalletWithInvalidCurrencyLengthThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                walletService.createWallet(UUID.randomUUID(), "My Wallet", "CASH", "EU"));
    }

    @Test
    void createWalletSucceeds() throws Exception {
        User user = createTestUser();

        Wallet wallet = walletService.createWallet(user.getId(), "Test Wallet", "CASH", "EUR");

        assertNotNull(wallet.getId());
        assertEquals(user.getId(), wallet.getUserId());
        assertEquals("Test Wallet", wallet.getName());
        assertEquals("EUR", wallet.getCurrency());
        assertNotNull(wallet.getCreatedAt());
    }

    @Test
    void getWalletsByUserReturnsCreatedWallets() throws Exception {
        User user = createTestUser();

        walletService.createWallet(user.getId(), "Wallet A", "CASH", "EUR");
        walletService.createWallet(user.getId(), "Wallet B", "BANK_ACCOUNT", "EUR");

        List<Wallet> wallets = walletService.getWalletsByUser(user.getId());

        assertEquals(2, wallets.size());
    }

    @Test
    void getWalletsByUserReturnsEmptyForNewUser() throws Exception {
        User user = createTestUser();

        List<Wallet> wallets = walletService.getWalletsByUser(user.getId());

        assertTrue(wallets.isEmpty());
    }

    @Test
    void updateWalletSucceeds() throws Exception {
        User user = createTestUser();
        Wallet wallet = walletService.createWallet(user.getId(), "Old Name", "CASH", "EUR");

        Wallet updated = walletService.updateWallet(
                wallet.getId(), user.getId(), "New Name", "BANK_ACCOUNT", "USD");

        assertEquals("New Name", updated.getName());
        assertEquals("USD", updated.getCurrency());
    }

    @Test
    void createSecondWalletWithDifferentCurrencyThrowsValidation() throws Exception {
        User user = createTestUser();
        walletService.createWallet(user.getId(), "Wallet A", "CASH", "EUR");

        assertThrows(SingleCurrencyRequiredException.class, () ->
                walletService.createWallet(user.getId(), "Wallet B", "BANK_ACCOUNT", "USD"));
    }

    @Test
    void updateWalletToDifferentCurrencyWhileAnotherWalletExistsThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet first = walletService.createWallet(user.getId(), "Wallet A", "CASH", "EUR");
        walletService.createWallet(user.getId(), "Wallet B", "BANK_ACCOUNT", "EUR");

        assertThrows(SingleCurrencyRequiredException.class, () ->
                walletService.updateWallet(first.getId(), user.getId(), "Wallet A", "CASH", "USD"));
    }

    @Test
    void updateLegacyWalletToMatchRemainingCurrencySucceeds() throws Exception {
        User user = createTestUser();
        Wallet eurWallet = walletDAO.create(user.getId(), "Legacy EUR", WalletType.CASH, "EUR");
        walletDAO.create(user.getId(), "Legacy USD", WalletType.BANK_ACCOUNT, "USD");

        Wallet updated = walletService.updateWallet(
                eurWallet.getId(), user.getId(), "Legacy EUR", "CASH", "USD");

        assertEquals("USD", updated.getCurrency());
    }

    @Test
    void updateWalletByDifferentUserThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        Wallet wallet = walletService.createWallet(owner.getId(), "My Wallet", "CASH", "EUR");

        User otherUser = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                walletService.updateWallet(wallet.getId(), otherUser.getId(), "Stolen", "CASH", "EUR"));
    }

    @Test
    void updateNonexistentWalletThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                walletService.updateWallet(UUID.randomUUID(), UUID.randomUUID(), "Name", "CASH", "EUR"));
    }

    @Test
    void deleteWalletSucceeds() throws Exception {
        User user = createTestUser();
        Wallet wallet = walletService.createWallet(user.getId(), "To Delete", "CASH", "EUR");

        walletService.deleteWallet(wallet.getId(), user.getId());

        List<Wallet> wallets = walletService.getWalletsByUser(user.getId());
        assertTrue(wallets.isEmpty());
    }

    @Test
    void deleteWalletByDifferentUserThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        Wallet wallet = walletService.createWallet(owner.getId(), "My Wallet", "CASH", "EUR");

        User otherUser = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                walletService.deleteWallet(wallet.getId(), otherUser.getId()));
    }

    @Test
    void deleteNonexistentWalletThrowsNotFound() {
        assertThrows(WalletNotFoundException.class, () ->
                walletService.deleteWallet(UUID.randomUUID(), UUID.randomUUID()));
    }
}
