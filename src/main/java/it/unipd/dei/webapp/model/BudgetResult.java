package it.unipd.dei.webapp.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BudgetResult {

    private final BigDecimal totalIncome;
    private final BigDecimal totalExpenses;
    private final BigDecimal netBalance;
    private final Map<String, BigDecimal> incomeByCategory;
    private final Map<String, BigDecimal> expensesByCategory;

    public BudgetResult(BigDecimal totalIncome,
                        BigDecimal totalExpenses,
                        Map<String, BigDecimal> incomeByCategory,
                        Map<String, BigDecimal> expensesByCategory) {
        this.totalIncome       = totalIncome;
        this.totalExpenses     = totalExpenses;
        this.netBalance        = totalIncome.subtract(totalExpenses);
        // Defensive copies — il chiamante non può modificare le mappe interne
        this.incomeByCategory   = Collections.unmodifiableMap(incomeByCategory);
        this.expensesByCategory = Collections.unmodifiableMap(expensesByCategory);
    }

    public BigDecimal getTotalIncome()                        { return totalIncome; }
    public BigDecimal getTotalExpenses()                      { return totalExpenses; }
    public BigDecimal getNetBalance()                         { return netBalance; }
    public Map<String, BigDecimal> getIncomeByCategory()      { return incomeByCategory; }
    public Map<String, BigDecimal> getExpensesByCategory()    { return expensesByCategory; }

}
