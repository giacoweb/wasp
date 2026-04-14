package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.model.AmortizationRow;
import it.unipd.dei.webapp.model.CompoundResult;
import it.unipd.dei.webapp.service.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class CalculatorService {


    public List<AmortizationRow> calculateLoan(double principal, double annualRate, int months) throws ValidationException{

        if (principal <= 0) {
            throw new ValidationException("loan_error_principal"); //The capital need to be greater than 0.
        }

        if (annualRate < 0 || annualRate > 100) {
            throw new ValidationException("loan_error_annualRate"); //The interest need to be between 0 and 100.
        }

        if (months <= 0) {
            throw new ValidationException("loan_error_durations"); //The duration need to be greater than 0.
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
            throw new ValidationException("The initial capital need to be greater than 0.");
        }

        if (monthlyContribution < 0) {
            throw new ValidationException("The monthly contribution need to be positive.");
        }

        if (annualRate < 0 || annualRate > 100) {
            throw new ValidationException("The annual rate need to be between 0% and 100%,");
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


}
