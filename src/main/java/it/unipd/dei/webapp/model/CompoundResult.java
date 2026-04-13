package it.unipd.dei.webapp.model;

public class CompoundResult {

    private final int year;
    private final double totalValue;
    public CompoundResult(int year, double totalValue) {
        this.year = year;
        this.totalValue = totalValue;
    }
    public int getYear() { return year; }
    public double getTotalValue() { return totalValue; }

}
