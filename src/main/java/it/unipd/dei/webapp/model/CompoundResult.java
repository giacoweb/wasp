package it.unipd.dei.webapp.model;

import java.math.BigDecimal;

public class CompoundResult {
    private final int year;
    private final BigDecimal totalValue;

    public CompoundResult(int year, BigDecimal totalValue) {
        this.year       = year;
        this.totalValue = totalValue;
    }

    public int getYear()              { return year; }
    public BigDecimal getTotalValue() { return totalValue; }
}
