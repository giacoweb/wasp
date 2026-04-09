package it.unipd.dei.webapp.unit.dao;

import it.unipd.dei.webapp.dao.ForecastDAO;
import it.unipd.dei.webapp.dao.UserDAO;
import it.unipd.dei.webapp.model.Forecast;
import it.unipd.dei.webapp.model.ForecastScenario;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ForecastDAOTest {

    private ForecastDAO forecastDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        forecastDAO = new ForecastDAO();
        userDAO = new UserDAO();
    }

    private User createTestUser() throws Exception {
        User user = new User();
        user.setName("Forecast");
        user.setSurname("DAOTest");
        user.setEmail("junit-fdao-" + System.currentTimeMillis() + "@test.com");
        user.setPasswordHash(PasswordUtil.hashPassword("secret123"));
        user.setRole(UserRole.REGISTERED);
        return userDAO.createUser(user);
    }

    private Forecast buildTestForecast(UUID userId, ForecastScenario scenario) {
        Forecast forecast = new Forecast();
        forecast.setUserId(userId);
        forecast.setScenario(scenario);
        forecast.setProjectionJson("[{\"month\":1,\"balance\":1000.00}]");
        return forecast;
    }

    @Test
    void saveReturnsForecastWithGeneratedId() throws Exception {
        User user = createTestUser();
        Forecast forecast = buildTestForecast(user.getId(), ForecastScenario.OPTIMISTIC);

        Forecast saved = forecastDAO.save(forecast);

        assertNotNull(saved.getId());
        assertEquals(ForecastScenario.OPTIMISTIC, saved.getScenario());
        assertNotNull(saved.getProjectionJson());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void findLatestByUserAndScenarioReturnsCorrect() throws Exception {
        User user = createTestUser();

        forecastDAO.save(buildTestForecast(user.getId(), ForecastScenario.OPTIMISTIC));
        Forecast second = forecastDAO.save(buildTestForecast(user.getId(), ForecastScenario.OPTIMISTIC));

        Forecast latest = forecastDAO.findLatestByUserAndScenario(user.getId(), ForecastScenario.OPTIMISTIC);

        assertNotNull(latest);
        assertEquals(second.getId(), latest.getId());
    }

    @Test
    void findLatestByUserAndScenarioReturnsNullForNonexistent() throws Exception {
        User user = createTestUser();

        Forecast found = forecastDAO.findLatestByUserAndScenario(user.getId(), ForecastScenario.STRESS_TEST);

        assertNull(found);
    }

    @Test
    void findLatestByUserAndScenarioSeparatesScenarios() throws Exception {
        User user = createTestUser();

        Forecast optimistic = forecastDAO.save(buildTestForecast(user.getId(), ForecastScenario.OPTIMISTIC));
        Forecast stress = forecastDAO.save(buildTestForecast(user.getId(), ForecastScenario.STRESS_TEST));

        Forecast latestOpt = forecastDAO.findLatestByUserAndScenario(user.getId(), ForecastScenario.OPTIMISTIC);
        Forecast latestStress = forecastDAO.findLatestByUserAndScenario(user.getId(), ForecastScenario.STRESS_TEST);

        assertEquals(optimistic.getId(), latestOpt.getId());
        assertEquals(stress.getId(), latestStress.getId());
    }

    @Test
    void findLatestByUserAndScenarioReturnsNullForDifferentUser() throws Exception {
        User user = createTestUser();
        User other = createTestUser();

        forecastDAO.save(buildTestForecast(user.getId(), ForecastScenario.OPTIMISTIC));

        Forecast found = forecastDAO.findLatestByUserAndScenario(other.getId(), ForecastScenario.OPTIMISTIC);

        assertNull(found);
    }
}
