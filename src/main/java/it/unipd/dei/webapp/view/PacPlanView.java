package it.unipd.dei.webapp.view;

import it.unipd.dei.webapp.model.PacPlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class PacPlanView {

    private static final DateTimeFormatter MONTH_YEAR_FMT = DateTimeFormatter.ofPattern("MMM yyyy");

    private final PacPlan plan;
    private final String monthlyFormatted;
    private final String targetFormatted;
    private final String savedFormatted;
    private final String pctDisplay;
    private final int progressPct;
    private final String startedLabel;

    public PacPlanView(final PacPlan plan, final LocalDate today) {
        this.plan = plan;

        long monthsSince = plan.getStartDate() != null
                ? Math.max(0, ChronoUnit.MONTHS.between(plan.getStartDate(), today))
                : 0;

        BigDecimal saved = plan.getMonthlyAmount() != null
                ? plan.getMonthlyAmount().multiply(BigDecimal.valueOf(monthsSince))
                : BigDecimal.ZERO;

        BigDecimal target = plan.getTargetAmount() != null
                ? plan.getTargetAmount() : BigDecimal.ZERO;

        BigDecimal pct = BigDecimal.ZERO;
        if (target.signum() > 0) {
            pct = saved.multiply(BigDecimal.valueOf(100))
                    .divide(target, 1, RoundingMode.HALF_UP);
            if (pct.compareTo(BigDecimal.valueOf(100)) > 0) {
                pct = BigDecimal.valueOf(100);
            }
            if (saved.compareTo(target) > 0) {
                saved = target;
            }
        }

        this.monthlyFormatted = plan.getMonthlyAmount() != null
                ? String.format("%,.2f", plan.getMonthlyAmount()) : "0.00";
        this.targetFormatted = String.format("%,.2f", target);
        this.savedFormatted = String.format("%,.2f", saved);

        String pctText = pct.stripTrailingZeros().toPlainString();
        if (pctText.startsWith(".")) {
            pctText = "0" + pctText;
        }
        this.pctDisplay = pctText;
        this.progressPct = pct.intValue();

        this.startedLabel = plan.getStartDate() != null
                ? plan.getStartDate().format(MONTH_YEAR_FMT) : "—";
    }

    public PacPlan getPlan() { return plan; }
    public String getMonthlyFormatted() { return monthlyFormatted; }
    public String getTargetFormatted() { return targetFormatted; }
    public String getSavedFormatted() { return savedFormatted; }
    public String getPctDisplay() { return pctDisplay; }
    public int getProgressPct() { return progressPct; }
    public String getStartedLabel() { return startedLabel; }
}
