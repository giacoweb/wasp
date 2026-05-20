<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.model.User" %>
<%@ page import="it.unipd.dei.webapp.model.Wallet" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("authenticatedUser");
    List<Wallet> wallets = (List<Wallet>) request.getAttribute("wallets");
    String ctxPath = request.getContextPath();
    String currency = (wallets != null && !wallets.isEmpty()) ? wallets.get(0).getCurrency() : "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transactions &middot; FinTrack</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="<%= ctxPath %>/static/css/tokens.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/base.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/layout.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/components.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/tables.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/forms.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/dashboard.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/transactions.css">
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
                    <a href="<%= ctxPath %>/dashboard" class="nav-item">
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
                    <a href="<%= ctxPath %>/transactions" class="nav-item active">
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
                <button type="submit" class="btn btn-icon btn-ghost" title="Logout" style="width:24px;height:24px;">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"/></svg>
                </button>
            </form>
        </div>
    </aside>

    <!-- Main Content -->
    <main class="app-main">

        <nav class="breadcrumb">
            <a href="<%= ctxPath %>/dashboard">FinTrack</a> / <span>Transactions</span>
        </nav>

        <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:var(--space-6);">
            <div>
                <h1 style="margin:0 0 var(--space-1);">Transactions</h1>
                <p class="muted" style="margin:0;">Track and manage your financial activity.</p>
            </div>
            <button class="btn btn-primary" id="btn-add" <%= (wallets == null || wallets.isEmpty()) ? "disabled" : "" %>>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                New transaction
            </button>
        </div>

        <% if (wallets == null || wallets.isEmpty()) { %>
            <div class="card" style="text-align:center; padding:var(--space-10);">
                <p class="muted" style="margin:0;">No wallets yet. <a href="<%= ctxPath %>/wallets">Create one first.</a></p>
            </div>
        <% } else { %>

            <!-- KPI Cards -->
            <div class="grid grid--3" style="margin-bottom:var(--space-8);">
                <div class="card">
                    <div class="kpi-icon kpi-icon--income">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
                    </div>
                    <div class="card-subtitle">Total Income</div>
                    <div class="kpi-amount kpi-amount--income" id="kpi-income">—</div>
                    <p class="muted" style="margin:0; font-size:var(--font-size-xs);">Across all wallets</p>
                </div>
                <div class="card">
                    <div class="kpi-icon kpi-icon--expense">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="7" y1="7" x2="17" y2="17"/><polyline points="17 7 17 17 7 17"/></svg>
                    </div>
                    <div class="card-subtitle">Total Expenses</div>
                    <div class="kpi-amount kpi-amount--expense" id="kpi-expenses">—</div>
                    <p class="muted" style="margin:0; font-size:var(--font-size-xs);">Across all wallets</p>
                </div>
                <div class="card">
                    <div class="kpi-icon kpi-icon--flow">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/><polyline points="16 7 22 7 22 13"/></svg>
                    </div>
                    <div class="card-subtitle">Net Flow</div>
                    <div class="kpi-amount" id="kpi-flow">—</div>
                    <p class="muted" id="kpi-flow-label" style="margin:0; font-size:var(--font-size-xs);">—</p>
                </div>
            </div>

            <!-- Toolbar + Table -->
            <div class="card" style="padding:var(--space-5);">
                <div class="tx-toolbar">
                    <div class="form-input-group" style="flex:1; min-width:200px;">
                        <svg class="form-input-group__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                        <input class="form-input" id="search-input" type="search" placeholder="Search transactions...">
                    </div>
                    <div class="filter-tabs">
                        <button class="filter-tab active" data-filter="ALL">All</button>
                        <button class="filter-tab" data-filter="INCOME">Income</button>
                        <button class="filter-tab" data-filter="EXPENSE">Expense</button>
                    </div>
                </div>

                <div class="table-wrapper">
                    <table class="table table--striped">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Description</th>
                                <th>Category</th>
                                <th>Wallet</th>
                                <th class="num">Amount</th>
                                <th class="actions"></th>
                            </tr>
                        </thead>
                        <tbody id="transactions-body">
                            <tr><td colspan="6" class="table-empty">Loading...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>

        <% } %>
    </main>
</div>

<!-- Add / Edit Modal -->
<div id="modal-overlay" style="display:none; position:fixed; inset:0; background:rgba(15,23,42,0.4); z-index:var(--z-modal); align-items:center; justify-content:center;">
    <div class="card" style="width:100%; max-width:500px; box-shadow:var(--shadow-modal);">

        <div class="card-header" style="margin-bottom:var(--space-5);">
            <h2 class="card-title" id="modal-title">Create transaction</h2>
            <button class="btn btn-icon btn-ghost" id="modal-close" type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>

        <form id="transaction-form" class="form" novalidate>
            <input type="hidden" id="form-id">

            <!-- Type toggle -->
            <div class="form-field">
                <label class="form-label form-required">Type</label>
                <div class="type-toggle" id="type-toggle">
                    <button type="button" class="type-toggle__btn active" data-value="INCOME">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
                        Income
                    </button>
                    <button type="button" class="type-toggle__btn" data-value="EXPENSE">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="7" y1="7" x2="17" y2="17"/><polyline points="17 7 17 17 7 17"/></svg>
                        Expense
                    </button>
                </div>
                <input type="hidden" id="form-type" value="INCOME">
            </div>

            <div class="form-grid-2">
                <div class="form-field">
                    <label class="form-label form-required" for="form-amount">Amount</label>
                    <input class="form-input" id="form-amount" type="number" step="0.01" min="0.01" required placeholder="e.g. 120.50">
                    <span class="form-error"></span>
                </div>
                <div class="form-field">
                    <label class="form-label form-required" for="form-date">Date</label>
                    <input class="form-input" id="form-date" type="date" required>
                    <span class="form-error"></span>
                </div>
            </div>

            <div class="form-field">
                <label class="form-label form-required" for="form-category">Description</label>
                <input class="form-input" id="form-category" type="text" maxlength="80" required placeholder="e.g. Grocery shopping">
                <span class="form-error"></span>
            </div>

            <div class="form-field">
                <label class="form-label form-required" for="form-wallet">Wallet</label>
                <select class="form-select" id="form-wallet" required>
                    <% if (wallets != null) { for (Wallet wallet : wallets) { %>
                        <option value="<%= wallet.getId() %>"><%= HtmlUtil.escape(wallet.getName()) %> (<%= HtmlUtil.escape(wallet.getCurrency()) %>)</option>
                    <% } } %>
                </select>
            </div>

            <div class="form-field">
                <label class="form-label" for="form-note">Notes</label>
                <textarea class="form-textarea" id="form-note" placeholder="Optional details about this transaction"></textarea>
            </div>

            <div id="form-error" class="alert alert-error" style="display:none;"></div>

            <div style="display:flex; gap:var(--space-3); justify-content:flex-end;">
                <button type="button" class="btn btn-ghost" id="modal-cancel">Cancel</button>
                <button type="submit" class="btn btn-primary" id="form-submit">Create transaction</button>
            </div>
        </form>
    </div>
</div>

<script>
window.CTX_PATH   = "<%= ctxPath %>";
window.CURRENCY   = "<%= HtmlUtil.escape(currency) %>";
window.WALLET_IDS = [
    <% if (wallets != null) {
        for (int i = 0; i < wallets.size(); i++) { %>
    { id: "<%= wallets.get(i).getId() %>", name: "<%= HtmlUtil.escape(wallets.get(i).getName()) %>" }<%= i < wallets.size() - 1 ? "," : "" %>
    <% } } %>
];
</script>
<script src="<%= ctxPath %>/static/js/transactions.js"></script>

</body>
</html>
