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
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FinTrack - Dashboard</title>
    
    <!-- Design System CSS -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/tokens.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/base.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/layout.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/components.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/tables.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/forms.css">

    <style>
        /* App Shell specific to dashboard */
        .app-shell { display: flex; min-height: 100vh; }
        .app-sidebar { 
            width: 260px; 
            border-right: 1px solid var(--color-border); 
            background: var(--color-surface); 
            padding: var(--space-6) var(--space-4); 
            display: flex; 
            flex-direction: column; 
            position: fixed;
            top: 0; bottom: 0; left: 0;
        }
        .app-main { 
            flex: 1; 
            padding: var(--space-8); 
            background: var(--color-bg); 
            margin-left: 260px; /* offset for fixed sidebar */
            min-height: 100vh;
        }
        .nav-menu { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: var(--space-1); }
        .nav-item { 
            display: flex; align-items: center; gap: var(--space-3); 
            padding: var(--space-2) var(--space-3); 
            border-radius: var(--radius-md); 
            color: var(--color-text-muted); 
            font-weight: 500; 
            text-decoration: none; 
            transition: background var(--transition); 
        }
        .nav-item:hover, .nav-item.active { 
            background: var(--color-primary-tint); 
            color: var(--color-primary); 
        }
        
        .user-profile {
            display: flex; align-items: center; gap: var(--space-3);
            padding-top: var(--space-4);
            border-top: 1px solid var(--color-border);
            margin-top: auto;
        }
        .user-avatar {
            width: 36px; height: 36px;
            background: var(--color-primary);
            color: white;
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            font-weight: 600;
            font-size: var(--font-size-sm);
        }
        .user-info { display: flex; flex-direction: column; }
        .user-name { font-size: var(--font-size-sm); font-weight: 600; color: var(--color-text); }
        .user-email { font-size: var(--font-size-xs); color: var(--color-text-muted); }
        
        /* Utility for the header logo */
        .brand-logo {
            display: inline-flex; align-items: center; justify-content: center;
            width: 24px; height: 24px;
            background: var(--color-primary); color: white;
            border-radius: 4px;
            font-size: 14px; font-weight: bold;
        }
    </style>
</head>
<body>

<div class="app-shell">
    
    <!-- Sidebar -->
    <aside class="app-sidebar">
        <a href="<%= request.getContextPath() %>/dashboard" class="brand">
            <span class="brand-logo">▲</span> FinTrack
        </a>
        
        <nav style="margin-top: var(--space-8);">
            <ul class="nav-menu">
                <li><a href="<%= request.getContextPath() %>/dashboard" class="nav-item active">Dashboard</a></li>
                <li><a href="<%= request.getContextPath() %>/wallets" class="nav-item">Wallets</a></li>
                <!-- We don't have a transactions page yet, this is a placeholder link -->
                <li><a href="#" class="nav-item">Transactions</a></li>
                <li><a href="<%= request.getContextPath() %>/pac" class="nav-item">PAC Plans</a></li>
                <li><a href="<%= request.getContextPath() %>/expenses" class="nav-item">Recurring Exp.</a></li>
                <li><a href="<%= request.getContextPath() %>/report/export" class="nav-item">Forecast / Reports</a></li>
            </ul>
        </nav>
        
        <div class="user-profile">
            <div class="user-avatar"><%= user.getName().substring(0, 1).toUpperCase() %></div>
            <div class="user-info">
                <span class="user-name"><%= HtmlUtil.escape(user.getName()) %> <%= HtmlUtil.escape(user.getSurname()) %></span>
                <span class="user-email"><%= HtmlUtil.escape(user.getEmail()) %></span>
            </div>
            
            <!-- Minimal Logout Button -->
            <form action="<%= request.getContextPath() %>/auth/logout" method="post" style="margin-left: auto;">
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
                    DashboardStats primaryStats = null;
                    if (statsList != null && !statsList.isEmpty()) {
                        primaryStats = statsList.get(0); // Take the first currency for the main KPIs
                    }
                %>
                
                <% if (primaryStats != null) { %>
                    <!-- Total Equity -->
                    <div class="card">
                        <div class="card-subtitle">Total Equity</div>
                        <div class="card-title" style="font-size: var(--font-size-3xl); margin: var(--space-2) 0;">
                            <%= HtmlUtil.escape(primaryStats.currency()) %> <%= primaryStats.totalEquity() %>
                        </div>
                        <span class="badge badge-income">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12l5 5L20 7"/></svg>
                            Up to date
                        </span>
                    </div>

                    <!-- YTD Expenses -->
                    <div class="card">
                        <div class="card-subtitle">YTD Expenses</div>
                        <div class="card-title" style="font-size: var(--font-size-3xl); margin: var(--space-2) 0;">
                            <%= HtmlUtil.escape(primaryStats.currency()) %> <%= primaryStats.ytdExpenses() %>
                        </div>
                        <span class="badge badge-expense">
                            YTD
                        </span>
                    </div>

                    <!-- YTD Savings -->
                    <div class="card">
                        <div class="card-subtitle">YTD Savings</div>
                        <div class="card-title" style="font-size: var(--font-size-3xl); margin: var(--space-2) 0;">
                            <%= HtmlUtil.escape(primaryStats.currency()) %> <%= primaryStats.ytdSavings() %>
                        </div>
                        <span class="badge badge-income">
                            YTD
                        </span>
                    </div>
                <% } else { %>
                    <div class="card"><p class="muted" style="margin:0;">No financial data yet.</p></div>
                <% } %>
            </div>

            <!-- Wallets Section -->
            <div class="card-header" style="margin-top: var(--space-10);">
                <h2 class="card-title">Your wallets</h2>
                <a href="<%= request.getContextPath() %>/wallets" class="btn btn-primary">+ New wallet</a>
            </div>

            <div class="grid grid--3">
                <% 
                    List<WalletSummary> wallets = summary.getWallets();
                    if (wallets != null && !wallets.isEmpty()) {
                        for (WalletSummary wallet : wallets) { 
                            String badgeClass = "badge-primary";
                            switch (wallet.getType().name()) {
                                case "CASH": badgeClass = "badge-wallet-cash"; break;
                                case "BANK": badgeClass = "badge-wallet-bank"; break;
                                case "CREDIT_CARD": badgeClass = "badge-wallet-credit-card"; break;
                                case "INVESTMENT": badgeClass = "badge-wallet-investment"; break;
                            }
                %>
                    <div class="card">
                        <div style="margin-bottom: var(--space-2);">
                            <span class="badge <%= badgeClass %>"><%= wallet.getType().name() %></span>
                            <span class="muted" style="font-size: var(--font-size-xs); margin-left: 4px;"><%= HtmlUtil.escape(wallet.getCurrency()) %></span>
                        </div>
                        <div class="card-title" style="font-size: var(--font-size-base);"><%= HtmlUtil.escape(wallet.getName()) %></div>
                        <div class="card-title" style="font-size: var(--font-size-xl); margin-top: var(--space-2);">
                            <% if (wallet.getBalance().signum() < 0) { %>
                                <span class="amount-negative"><%= wallet.getBalance() %></span>
                            <% } else { %>
                                <%= wallet.getBalance() %>
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
                <a href="#" class="btn btn-link">View all &rarr;</a>
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
                    <tbody>
                        <!-- Placeholder row because backend doesn't provide transactions yet -->
                        <tr>
                            <td colspan="4" class="table-empty">
                                No recent transactions to display (Backend integration pending).
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

        <% } %>
    </main>
</div>

</body>
</html>