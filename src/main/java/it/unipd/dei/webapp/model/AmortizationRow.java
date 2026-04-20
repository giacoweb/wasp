package it.unipd.dei.webapp.model;

import java.math.BigDecimal;

public class AmortizationRow {

    private final int month;
    private final BigDecimal totalPayment;
    private final BigDecimal principalPaid;
    private final BigDecimal interestPaid;
    private final BigDecimal remainingBalance;

    public AmortizationRow(int month, BigDecimal totalPayment,
                           BigDecimal principalPaid, BigDecimal interestPaid,
                           BigDecimal remainingBalance) {
        this.month            = month;
        this.totalPayment     = totalPayment;
        this.principalPaid    = principalPaid;
        this.interestPaid     = interestPaid;
        this.remainingBalance = remainingBalance;
    }

    public int getMonth()                   { return month; }
    public BigDecimal getTotalPayment()     { return totalPayment; }
    public BigDecimal getPrincipalPaid()    { return principalPaid; }
    public BigDecimal getInterestPaid()     { return interestPaid; }
    public BigDecimal getRemainingBalance() { return remainingBalance; }

}
