package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.PacPlanDAO;
import it.unipd.dei.webapp.model.PacPlan;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public class PacPlanService {

    private static final int MAX_ASSET_NAME_LENGTH = 120;

    private final PacPlanDAO pacPlanDAO;

    public PacPlanService() {
        this.pacPlanDAO = new PacPlanDAO();
    }

    public PacPlanService(final PacPlanDAO pacPlanDAO) {
        this.pacPlanDAO = pacPlanDAO;
    }

    public List<PacPlan> getPlansByUser(final UUID userId) throws SQLException {
        return pacPlanDAO.findByUserId(userId);
    }

    public PacPlan createPlan(final UUID userId,
                              final String assetName,
                              final String monthlyAmountStr,
                              final String targetAmountStr,
                              final String startDateStr)
            throws ValidationException, SQLException {

        validateAssetName(assetName);
        BigDecimal monthlyAmount = parsePositiveAmount(monthlyAmountStr, "Monthly amount");
        BigDecimal targetAmount = parsePositiveAmount(targetAmountStr, "Target amount");
        LocalDate startDate = parseDate(startDateStr);

        PacPlan plan = new PacPlan();
        plan.setUserId(userId);
        plan.setAssetName(assetName.trim());
        plan.setMonthlyAmount(monthlyAmount);
        plan.setTargetAmount(targetAmount);
        plan.setStartDate(startDate);
        plan.setActive(true);

        return pacPlanDAO.create(plan);
    }

    public PacPlan updatePlan(final UUID planId,
                              final UUID userId,
                              final String assetName,
                              final String monthlyAmountStr,
                              final String targetAmountStr,
                              final String startDateStr,
                              final String activeStr)
            throws ValidationException, UnauthorizedOperationException, SQLException {

        PacPlan existing = pacPlanDAO.findById(planId);

        if (existing == null) {
            throw new ValidationException("PAC plan not found.");
        }

        if (!existing.getUserId().equals(userId)) {
            throw new UnauthorizedOperationException();
        }

        validateAssetName(assetName);
        BigDecimal monthlyAmount = parsePositiveAmount(monthlyAmountStr, "Monthly amount");
        BigDecimal targetAmount = parsePositiveAmount(targetAmountStr, "Target amount");
        LocalDate startDate = parseDate(startDateStr);
        boolean active = parseActive(activeStr);

        existing.setAssetName(assetName.trim());
        existing.setMonthlyAmount(monthlyAmount);
        existing.setTargetAmount(targetAmount);
        existing.setStartDate(startDate);
        existing.setActive(active);

        return pacPlanDAO.update(existing);
    }

    public void deletePlan(final UUID planId, final UUID userId)
            throws ValidationException, UnauthorizedOperationException, SQLException {

        PacPlan existing = pacPlanDAO.findById(planId);

        if (existing == null) {
            throw new ValidationException("PAC plan not found.");
        }

        if (!existing.getUserId().equals(userId)) {
            throw new UnauthorizedOperationException();
        }

        pacPlanDAO.delete(planId);
    }

    private void validateAssetName(final String assetName) throws ValidationException {
        if (assetName == null || assetName.isBlank()) {
            throw new ValidationException("Asset name cannot be empty.");
        }

        if (assetName.trim().length() > MAX_ASSET_NAME_LENGTH) {
            throw new ValidationException("Asset name must not exceed " + MAX_ASSET_NAME_LENGTH + " characters.");
        }
    }

    private BigDecimal parsePositiveAmount(final String amountStr, final String fieldName) throws ValidationException {
        if (amountStr == null || amountStr.isBlank()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr.trim()).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException(fieldName + " must be greater than zero.");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid " + fieldName.toLowerCase() + " format.");
        }
    }

    private LocalDate parseDate(final String dateStr) throws ValidationException {
        if (dateStr == null || dateStr.isBlank()) {
            throw new ValidationException("Start date cannot be empty.");
        }

        try {
            return LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    private boolean parseActive(final String activeStr) {
        if (activeStr == null) {
            return true;
        }
        return Boolean.parseBoolean(activeStr.trim());
    }
}
