package it.unipd.dei.webapp.model;

import java.math.BigDecimal;

public record DashboardStats(String currency, BigDecimal totalEquity, BigDecimal ytdExpenses, BigDecimal ytdSavings) {
}
