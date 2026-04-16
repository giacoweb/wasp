package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.model.AmortizationRow;
import it.unipd.dei.webapp.model.CompoundResult;
import it.unipd.dei.webapp.model.BudgetResult;
import it.unipd.dei.webapp.service.exception.ValidationException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalculatorService {


    public List<AmortizationRow> calculateLoan(double principal, double annualRate, int months) throws ValidationException{

        if (principal <= 0) {
            throw new ValidationException("The capital need to be greater than 0.");
        }

        if (annualRate < 0 || annualRate > 100) {
            throw new ValidationException("The interest need to be between % and 100%.");
        }

        if (months <= 0) {
            throw new ValidationException("The duration need to be greater than 0.");
        }


        double monthlyRate = annualRate / 12 / 100;
        double monthlyPayment = principal * (monthlyRate * Math.pow(1 + monthlyRate, months)) / (Math.pow(1 + monthlyRate, months) - 1);

        List<AmortizationRow> schedule = new ArrayList<>();
        double balance = principal;

        for (int i = 1; i <= months; i++) {
            double interest = balance * monthlyRate;
            double principalPaid = monthlyPayment - interest;
            balance -= principalPaid;
            schedule.add(new AmortizationRow(i, monthlyPayment, principalPaid, interest, Math.max(0, balance)));
        }
        return schedule;
    }


    public List<CompoundResult> calculateCompound(double initialCapital, double monthlyContribution, double annualRate, int years) throws ValidationException {

        if (initialCapital <=0){
            throw new ValidationException("The capital need to be greater than 0.");
        }

        if (monthlyContribution < 0) {
            throw new ValidationException("The monthly contribution need to be positive.");
        }

        if (annualRate < 0 || annualRate > 100) {
            throw new ValidationException("The annual rate need to be between 0% and 100%.");
        }

        if (years <= 0) {
            throw new ValidationException("The duration of investment need to be at least 1 year.");
        }

        List<CompoundResult> projection = new ArrayList<>();
        double monthlyRate = annualRate / 12 / 100;
        double currentBalance = initialCapital;

        for (int y = 1; y <= years; y++) {

            for (int m = 1; m <= 12; m++) {

                currentBalance = (currentBalance + monthlyContribution) * (1 + monthlyRate);
            }

            projection.add(new CompoundResult(y, currentBalance));

        }


        return projection;
    }

    public BudgetResult calculateBudget(String[] incomeCategories,double[] incomeAmounts,String[] expenseCategories,double[] expenseAmounts) throws ValidationException{

        if (incomeCategories == null || incomeCategories.length == 0) {
            throw new ValidationException("At least one income item is required.");
        }
        if (expenseCategories == null || expenseCategories.length == 0) {
            throw new ValidationException("At least one expense item is required.");
        }
        if (incomeCategories.length != incomeAmounts.length) {
            throw new ValidationException("Income categories and amounts must match.");
        }
        if (expenseCategories.length != expenseAmounts.length) {
            throw new ValidationException("Expense categories and amounts must match.");
        }

        Map<String, Double> incomeByCategory = new LinkedHashMap<>();
        double totalIncome = 0;

        for (int i = 0; i < incomeCategories.length; i++) {
            String category = incomeCategories[i].trim();
            double amount = incomeAmounts[i];

            if(amount < 0){
                throw new ValidationException("Income amount for '" + category + "' must be positive.");
            }

            incomeByCategory.merge(category,amount, Double::sum);
            totalIncome += amount;
        }

        Map<String, Double> expensesByCategory = new LinkedHashMap<>();
        double totalExpenses = 0;

        for (int i = 0; i < expenseCategories.length; i++) {
            String category = expenseCategories[i].trim();
            double amount = expenseAmounts[i];

            if (amount <= 0) {
                throw new ValidationException("Expense amount for '" + category + "' must be positive.");
            }

            expensesByCategory.merge(category,amount,Double::sum);
            totalExpenses += amount;

        }


        return new BudgetResult(totalIncome,totalExpenses,incomeByCategory,expensesByCategory);

    }

}
