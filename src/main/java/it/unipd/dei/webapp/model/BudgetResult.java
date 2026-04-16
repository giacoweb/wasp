package it.unipd.dei.webapp.model;

import java.util.List;
import java.util.Map;

public class BudgetResult {

    private final double totalIncome;
    private final double totalExpenses;
    private final double netBalance;
    private final Map<String, Double> incomeByCategory;
    private final Map<String, Double> expensesByCategory;

    public BudgetResult(double totalIncome, double totalExpenses,
                        Map<String, Double> incomeByCategory,
                        Map<String, Double> expensesByCategory) {

        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netBalance = totalIncome - totalExpenses;
        this.incomeByCategory = incomeByCategory;
        this.expensesByCategory = expensesByCategory;
    }

    public double getTotalIncome()                  { return totalIncome; }
    public double getTotalExpenses()                { return totalExpenses; }
    public double getNetBalance()                   { return netBalance; }
    public Map<String, Double> getIncomeByCategory()   { return incomeByCategory; }
    public Map<String, Double> getExpensesByCategory() { return expensesByCategory; }

}
