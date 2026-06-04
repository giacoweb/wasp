<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wallets &middot; FinTrack</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${ctx}/static/css/tokens.css">
    <link rel="stylesheet" href="${ctx}/static/css/base.css">
    <link rel="stylesheet" href="${ctx}/static/css/layout.css">
    <link rel="stylesheet" href="${ctx}/static/css/components.css">
    <link rel="stylesheet" href="${ctx}/static/css/tables.css">
    <link rel="stylesheet" href="${ctx}/static/css/forms.css">
    <link rel="stylesheet" href="${ctx}/static/css/dashboard.css">
    <link rel="stylesheet" href="${ctx}/static/css/wallets.css">
</head>
<body>

<div class="app-shell">

    <jsp:include page="/WEB-INF/jsp/common/_sidebar.jsp">
        <jsp:param name="navActive" value="wallets"/>
    </jsp:include>

    <main class="app-main">

        <nav class="breadcrumb">
            <a href="${ctx}/dashboard">FinTrack</a> / <span>Wallets</span>
        </nav>

        <div class="page-header">
            <div>
                <h1>Wallets</h1>
                <p class="muted">Manage your accounts and balances.</p>
            </div>
            <button class="btn btn-primary" id="btn-add-wallet">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                New wallet
            </button>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                <c:out value="${successMessage}"/>
            </div>
        </c:if>

        <div class="card card--padded">
            <div class="tx-toolbar">
                <div class="form-input-group">
                    <svg class="form-input-group__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                    <input class="form-input" id="wallet-search" type="search" placeholder="Search wallets...">
                </div>
                <div class="filter-tabs">
                    <button class="filter-tab active" data-filter="ALL" aria-pressed="true">All</button>
                    <button class="filter-tab" data-filter="CASH" aria-pressed="false">Cash</button>
                    <button class="filter-tab" data-filter="BANK_ACCOUNT" aria-pressed="false">Bank</button>
                    <button class="filter-tab" data-filter="CREDIT_CARD" aria-pressed="false">Credit Card</button>
                    <button class="filter-tab" data-filter="INVESTMENT" aria-pressed="false">Investment</button>
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
                        <c:choose>
                            <c:when test="${empty wallets}">
                                <tr><td colspan="5" class="table-empty">No wallets yet. Create your first wallet.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="wallet" items="${wallets}">
                                    <c:set var="typeName" value="${wallet.type}"/>
                                    <c:choose>
                                        <c:when test="${typeName eq 'CASH'}">
                                            <c:set var="typeLabel"  value="Cash"/>
                                            <c:set var="badgeClass" value="badge-wallet-cash"/>
                                        </c:when>
                                        <c:when test="${typeName eq 'BANK_ACCOUNT'}">
                                            <c:set var="typeLabel"  value="Bank"/>
                                            <c:set var="badgeClass" value="badge-wallet-bank"/>
                                        </c:when>
                                        <c:when test="${typeName eq 'CREDIT_CARD'}">
                                            <c:set var="typeLabel"  value="Credit Card"/>
                                            <c:set var="badgeClass" value="badge-wallet-credit-card"/>
                                        </c:when>
                                        <c:when test="${typeName eq 'INVESTMENT'}">
                                            <c:set var="typeLabel"  value="Investment"/>
                                            <c:set var="badgeClass" value="badge-wallet-investment"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="typeLabel"  value="${typeName}"/>
                                            <c:set var="badgeClass" value="badge-primary"/>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:set var="isNegative" value="${wallet.balance != null and wallet.balance lt 0}"/>
                                    <tr data-type="${typeName}" data-name="<c:out value='${fn:toLowerCase(wallet.name)}'/>">
                                        <td><strong><c:out value="${wallet.name}"/></strong></td>
                                        <td><span class="badge ${badgeClass}">${typeLabel}</span></td>
                                        <td><c:out value="${wallet.currency}"/></td>
                                        <td class="num">
                                            <span class="${isNegative ? 'amount-neg' : 'amount-pos'}">
                                                ${isNegative ? '' : '+'}<fmt:formatNumber value="${wallet.balance != null ? wallet.balance : 0}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                                            </span>
                                        </td>
                                        <td class="actions">
                                            <button class="btn btn-icon btn-ghost btn-sm js-edit-wallet" title="Edit"
                                                    data-id="${wallet.id}"
                                                    data-name="<c:out value='${wallet.name}'/>"
                                                    data-type="${typeName}"
                                                    data-currency="<c:out value='${wallet.currency}'/>">
                                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                                            </button>
                                            <form action="${ctx}/wallets/delete" method="post" class="form--inline js-delete-wallet"
                                                  data-name="<c:out value='${wallet.name}'/>">
                                                <input type="hidden" name="walletId" value="${wallet.id}">
                                                <button type="submit" class="btn btn-icon btn-ghost btn-sm" title="Delete">
                                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="icon--danger"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

    </main>
</div>

<div id="create-modal-overlay" class="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="create-modal-title" hidden>
    <div class="card modal-card">
        <div class="card-header">
            <h2 id="create-modal-title" class="card-title">Create wallet</h2>
            <button class="btn btn-icon btn-ghost" id="create-modal-close" type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>
        <form action="${ctx}/wallets/create" method="post" class="form">
            <div class="form-field">
                <label class="form-label form-required" for="create-name">Name</label>
                <input class="form-input" id="create-name" name="name" type="text" maxlength="80" required placeholder="e.g. Main checking">
            </div>
            <div class="form-field">
                <label class="form-label form-required">Type</label>
                <div class="wallet-type-toggle">
                    <button type="button" class="wallet-type-btn active" data-value="CASH" aria-pressed="true">Cash</button>
                    <button type="button" class="wallet-type-btn" data-value="BANK_ACCOUNT" aria-pressed="false">Bank</button>
                    <button type="button" class="wallet-type-btn" data-value="CREDIT_CARD" aria-pressed="false">Credit</button>
                    <button type="button" class="wallet-type-btn" data-value="INVESTMENT" aria-pressed="false">Invest</button>
                </div>
                <input type="hidden" id="create-type" name="type" value="CASH">
            </div>
            <div class="form-field">
                <label class="form-label form-required" for="create-currency">Currency</label>
                <input class="form-input" id="create-currency" name="currency" type="text" maxlength="3" minlength="3" pattern="[A-Za-z]{3}" required placeholder="e.g. EUR" title="Three-letter ISO currency code, e.g. EUR">
            </div>
            <div class="modal__footer">
                <button type="button" class="btn btn-ghost" id="create-modal-cancel">Cancel</button>
                <button type="submit" class="btn btn-primary">Create wallet</button>
            </div>
        </form>
    </div>
</div>

<div id="edit-modal-overlay" class="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="edit-modal-title" hidden>
    <div class="card modal-card">
        <div class="card-header">
            <h2 id="edit-modal-title" class="card-title">Edit wallet</h2>
            <button class="btn btn-icon btn-ghost" id="edit-modal-close" type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>
        <form action="${ctx}/wallets/edit" method="post" class="form">
            <input type="hidden" id="edit-wallet-id" name="walletId">
            <div class="form-field">
                <label class="form-label form-required" for="edit-name">Name</label>
                <input class="form-input" id="edit-name" name="name" type="text" maxlength="80" required>
            </div>
            <div class="form-field">
                <label class="form-label form-required">Type</label>
                <div class="wallet-type-toggle">
                    <button type="button" class="wallet-type-btn" data-value="CASH" aria-pressed="false">Cash</button>
                    <button type="button" class="wallet-type-btn" data-value="BANK_ACCOUNT" aria-pressed="false">Bank</button>
                    <button type="button" class="wallet-type-btn" data-value="CREDIT_CARD" aria-pressed="false">Credit</button>
                    <button type="button" class="wallet-type-btn" data-value="INVESTMENT" aria-pressed="false">Invest</button>
                </div>
                <input type="hidden" id="edit-type" name="type" value="CASH">
            </div>
            <div class="form-field">
                <label class="form-label form-required" for="edit-currency">Currency</label>
                <input class="form-input" id="edit-currency" name="currency" type="text" maxlength="3" minlength="3" pattern="[A-Za-z]{3}" required placeholder="e.g. EUR" title="Three-letter ISO currency code, e.g. EUR">
            </div>
            <div class="modal__footer">
                <button type="button" class="btn btn-ghost" id="edit-modal-cancel">Cancel</button>
                <button type="submit" class="btn btn-primary">Save changes</button>
            </div>
        </form>
    </div>
</div>

<script src="${ctx}/static/js/wallets.js"></script>
</body>
</html>
