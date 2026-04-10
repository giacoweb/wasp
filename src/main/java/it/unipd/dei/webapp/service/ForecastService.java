package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.ForecastDAO;
import it.unipd.dei.webapp.dao.PacPlanDAO;
import it.unipd.dei.webapp.dao.RecurringExpenseDAO;
import it.unipd.dei.webapp.dao.TransactionDAO;
import it.unipd.dei.webapp.model.Forecast;
import it.unipd.dei.webapp.model.ForecastScenario;
import it.unipd.dei.webapp.model.PacPlan;
import it.unipd.dei.webapp.model.Periodicity;
import it.unipd.dei.webapp.model.RecurringExpense;
import it.unipd.dei.webapp.model.Transaction;
import it.unipd.dei.webapp.model.TransactionType;
import it.unipd.dei.webapp.service.exception.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ForecastService {

    private static final int PROJECTION_MONTHS = 24;
    private static final BigDecimal OPTIMISTIC_INCOME_MODIFIER = new BigDecimal("1.10");
    private static final BigDecimal CONSERVATIVE_INFLATION_RATE = new BigDecimal("1.03");
    private static final BigDecimal STRESS_INCOME_MODIFIER = new BigDecimal("0.80");
    private static final BigDecimal STRESS_EXPENSE_SHOCK_MULTIPLIER = new BigDecimal("3");
    private static final int STRESS_SHOCK_MONTH = 6;

    private final TransactionDAO transactionDAO;
    private final RecurringExpenseDAO recurringExpenseDAO;
    private final PacPlanDAO pacPlanDAO;
    private final ForecastDAO forecastDAO;

    public ForecastService() {
        this.transactionDAO = new TransactionDAO();
        this.recurringExpenseDAO = new RecurringExpenseDAO();
        this.pacPlanDAO = new PacPlanDAO();
        this.forecastDAO = new ForecastDAO();
    }

    public ForecastService(final TransactionDAO transactionDAO,
                           final RecurringExpenseDAO recurringExpenseDAO,
                           final PacPlanDAO pacPlanDAO,
                           final ForecastDAO forecastDAO) {
        this.transactionDAO = transactionDAO;
        this.recurringExpenseDAO = recurringExpenseDAO;
        this.pacPlanDAO = pacPlanDAO;
        this.forecastDAO = forecastDAO;
    }

    public Forecast compute(final UUID userId, final ForecastScenario scenario)
            throws ValidationException, SQLException {

        if (scenario == null) {
            throw new ValidationException("Scenario is required.");
        }

        List<Transaction> allTransactions = transactionDAO.findByUserId(userId);
        BigDecimal currentBalance = computeCurrentBalance(allTransactions);
        BigDecimal avgMonthlyIncome = computeAverageMonthly(allTransactions, TransactionType.INCOME);
        BigDecimal avgMonthlyExpense = computeAverageMonthly(allTransactions, TransactionType.EXPENSE);
        BigDecimal monthlyRecurring = computeMonthlyRecurringExpenses(userId);
        BigDecimal monthlyPac = computeMonthlyPacContributions(userId);

        JSONArray projection = buildProjection(
                scenario, currentBalance, avgMonthlyIncome, avgMonthlyExpense, monthlyRecurring, monthlyPac);

        Forecast forecast = new Forecast();
        forecast.setUserId(userId);
        forecast.setScenario(scenario);
        forecast.setProjectionJson(projection.toString());

        return forecastDAO.save(forecast);
    }

    public Forecast findLatest(final UUID userId, final ForecastScenario scenario) throws SQLException {
        return forecastDAO.findLatestByUserAndScenario(userId, scenario);
    }

    private BigDecimal computeCurrentBalance(final List<Transaction> transactions) {
        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction txn : transactions) {
            if (txn.getType() == TransactionType.INCOME) {
                balance = balance.add(txn.getAmount());
            } else {
                balance = balance.subtract(txn.getAmount());
            }
        }

        return balance.setScale(2, RoundingMode.HALF_UP);
    }

    private static final int AVERAGE_MONTHS_WINDOW = 3;

    private BigDecimal computeAverageMonthly(final List<Transaction> transactions, final TransactionType type) {
        LocalDate now = LocalDate.now();
        LocalDate windowStart = now.minusMonths(AVERAGE_MONTHS_WINDOW);

        BigDecimal total = BigDecimal.ZERO;
        LocalDate earliest = null;

        for (Transaction txn : transactions) {
            if (txn.getType() == type && !txn.getTransactionDate().isBefore(windowStart)) {
                total = total.add(txn.getAmount());
                if (earliest == null || txn.getTransactionDate().isBefore(earliest)) {
                    earliest = txn.getTransactionDate();
                }
            }
        }

        if (earliest == null) {
            return BigDecimal.ZERO;
        }

        long monthsWithData = java.time.temporal.ChronoUnit.MONTHS.between(
                earliest.withDayOfMonth(1), now.withDayOfMonth(1)) + 1;
        long divisor = Math.min(monthsWithData, AVERAGE_MONTHS_WINDOW);

        return total.divide(new BigDecimal(divisor), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal computeMonthlyRecurringExpenses(final UUID userId) throws SQLException {
        List<RecurringExpense> expenses = recurringExpenseDAO.findByUserId(userId);
        BigDecimal monthlyTotal = BigDecimal.ZERO;

        for (RecurringExpense expense : expenses) {
            if (!expense.isActive()) {
                continue;
            }

            BigDecimal monthlyEquivalent = toMonthlyAmount(expense.getAmount(), expense.getPeriodicity());
            monthlyTotal = monthlyTotal.add(monthlyEquivalent);
        }

        return monthlyTotal.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal toMonthlyAmount(final BigDecimal amount, final Periodicity periodicity) {
        switch (periodicity) {
            case MONTHLY:
                return amount;
            case QUARTERLY:
                return amount.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
            case YEARLY:
                return amount.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
            default:
                return amount;
        }
    }

    private BigDecimal computeMonthlyPacContributions(final UUID userId) throws SQLException {
        List<PacPlan> plans = pacPlanDAO.findActiveByUserId(userId);
        BigDecimal total = BigDecimal.ZERO;

        for (PacPlan plan : plans) {
            total = total.add(plan.getMonthlyAmount());
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private JSONArray buildProjection(final ForecastScenario scenario,
                                      final BigDecimal currentBalance,
                                      final BigDecimal avgMonthlyIncome,
                                      final BigDecimal avgMonthlyExpense,
                                      final BigDecimal monthlyRecurring,
                                      final BigDecimal monthlyPac) {

        JSONArray projection = new JSONArray();
        BigDecimal balance = currentBalance;

        for (int month = 1; month <= PROJECTION_MONTHS; month++) {
            BigDecimal income = avgMonthlyIncome;
            BigDecimal expense = avgMonthlyExpense.add(monthlyRecurring);
            BigDecimal pacOutflow = monthlyPac;

            switch (scenario) {
                case OPTIMISTIC:
                    income = income.multiply(OPTIMISTIC_INCOME_MODIFIER);
                    break;
                case CONSERVATIVE:
                    BigDecimal inflationFactor = CONSERVATIVE_INFLATION_RATE.pow(month, MathContext.DECIMAL64);
                    expense = expense.multiply(inflationFactor);
                    break;
                case STRESS_TEST:
                    income = income.multiply(STRESS_INCOME_MODIFIER);
                    if (month == STRESS_SHOCK_MONTH) {
                        BigDecimal shock = avgMonthlyExpense.multiply(STRESS_EXPENSE_SHOCK_MULTIPLIER);
                        expense = expense.add(shock);
                    }
                    break;
            }

            income = income.setScale(2, RoundingMode.HALF_UP);
            expense = expense.setScale(2, RoundingMode.HALF_UP);
            pacOutflow = pacOutflow.setScale(2, RoundingMode.HALF_UP);

            BigDecimal netFlow = income.subtract(expense).subtract(pacOutflow);
            balance = balance.add(netFlow).setScale(2, RoundingMode.HALF_UP);

            JSONObject entry = new JSONObject();
            entry.put("month", month);
            entry.put("income", income);
            entry.put("expense", expense);
            entry.put("pacContribution", pacOutflow);
            entry.put("netFlow", netFlow);
            entry.put("balance", balance);
            projection.put(entry);
        }

        return projection;
    }
}
