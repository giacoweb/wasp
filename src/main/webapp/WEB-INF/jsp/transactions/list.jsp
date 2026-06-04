<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transactions &middot; FinTrack</title>

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
    <link rel="stylesheet" href="${ctx}/static/css/transactions.css">
</head>
<body>

<div class="app-shell">

    <jsp:include page="/WEB-INF/jsp/common/_sidebar.jsp">
        <jsp:param name="navActive" value="transactions"/>
    </jsp:include>

    <main class="app-main">

        <nav class="breadcrumb">
            <a href="${ctx}/dashboard">FinTrack</a> / <span>Transactions</span>
        </nav>

        <div class="page-header">
            <div>
                <h1>Transactions</h1>
                <p class="muted">Track and manage your financial activity.</p>
            </div>
            <button class="btn btn-primary" id="btn-add" ${empty wallets ? 'disabled' : ''}>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                New transaction
            </button>
        </div>

        <c:choose>
            <c:when test="${empty wallets}">
                <div class="card card--empty">
                    <p class="muted">No wallets yet. <a href="${ctx}/wallets">Create one first.</a></p>
                </div>
            </c:when>
            <c:otherwise>

                <div id="kpi-section" class="kpi-section"></div>

                <div class="card card--padded">
                    <div class="tx-toolbar">
                        <div class="form-input-group">
                            <svg class="form-input-group__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                            <input class="form-input" id="search-input" type="search" placeholder="Search transactions...">
                        </div>
                        <div class="filter-tabs">
                            <button class="filter-tab active" data-filter="ALL" aria-pressed="true">All</button>
                            <button class="filter-tab" data-filter="INCOME" aria-pressed="false">Income</button>
                            <button class="filter-tab" data-filter="EXPENSE" aria-pressed="false">Expense</button>
                        </div>
                    </div>

                    <div class="table-wrapper">
                        <table class="table table--striped">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Description</th>
                                    <th>Type</th>
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

            </c:otherwise>
        </c:choose>
    </main>
</div>

<div id="modal-overlay" class="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="modal-title" hidden>
    <div class="card modal-card">

        <div class="card-header">
            <h2 class="card-title" id="modal-title">Create transaction</h2>
            <button class="btn btn-icon btn-ghost" id="modal-close" type="button">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
        </div>

        <form id="transaction-form" class="form" novalidate>
            <input type="hidden" id="form-id">

            <div class="form-field">
                <label class="form-label form-required">Type</label>
                <div class="type-toggle" id="type-toggle">
                    <button type="button" class="type-toggle__btn active" data-value="INCOME" aria-pressed="true">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
                        Income
                    </button>
                    <button type="button" class="type-toggle__btn" data-value="EXPENSE" aria-pressed="false">
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
                    <c:forEach var="wallet" items="${wallets}">
                        <option value="${wallet.id}"><c:out value="${wallet.name}"/> (<c:out value="${wallet.currency}"/>)</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-field">
                <label class="form-label" for="form-note">Notes</label>
                <textarea class="form-textarea" id="form-note" placeholder="Optional details about this transaction"></textarea>
            </div>

            <div id="form-error" class="alert alert-error" hidden></div>

            <div class="modal__footer">
                <button type="button" class="btn btn-ghost" id="modal-cancel">Cancel</button>
                <button type="submit" class="btn btn-primary" id="form-submit">Create transaction</button>
            </div>
        </form>
    </div>
</div>

<script>
window.CTX_PATH   = "${ctx}";
window.WALLET_IDS = [
    <c:forEach var="w" items="${wallets}" varStatus="status">
    { id: "${w.id}", name: "<c:out value='${w.name}'/>", currency: "<c:out value='${w.currency}'/>" }<c:if test="${not status.last}">,</c:if>
    </c:forEach>
];
</script>
<script src="${ctx}/static/js/transactions.js"></script>

</body>
</html>
