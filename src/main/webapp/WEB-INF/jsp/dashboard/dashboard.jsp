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
    <jsp:include page="/WEB-INF/jsp/common/_sidebar.jsp">
        <jsp:param name="navActive" value="dashboard"/>
    </jsp:include>

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
            <a href="<%= ctxPath %>/report/export" class="btn btn-ghost" style="gap:var(--space-2);">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
                Download report
            </a>
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
                                <span style="color: var(--color-danger);"><%= String.format("%,.2f", wallet.getBalance()) %></span>
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
            <div id="tx-footer" style="display:none; justify-content:space-between; align-items:center; margin-top:var(--space-4); padding-top:var(--space-3); border-top:1px solid var(--color-border);">
                <span class="muted" id="tx-count" style="font-size:var(--font-size-sm);"></span>
                <a href="<%= ctxPath %>/transactions" style="font-size:var(--font-size-sm); color:var(--color-primary); text-decoration:none; font-weight:500;">View all transactions &rarr;</a>
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