package it.unipd.dei.webapp.unit.service;

import it.unipd.dei.webapp.dao.WalletDAO;
import it.unipd.dei.webapp.model.Forecast;
import it.unipd.dei.webapp.model.ForecastScenario;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.WalletType;
import it.unipd.dei.webapp.service.ForecastService;
import it.unipd.dei.webapp.service.PacPlanService;
import it.unipd.dei.webapp.service.TransactionService;
import it.unipd.dei.webapp.service.UserService;
import it.unipd.dei.webapp.service.WalletService;
import it.unipd.dei.webapp.service.exception.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForecastServiceTest {

    private ForecastService forecastService;
    private UserService userService;
    private WalletService walletService;
    private TransactionService transactionService;
    private PacPlanService pacPlanService;
    private WalletDAO walletDAO;

    @BeforeEach
    void setUp() {
        forecastService = new ForecastService();
        userService = new UserService();
        walletService = new WalletService();
        transactionService = new TransactionService();
        pacPlanService = new PacPlanService();
        walletDAO = new WalletDAO();
    }

    private User createTestUser() throws Exception {
        String email = "junit-forecast-" + System.currentTimeMillis() + "@test.com";
        return userService.register("Forecast", "Tester", email, "Secret123!");
    }

    @Test
    void computeWithNullScenarioThrowsValidation() throws Exception {
        User user = createTestUser();

        assertThrows(ValidationException.class, () ->
                forecastService.compute(user.getId(), null));
    }

    @Test
    void computeOptimisticReturns24Months() throws Exception {
        User user = createTestUser();

        Forecast forecast = forecastService.compute(user.getId(), ForecastScenario.OPTIMISTIC);

        assertNotNull(forecast.getId());
        assertEquals(ForecastScenario.OPTIMISTIC, forecast.getScenario());
        assertNotNull(forecast.getProjectionJson());

        JSONArray projection = new JSONArray(forecast.getProjectionJson());
        assertEquals(24, projection.length());
    }

    @Test
    void computeConservativeReturns24Months() throws Exception {
        User user = createTestUser();

        Forecast forecast = forecastService.compute(user.getId(), ForecastScenario.CONSERVATIVE);

        JSONArray projection = new JSONArray(forecast.getProjectionJson());
        assertEquals(24, projection.length());
    }

    @Test
    void computeStressTestReturns24Months() throws Exception {
        User user = createTestUser();

        Forecast forecast = forecastService.compute(user.getId(), ForecastScenario.STRESS_TEST);

        JSONArray projection = new JSONArray(forecast.getProjectionJson());
        assertEquals(24, projection.length());
    }

    @Test
    void projectionEntriesHaveExpectedFields() throws Exception {
        User user = createTestUser();

        Forecast forecast = forecastService.compute(user.getId(), ForecastScenario.OPTIMISTIC);
        JSONArray projection = new JSONArray(forecast.getProjectionJson());
        JSONObject firstMonth = projection.getJSONObject(0);

        assertTrue(firstMonth.has("month"));
        assertTrue(firstMonth.has("income"));
        assertTrue(firstMonth.has("expense"));
        assertTrue(firstMonth.has("pacContribution"));
        assertTrue(firstMonth.has("netFlow"));
        assertTrue(firstMonth.has("balance"));
        assertTrue(firstMonth.has("currency"));
        assertEquals(1, firstMonth.getInt("month"));
    }

    @Test
    void computeWithTransactionsReflectsBalance() throws Exception {
        User user = createTestUser();
        var wallet = walletService.createWallet(user.getId(), "Main", "CASH", "EUR");

        String today = LocalDate.now().toString();
        transactionService.createTransaction(wallet.getId(), user.getId(), "1000", "Salary", today, null);
        transactionService.createTransaction(wallet.getId(), user.getId(), "-300", "Rent", today, null);

        Forecast forecast = forecastService.compute(user.getId(), ForecastScenario.CONSERVATIVE);
        JSONArray projection = new JSONArray(forecast.getProjectionJson());
        JSONObject firstMonth = projection.getJSONObject(0);

        assertNotNull(firstMonth.getBigDecimal("balance"));
        assertEquals("EUR", firstMonth.getString("currency"));
    }

    @Test
    void computeWithMixedWalletCurrenciesThrowsValidation() throws Exception {
        User user = createTestUser();
        walletDAO.create(user.getId(), "EUR Wallet", WalletType.CASH, "EUR");
        walletDAO.create(user.getId(), "USD Wallet", WalletType.BANK_ACCOUNT, "USD");

        assertThrows(ValidationException.class, () ->
                forecastService.compute(user.getId(), ForecastScenario.OPTIMISTIC));
    }

    @Test
    void findLatestReturnsNullForNewUser() throws Exception {
        User user = createTestUser();

        Forecast found = forecastService.findLatest(user.getId(), ForecastScenario.OPTIMISTIC);

        assertNull(found);
    }

    @Test
    void findLatestReturnsMostRecentComputation() throws Exception {
        User user = createTestUser();

        forecastService.compute(user.getId(), ForecastScenario.OPTIMISTIC);
        Forecast second = forecastService.compute(user.getId(), ForecastScenario.OPTIMISTIC);

        Forecast latest = forecastService.findLatest(user.getId(), ForecastScenario.OPTIMISTIC);

        assertNotNull(latest);
        assertEquals(second.getId(), latest.getId());
    }

    @Test
    void findLatestReturnsDifferentScenariosSeparately() throws Exception {
        User user = createTestUser();

        Forecast optimistic = forecastService.compute(user.getId(), ForecastScenario.OPTIMISTIC);
        Forecast stress = forecastService.compute(user.getId(), ForecastScenario.STRESS_TEST);

        Forecast latestOpt = forecastService.findLatest(user.getId(), ForecastScenario.OPTIMISTIC);
        Forecast latestStress = forecastService.findLatest(user.getId(), ForecastScenario.STRESS_TEST);

        assertEquals(optimistic.getId(), latestOpt.getId());
        assertEquals(stress.getId(), latestStress.getId());
    }
}
