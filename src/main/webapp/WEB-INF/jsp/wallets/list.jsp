<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.model.WalletSummary" %>
<%@ page import="it.unipd.dei.webapp.model.WalletType" %>
<%@ page import="it.unipd.dei.webapp.model.User" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("authenticatedUser");
    List<WalletSummary> wallets = (List<WalletSummary>) request.getAttribute("wallets");
    String errorMessage   = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    String ctxPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wallets &middot; FinTrack</title>

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
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/wallets.css">
</head>
<body>

<div class="app-shell">

    <!-- Sidebar -->
    <jsp:include page="/WEB-INF/jsp/common/_sidebar.jsp">
        <jsp:param name="navActive" value="wallets"/>
    </jsp:include>

    <!-- Main Content -->
    <main class="app-main">

        <nav class="breadcrumb">
            <a href="<%= ctxPath %>/dashboard">FinTrack</a> / <span>Wallets</span>
        </nav>

        <div style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:var(--space-3); margin-bottom:var(--space-6);">
            <div>
                <h1 style="margin:0 0 var(--space-1);">Wallets</h1>
                <p class="muted" style="margin:0;">Manage your accounts and balances.</p>
            </div>
            <button class="btn btn-primary" id="btn-add-wallet">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                New wallet
            </button>
        </div>

        <% if (errorMessage != null) { %>
            <div class="alert alert-error" style="margin-bottom:var(--space-4);">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <%= HtmlUtil.escape(errorMessage) %>
            </div>
        <% } %>
        <% if (successMessage != null) { %>
            <div class="alert alert-success" style="margin-bottom:var(--space-4);">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                <%= HtmlUtil.escape(successMessage) %>
            </div>
        <% } %>

        <!-- Toolbar + Table -->
        <div class="card" style="padding:var(--space-5);">
            <div class="tx-toolbar">
                <div class="form-input-group" style="flex:1; min-width:200px;">
                    <svg class="form-input-group__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                    <input class="form-input" id="wallet-search" type="search" placeholder="Search wallets...">
                </div>
                <div class="filter-tabs">
                    <button class="filter-tab active" data-filter="ALL">All</button>
                    <button class="filter-tab" data-filter="CASH">Cash</button>
                    <button class="filter-tab" data-filter="BANK">Bank</button>
                    <button class="filter-tab" data-filter="CREDIT_CARD">Credit Card</button>
                    <button class="filter-tab" data-filter="INVESTMENT">Investment</button>
                </div>
            </div>

            <div class="table-wrapper">
                <table class="table table--striped" id="wallets-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Currency</th>
                            <th class="num">Balance</th>
                            <th class="actions"></th>
                        </tr>
                    </thead>
                    <tbody id="wallets-body">
                    <% if (wallets == null || wallets.isEmpty()) { %>
                        <tr><td colspan="5" class="table-empty">No wallets yet. Create your first wallet.</td></tr>
                    <% } else { for (WalletSummary wallet : wallets) {
                        String typeName = wallet.getType().name();
                        String typeLabel;
                        String badgeClass;
                        switch (typeName) {
                            case "CASH":        typeLabel = "Cash";        badgeClass = "badge-wallet-cash";        break;
                            case "BANK":        typeLabel = "Bank";        badgeClass = "badge-wallet-bank";        break;
                            case "CREDIT_CARD": typeLabel = "Credit Card"; badgeClass = "badge-wallet-credit-card"; break;
                            case "INVESTMENT":  typeLabel = "Investment";  badgeClass = "badge-wallet-investment";  break;
                            default:            typeLabel = typeName;      badgeClass = "badge-primary";            break;
                        }
                        boolean isNegative = wallet.getBalance() != null && wallet.getBalance().signum() < 0;
                        String balanceClass = isNegative ? "amount-neg" : "amount-pos";
                        String balanceSign  = isNegative ? "" : "+";
                    %>
                        <tr data-type="<%= typeName %>" data-name="<%= HtmlUtil.escape(wallet.getName().toLowerCase()) %>">
                            <td><strong><%= HtmlUtil.escape(wallet.getName()) %></strong></td>
                            <td><span class="badge <%= badgeClass %>"><%= typeLabel %></span></td>
                            <td><%= HtmlUtil.escape(wallet.getCurrency()) %></td>
                            <td class="num">
                                <span class="<%= balanceClass %>">
                                    <%= balanceSign %><%= wallet.getBalance() != null ? String.format("%,.2f", wallet.getBalance()) : "0.00" %>
                                </span>
                            </td>
                            <td class="actions">
                                <button class="btn btn-icon btn-ghost btn-sm" title="Edit"
                                        onclick="Wallets.openEdit('<%= wallet.getId() %>','<%= HtmlUtil.escape(wallet.getName()) %>','<%= typeName %>','<%= HtmlUtil.escape(wallet.getCurrency()) %>')">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                                </button>
                                <form action="<%= ctxPath %>/wallets/delete" method="post" style="display:inline;"
                                      onsubmit="return confirm('Delete wallet \'<%= HtmlUtil.escape(wallet.getName()) %>\'? This cannot be undone.')">
                                    <input type="hidden" name="walletId" value="<%= wallet.getId() %>">
                                    <button type="submit" class="btn btn-icon btn-ghost btn-sm" title="Delete">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="color:var(--color-negative)"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>
                                    </button>
                                </form>
                            </td>
                        </tr>
                    <% } } %>
                    </tbody>
                </table>
            </div>
        </div>

    </main>
</div>

<!-- Create Wallet Modal -->
<div id="create-modal-overlay" class="modal-overlay" style="display:none;">
    <div class="card modal-card">
        <div class="card-header" style="margin-bottom:var(--space-5);">
            <h2 class="card-title">Create wallet</h2>
            <button class="btn btn-icon btn-ghost" id="create-modal-close" type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>
        <form action="<%= ctxPath %>/wallets/create" method="post" class="form">
            <div class="form-field">
                <label class="form-label form-required" for="create-name">Name</label>
                <input class="form-input" id="create-name" name="name" type="text" maxlength="80" required placeholder="e.g. Main checking">
            </div>
            <div class="form-field">
                <label class="form-label form-required">Type</label>
                <div class="wallet-type-toggle">
                    <button type="button" class="wallet-type-btn active" data-value="CASH">Cash</button>
                    <button type="button" class="wallet-type-btn" data-value="BANK">Bank</button>
                    <button type="button" class="wallet-type-btn" data-value="CREDIT_CARD">Credit</button>
                    <button type="button" class="wallet-type-btn" data-value="INVESTMENT">Invest</button>
                </div>
                <input type="hidden" id="create-type" name="type" value="CASH">
            </div>
            <div class="form-field">
                <label class="form-label form-required" for="create-currency">Currency</label>
                <input class="form-input" id="create-currency" name="currency" type="text" maxlength="3" required placeholder="e.g. EUR — max 3 characters">
            </div>
            <div style="display:flex; gap:var(--space-3); justify-content:flex-end; margin-top:var(--space-4);">
                <button type="button" class="btn btn-ghost" id="create-modal-cancel">Cancel</button>
                <button type="submit" class="btn btn-primary">Create wallet</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Wallet Modal -->
<div id="edit-modal-overlay" class="modal-overlay" style="display:none;">
    <div class="card modal-card">
        <div class="card-header" style="margin-bottom:var(--space-5);">
            <h2 class="card-title">Edit wallet</h2>
            <button class="btn btn-icon btn-ghost" id="edit-modal-close" type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>
        <form action="<%= ctxPath %>/wallets/edit" method="post" class="form">
            <input type="hidden" id="edit-wallet-id" name="walletId">
            <div class="form-field">
                <label class="form-label form-required" for="edit-name">Name</label>
                <input class="form-input" id="edit-name" name="name" type="text" maxlength="80" required>
            </div>
            <div class="form-field">
                <label class="form-label form-required">Type</label>
                <div class="wallet-type-toggle">
                    <button type="button" class="wallet-type-btn" data-value="CASH">Cash</button>
                    <button type="button" class="wallet-type-btn" data-value="BANK">Bank</button>
                    <button type="button" class="wallet-type-btn" data-value="CREDIT_CARD">Credit</button>
                    <button type="button" class="wallet-type-btn" data-value="INVESTMENT">Invest</button>
                </div>
                <input type="hidden" id="edit-type" name="type" value="CASH">
            </div>
            <div class="form-field">
                <label class="form-label form-required" for="edit-currency">Currency</label>
                <input class="form-input" id="edit-currency" name="currency" type="text" maxlength="3" required placeholder="e.g. EUR">
            </div>
            <div style="display:flex; gap:var(--space-3); justify-content:flex-end; margin-top:var(--space-4);">
                <button type="button" class="btn btn-ghost" id="edit-modal-cancel">Cancel</button>
                <button type="submit" class="btn btn-primary">Save changes</button>
            </div>
        </form>
    </div>
</div>

<script src="<%= ctxPath %>/static/js/wallets.js"></script>
</body>
</html>
