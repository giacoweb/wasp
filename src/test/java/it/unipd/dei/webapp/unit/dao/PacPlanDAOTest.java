package it.unipd.dei.webapp.unit.dao;

import it.unipd.dei.webapp.dao.PacPlanDAO;
import it.unipd.dei.webapp.dao.UserDAO;
import it.unipd.dei.webapp.model.PacPlan;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacPlanDAOTest {

    private PacPlanDAO pacPlanDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        pacPlanDAO = new PacPlanDAO();
        userDAO = new UserDAO();
    }

    private User createTestUser() throws Exception {
        User user = new User();
        user.setName("Pac");
        user.setSurname("DAOTest");
        user.setEmail("junit-pdao-" + System.currentTimeMillis() + "@test.com");
        user.setPasswordHash(PasswordUtil.hashPassword("secret123"));
        user.setRole(UserRole.REGISTERED);
        return userDAO.createUser(user);
    }

    private PacPlan buildTestPlan(UUID userId) {
        PacPlan plan = new PacPlan();
        plan.setUserId(userId);
        plan.setAssetName("VWCE ETF");
        plan.setMonthlyAmount(new BigDecimal("200.00"));
        plan.setTargetAmount(new BigDecimal("50000.00"));
        plan.setStartDate(LocalDate.of(2026, 1, 1));
        plan.setActive(true);
        return plan;
    }

    @Test
    void createReturnsPlanWithGeneratedId() throws Exception {
        User user = createTestUser();
        PacPlan plan = buildTestPlan(user.getId());

        PacPlan created = pacPlanDAO.create(plan);

        assertNotNull(created.getId());
        assertEquals("VWCE ETF", created.getAssetName());
        assertEquals(0, created.getMonthlyAmount().compareTo(new BigDecimal("200.00")));
        assertTrue(created.isActive());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    void findByUserIdReturnsAllPlans() throws Exception {
        User user = createTestUser();

        pacPlanDAO.create(buildTestPlan(user.getId()));
        PacPlan second = buildTestPlan(user.getId());
        second.setAssetName("S&P 500");
        pacPlanDAO.create(second);

        List<PacPlan> plans = pacPlanDAO.findByUserId(user.getId());

        assertEquals(2, plans.size());
    }

    @Test
    void findByUserIdReturnsEmptyForUnknownUser() throws Exception {
        List<PacPlan> plans = pacPlanDAO.findByUserId(UUID.randomUUID());

        assertTrue(plans.isEmpty());
    }

    @Test
    void findByIdReturnsCorrectPlan() throws Exception {
        User user = createTestUser();
        PacPlan created = pacPlanDAO.create(buildTestPlan(user.getId()));

        PacPlan found = pacPlanDAO.findById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void findByIdReturnsNullForNonexistent() throws Exception {
        PacPlan found = pacPlanDAO.findById(UUID.randomUUID());

        assertNull(found);
    }

    @Test
    void findActiveByUserIdReturnsOnlyActive() throws Exception {
        User user = createTestUser();

        pacPlanDAO.create(buildTestPlan(user.getId()));
        PacPlan inactive = buildTestPlan(user.getId());
        inactive.setAssetName("Inactive");
        PacPlan createdInactive = pacPlanDAO.create(inactive);
        createdInactive.setActive(false);
        pacPlanDAO.update(createdInactive);

        List<PacPlan> active = pacPlanDAO.findActiveByUserId(user.getId());

        assertEquals(1, active.size());
        assertEquals("VWCE ETF", active.get(0).getAssetName());
    }

    @Test
    void updateModifiesPlanFields() throws Exception {
        User user = createTestUser();
        PacPlan created = pacPlanDAO.create(buildTestPlan(user.getId()));

        created.setAssetName("Updated ETF");
        created.setMonthlyAmount(new BigDecimal("500.00"));
        created.setActive(false);

        PacPlan updated = pacPlanDAO.update(created);

        assertEquals("Updated ETF", updated.getAssetName());
        assertEquals(0, updated.getMonthlyAmount().compareTo(new BigDecimal("500.00")));
        assertFalse(updated.isActive());
    }

    @Test
    void deleteRemovesPlan() throws Exception {
        User user = createTestUser();
        PacPlan created = pacPlanDAO.create(buildTestPlan(user.getId()));

        boolean deleted = pacPlanDAO.delete(created.getId());

        assertTrue(deleted);
        assertNull(pacPlanDAO.findById(created.getId()));
    }

    @Test
    void deleteReturnsFalseForNonexistent() throws Exception {
        boolean deleted = pacPlanDAO.delete(UUID.randomUUID());

        assertFalse(deleted);
    }
}
