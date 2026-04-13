package it.unipd.dei.webapp.model;

public class AmortizationRow {

    private final int month;
    private final double totalPayment;
    private final double principalPaid;
    private final double interestPaid;
    private final double remainingBalance;

    public AmortizationRow(int month, double totalPayment, double principalPaid,
                           double interestPaid, double remainingBalance)
    {
        this.month=month;
        this.totalPayment=totalPayment;
        this.principalPaid=principalPaid;
        this.interestPaid=interestPaid;
        this.remainingBalance=remainingBalance;
    }

    public int getMonth() { return month; }
    public double getTotalPayment() { return totalPayment; }
    public double getPrincipalPaid() { return principalPaid; }
    public double getInterestPaid() { return interestPaid; }
    public double getRemainingBalance() { return remainingBalance; }

}
