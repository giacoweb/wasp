<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.model.DashboardStats" %>
<%@ page import="it.unipd.dei.webapp.model.DashboardSummary" %>
<%@ page import="it.unipd.dei.webapp.model.WalletSummary" %>
<%@ page import="it.unipd.dei.webapp.model.User" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("authenticatedUser");
    DashboardSummary summary = (DashboardSummary) request.getAttribute("summary");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String ctxPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FinTrack - Dashboard</title>
    
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    
    <!-- Design System CSS -->
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/tokens.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/base.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/layout.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/components.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/tables.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/forms.css">

    <link rel="stylesheet" href="<%= ctxPath %>/static/css/dashboard.css">
</head>
<body>

<div class="app-shell">
    
    <!-- Sidebar -->
    <aside class="app-sidebar">
        <a href="<%= ctxPath %>/dashboard" class="brand">
            <span class="brand-logo">▲</span> FinTrack
        </a>
        
        <nav style="margin-top: var(--space-8);">
            <ul class="nav-menu">
                <li>
                    <a href="<%= ctxPath %>/dashboard" class="nav-item active">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="7" height="9" x="3" y="3" rx="1"/><rect width="7" height="5" x="14" y="3" rx="1"/><rect width="7" height="9" x="14" y="12" rx="1"/><rect width="7" height="5" x="3" y="16" rx="1"/></svg>
                        Dashboard
                    </a>
                </li>
                <li>
                    <a href="<%= ctxPath %>/wallets" class="nav-item">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/><path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/><path d="M18 12a2 2 0 0 0 0 4h4v-4Z"/></svg>
                        Wallets
                    </a>
                </li>
                <li>
                    <a href="<%= ctxPath %>/transactions" class="nav-item">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m21 16-4 4-4-4"/><path d="M17 20V4"/><path d="m3 8 4-4 4 4"/><path d="M7 4v16"/></svg>
                        Transactions
                    </a>
                </li>
                <li>
                    <a href="<%= ctxPath %>/pac" class="nav-item">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/><polyline points="16 7 22 7 22 13"/></svg>
                        PAC Plans
                    </a>
                </li>
                <li>
                    <a href="<%= ctxPath %>/expenses" class="nav-item">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/></svg>
                        Recurring Exp.
                    </a>
                </li>
                <li>
                    <a href="<%= ctxPath %>/report/export" class="nav-item">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21.21 15.89A10 10 0 1 1 8 2.83"/><path d="M22 12A10 10 0 0 0 12 2v10z"/></svg>
                        Forecast / Reports
                    </a>
                </li>
            </ul>
        </nav>
        
        <div class="user-profile">
            <div class="user-avatar"><%= user.getName() != null && !user.getName().isEmpty() ? user.getName().substring(0, 1).toUpperCase() : "U" %></div>
            <div class="user-info">
                <span class="user-name"><%= HtmlUtil.escape(user.getName()) %> <%= HtmlUtil.escape(user.getSurname()) %></span>
                <span class="user-email"><%= HtmlUtil.escape(user.getEmail()) %></span>
            </div>
            
            <form action="<%= ctxPath %>/auth/logout" method="post" style="margin-left: auto;">
                <button type="submit" class="btn btn-icon btn-ghost" title="Logout" style="width: 24px; height: 24px;">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"/></svg>
                </button>
            </form>
        </div>
    </aside>

    <!-- Main Content -->
    <main class="app-main">
        <% if (errorMessage != null) { %>
            <div class="alert alert-error" style="margin-bottom: var(--space-4);">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <%= HtmlUtil.escape(errorMessage) %>
            </div>
        <% } %>

        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-8);">
            <h1 style="margin: 0;">Hi, <%= HtmlUtil.escape(user.getName()) %> 👋</h1>
        </div>

        <% if (summary != null) { %>
            
            <!-- KPI Cards -->
            <div class="grid grid--3">
                <% 
                    List<DashboardStats> statsList = summary.getStatsByCurrency();
                    if (statsList != null && !statsList.isEmpty()) {
                        for (DashboardStats stats : statsList) {
                %>
                    <!-- Total Equity -->
                    <div class="card">
                        <div class="card-subtitle">Total Equity (<%= HtmlUtil.escape(stats.currency()) %>)</div>
                        <div class="card-title" style="font-size: var(--font-size-3xl); margin: var(--space-2) 0;">
                            <%= HtmlUtil.escape(stats.currency()) %> <%= String.format("%,.2f", stats.totalEquity()) %>
                        </div>
                        <span class="badge badge-income">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:16px;height:16px;"><path d="M5 12l5 5L20 7"/></svg>
                            Up to date
                        </span>
                    </div>

                    <!-- YTD Expenses -->
                    <div class="card">
                        <div class="card-subtitle">YTD Expenses (<%= HtmlUtil.escape(stats.currency()) %>)</div>
                        <div class="card-title" style="font-size: var(--font-size-3xl); margin: var(--space-2) 0;">
                            <%= HtmlUtil.escape(stats.currency()) %> <%= String.format("%,.2f", stats.ytdExpenses()) %>
                        </div>
                        <span class="badge badge-expense">
                            YTD
                        </span>
                    </div>

                    <!-- YTD Savings -->
                    <div class="card">
                        <div class="card-subtitle">YTD Savings (<%= HtmlUtil.escape(stats.currency()) %>)</div>
                        <div class="card-title" style="font-size: var(--font-size-3xl); margin: var(--space-2) 0;">
                            <%= HtmlUtil.escape(stats.currency()) %> <%= String.format("%,.2f", stats.ytdSavings()) %>
                        </div>
                        <span class="badge badge-income">
                            YTD
                        </span>
                    </div>
                <% 
                        }
                    } else { 
                %>
                    <div class="card"><p class="muted" style="margin:0;">No financial data yet.</p></div>
                <% } %>
            </div>

            <!-- Wallets Section -->
            <div class="card-header" style="margin-top: var(--space-10);">
                <h2 class="card-title">Your wallets</h2>
                <a href="<%= ctxPath %>/wallets" class="btn btn-primary">+ New wallet</a>
            </div>

            <div class="grid grid--3">
                <% 
                    List<WalletSummary> wallets = summary.getWallets();
                    if (wallets != null && !wallets.isEmpty()) {
                        for (WalletSummary wallet : wallets) { 
                            String badgeClass = "badge-primary";
                            String typeLabel = wallet.getType().name();
                            switch (wallet.getType().name()) {
                                case "CASH": badgeClass = "badge-wallet-cash"; typeLabel = "Cash"; break;
                                case "BANK": badgeClass = "badge-wallet-bank"; typeLabel = "Bank"; break;
                                case "CREDIT_CARD": badgeClass = "badge-wallet-credit-card"; typeLabel = "Credit Card"; break;
                                case "INVESTMENT": badgeClass = "badge-wallet-investment"; typeLabel = "Investment"; break;
                            }
                %>
                    <div class="card">
                        <div style="margin-bottom: var(--space-2);">
                            <span class="badge <%= badgeClass %>"><%= typeLabel %></span>
                            <span class="muted" style="font-size: var(--font-size-xs); margin-left: 4px;"><%= HtmlUtil.escape(wallet.getCurrency()) %></span>
                        </div>
                        <div class="card-title" style="font-size: var(--font-size-base);"><%= HtmlUtil.escape(wallet.getName()) %></div>
                        <div class="card-title" style="font-size: var(--font-size-xl); margin-top: var(--space-2);">
                            <% if (wallet.getBalance().signum() < 0) { %>
                                <span class="amount-negative" style="color: var(--color-danger);"><%= String.format("%,.2f", wallet.getBalance()) %></span>
                            <% } else { %>
                                <%= String.format("%,.2f", wallet.getBalance()) %>
                            <% } %>
                        </div>
                    </div>
                <%      }
                    } else { 
                %>
                    <div class="card" style="grid-column: 1 / -1; text-align: center;">
                        <p class="muted">No wallets yet.</p>
                    </div>
                <% } %>
            </div>

            <!-- Recent Transactions Section -->
            <div class="card-header" style="margin-top: var(--space-10);">
                <h2 class="card-title">Recent transactions</h2>
                <a href="<%= ctxPath %>/transactions" class="btn btn-link">View all &rarr;</a>
            </div>

            <div class="table-wrapper">
                <table class="table table--striped">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Category</th>
                            <th>Wallet</th>
                            <th class="num">Amount</th>
                        </tr>
                    </thead>
                    <tbody id="recent-tx-body">
                        <tr><td colspan="4" class="table-empty">Loading...</td></tr>
                    </tbody>
                </table>
            </div>

        <% } %>
    </main>
</div>

<script>
window.CTX_PATH = "<%= ctxPath %>";
window.WALLET_IDS = [
    <% if (summary != null && summary.getWallets() != null) {
        List<WalletSummary> ws = summary.getWallets();
        for (int i = 0; i < ws.size(); i++) { %>
    { id: "<%= ws.get(i).getId() %>", name: "<%= HtmlUtil.escape(ws.get(i).getName()) %>" }<%= i < ws.size() - 1 ? "," : "" %>
    <% } } %>
];
</script>
<script src="<%= ctxPath %>/static/js/dashboard.js"></script>

</body>
</html>