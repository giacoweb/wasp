package it.unipd.dei.webapp.model;

import java.math.BigDecimal;

public record DashboardStats(BigDecimal totalEquity, BigDecimal ytdExpenses, BigDecimal ytdSavings) {
}
