package it.unipd.dei.webapp.unit.service;

import it.unipd.dei.webapp.model.PacPlan;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.PacPlanService;
import it.unipd.dei.webapp.service.UserService;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacPlanServiceTest {

    private PacPlanService pacPlanService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        pacPlanService = new PacPlanService();
        userService = new UserService();
    }

    private User createTestUser() throws Exception {
        String email = "junit-pac-" + System.currentTimeMillis() + "@test.com";
        return userService.register("Pac", "Tester", email, "secret123");
    }

    @Test
    void createPlanSucceeds() throws Exception {
        User user = createTestUser();

        PacPlan plan = pacPlanService.createPlan(
                user.getId(), "VWCE ETF", "200.00", "50000.00", "2026-01-01");

        assertNotNull(plan.getId());
        assertEquals(user.getId(), plan.getUserId());
        assertEquals("VWCE ETF", plan.getAssetName());
        assertTrue(plan.isActive());
    }

    @Test
    void createPlanWithBlankAssetNameThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                pacPlanService.createPlan(UUID.randomUUID(), "", "200", "50000", "2026-01-01"));
    }

    @Test
    void createPlanWithZeroMonthlyAmountThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                pacPlanService.createPlan(UUID.randomUUID(), "ETF", "0", "50000", "2026-01-01"));
    }

    @Test
    void createPlanWithNegativeTargetThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                pacPlanService.createPlan(UUID.randomUUID(), "ETF", "200", "-100", "2026-01-01"));
    }

    @Test
    void createPlanWithInvalidDateThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                pacPlanService.createPlan(UUID.randomUUID(), "ETF", "200", "50000", "not-a-date"));
    }

    @Test
    void getPlansByUserReturnsCreated() throws Exception {
        User user = createTestUser();

        pacPlanService.createPlan(user.getId(), "Plan A", "100", "10000", "2026-01-01");
        pacPlanService.createPlan(user.getId(), "Plan B", "200", "20000", "2026-02-01");

        List<PacPlan> plans = pacPlanService.getPlansByUser(user.getId());

        assertEquals(2, plans.size());
    }

    @Test
    void getPlansByUserReturnsEmptyForNewUser() throws Exception {
        User user = createTestUser();

        List<PacPlan> plans = pacPlanService.getPlansByUser(user.getId());

        assertTrue(plans.isEmpty());
    }

    @Test
    void updatePlanSucceeds() throws Exception {
        User user = createTestUser();
        PacPlan plan = pacPlanService.createPlan(
                user.getId(), "Old Name", "100", "10000", "2026-01-01");

        PacPlan updated = pacPlanService.updatePlan(
                plan.getId(), user.getId(), "New Name", "300", "60000", "2026-06-01", "true");

        assertEquals("New Name", updated.getAssetName());
    }

    @Test
    void updatePlanByOtherUserThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        PacPlan plan = pacPlanService.createPlan(
                owner.getId(), "My Plan", "100", "10000", "2026-01-01");

        User other = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                pacPlanService.updatePlan(
                        plan.getId(), other.getId(), "Stolen", "999", "99999", "2026-01-01", "true"));
    }

    @Test
    void updateNonexistentPlanThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                pacPlanService.updatePlan(
                        UUID.randomUUID(), UUID.randomUUID(), "Name", "100", "10000", "2026-01-01", "true"));
    }

    @Test
    void deletePlanSucceeds() throws Exception {
        User user = createTestUser();
        PacPlan plan = pacPlanService.createPlan(
                user.getId(), "To Delete", "100", "10000", "2026-01-01");

        pacPlanService.deletePlan(plan.getId(), user.getId());

        List<PacPlan> plans = pacPlanService.getPlansByUser(user.getId());
        assertTrue(plans.isEmpty());
    }

    @Test
    void deletePlanByOtherUserThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        PacPlan plan = pacPlanService.createPlan(
                owner.getId(), "My Plan", "100", "10000", "2026-01-01");

        User other = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                pacPlanService.deletePlan(plan.getId(), other.getId()));
    }

    @Test
    void deleteNonexistentPlanThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                pacPlanService.deletePlan(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void createPlanWithAssetNameExceedingLimitThrowsValidation() {
        String longName = "A".repeat(121);
        assertThrows(ValidationException.class, () ->
                pacPlanService.createPlan(UUID.randomUUID(), longName, "200", "50000", "2026-01-01"));
    }
}
