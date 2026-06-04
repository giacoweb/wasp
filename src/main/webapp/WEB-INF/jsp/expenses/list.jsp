<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="hasWallets" value="${not empty wallets}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recurring Expenses &middot; FinTrack</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

    <link rel="stylesheet" href="${ctx}/static/css/tokens.css">
    <link rel="stylesheet" href="${ctx}/static/css/base.css">
    <link rel="stylesheet" href="${ctx}/static/css/layout.css">
    <link rel="stylesheet" href="${ctx}/static/css/components.css">
    <link rel="stylesheet" href="${ctx}/static/css/tables.css">
    <link rel="stylesheet" href="${ctx}/static/css/forms.css">
    <link rel="stylesheet" href="${ctx}/static/css/dashboard.css">
    <link rel="stylesheet" href="${ctx}/static/css/expenses.css">
</head>
<body>

<div class="app-shell" id="expenses-page" data-context-path="${ctx}">

    <jsp:include page="/WEB-INF/jsp/common/_sidebar.jsp">
        <jsp:param name="navActive" value="expenses"/>
    </jsp:include>

    <main class="app-main">

        <nav class="breadcrumb" aria-label="Breadcrumb">
            <a href="${ctx}/dashboard">FinTrack</a> / <span>Recurring Expenses</span>
        </nav>

        <header class="page-header">
            <div>
                <h1>Recurring Expenses</h1>
                <p class="muted">Track subscriptions, rent, bills, and other scheduled costs.</p>
            </div>
            <button class="btn btn-primary" id="expense-create-btn" type="button" ${hasWallets ? '' : 'disabled'}>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                New expense
            </button>
        </header>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error" role="alert">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not hasWallets}">
                <section class="card card--empty">
                    <p class="muted">No wallets yet. <a href="${ctx}/wallets">Create one first.</a></p>
                </section>
            </c:when>
            <c:otherwise>

                <section class="expenses-kpi-strip" aria-label="Summary">
                    <article class="card expenses-kpi">
                        <div class="expenses-kpi__icon expenses-kpi__icon--active" aria-hidden="true">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M20 6 9 17l-5-5"/>
                            </svg>
                        </div>
                        <div>
                            <p class="expenses-kpi__label">Active expenses</p>
                            <p class="expenses-kpi__value tabular" id="kpi-active-count">0</p>
                        </div>
                    </article>

                    <article class="card expenses-kpi">
                        <div class="expenses-kpi__icon expenses-kpi__icon--monthly" aria-hidden="true">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect width="18" height="18" x="3" y="4" rx="2"/>
                                <line x1="16" y1="2" x2="16" y2="6"/>
                                <line x1="8" y1="2" x2="8" y2="6"/>
                                <line x1="3" y1="10" x2="21" y2="10"/>
                            </svg>
                        </div>
                        <div>
                            <p class="expenses-kpi__label">Monthly equivalent</p>
                            <p class="expenses-kpi__value tabular" id="kpi-monthly-total">&euro;0.00</p>
                        </div>
                    </article>

                    <article class="card expenses-kpi">
                        <div class="expenses-kpi__icon expenses-kpi__icon--due" aria-hidden="true">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"/>
                                <polyline points="12 6 12 12 16 14"/>
                            </svg>
                        </div>
                        <div>
                            <p class="expenses-kpi__label">Due soon</p>
                            <p class="expenses-kpi__value tabular" id="kpi-due-count">0</p>
                        </div>
                    </article>
                </section>

                <section class="card card--padded">
                    <div class="tx-toolbar">
                        <div class="form-input-group">
                            <svg class="form-input-group__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
                            </svg>
                            <input class="form-input" id="expense-search" type="search" placeholder="Search recurring expenses...">
                        </div>
                        <div class="filter-tabs">
                            <button class="filter-tab active" data-filter="ALL" aria-pressed="true">All</button>
                            <button class="filter-tab" data-filter="ACTIVE" aria-pressed="false">Active</button>
                            <button class="filter-tab" data-filter="PAUSED" aria-pressed="false">Paused</button>
                        </div>
                    </div>

                    <div id="expense-alert" class="alert alert-error" role="alert" hidden></div>

                    <div class="table-wrapper">
                        <table class="table table--striped">
                            <thead>
                                <tr>
                                    <th>Label</th>
                                    <th>Wallet</th>
                                    <th>Periodicity</th>
                                    <th>Next due</th>
                                    <th>Status</th>
                                    <th class="num">Amount</th>
                                    <th class="actions"></th>
                                </tr>
                            </thead>
                            <tbody id="expenses-body">
                                <tr><td colspan="7" class="table-empty">Loading...</td></tr>
                            </tbody>
                        </table>
                    </div>
                </section>

            </c:otherwise>
        </c:choose>
    </main>
</div>

<div class="modal-overlay" id="expense-modal" role="dialog" aria-modal="true" aria-labelledby="expense-modal-title" hidden>
    <div class="card modal-card">
        <div class="card-header">
            <h2 class="card-title" id="expense-modal-title">Create recurring expense</h2>
            <button type="button" class="btn btn-icon btn-ghost" id="expense-modal-close" aria-label="Close">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
            </button>
        </div>

        <form id="expense-form" class="form" novalidate>
            <input type="hidden" id="expense-id">

            <div class="form-field">
                <label class="form-label form-required" for="expense-label">Label</label>
                <input class="form-input" id="expense-label" type="text" maxlength="120" required placeholder="e.g. Rent, Netflix, Phone bill">
                <span class="form-error"></span>
            </div>

            <div class="form-grid-2">
                <div class="form-field">
                    <label class="form-label form-required" for="expense-amount">Amount</label>
                    <input class="form-input" id="expense-amount" type="number" step="0.01" min="0.01" required placeholder="49.99">
                    <span class="form-error"></span>
                </div>
                <div class="form-field">
                    <label class="form-label form-required" for="expense-next-due">Next due</label>
                    <input class="form-input" id="expense-next-due" type="date" required>
                    <span class="form-error"></span>
                </div>
            </div>

            <div class="form-grid-2">
                <div class="form-field">
                    <label class="form-label form-required" for="expense-periodicity">Periodicity</label>
                    <select class="form-select" id="expense-periodicity" required>
                        <option value="MONTHLY">Monthly</option>
                        <option value="QUARTERLY">Quarterly</option>
                        <option value="YEARLY">Yearly</option>
                    </select>
                    <span class="form-error"></span>
                </div>
                <div class="form-field">
                    <label class="form-label form-required" for="expense-wallet">Wallet</label>
                    <select class="form-select" id="expense-wallet" required>
                        <c:forEach var="wallet" items="${wallets}">
                            <option value="${wallet.id}"
                                    data-wallet-name="<c:out value='${wallet.name}'/>"
                                    data-wallet-currency="<c:out value='${wallet.currency}'/>">
                                <c:out value="${wallet.name}"/> (<c:out value="${wallet.currency}"/>)
                            </option>
                        </c:forEach>
                    </select>
                    <span class="form-error"></span>
                </div>
            </div>

            <div id="expense-form-error" class="alert alert-error" hidden></div>

            <div class="modal__footer">
                <button type="button" class="btn btn-ghost" id="expense-modal-cancel">Cancel</button>
                <button type="submit" class="btn btn-primary" id="expense-submit">Create expense</button>
            </div>
        </form>
    </div>
</div>

<script src="${ctx}/static/js/expenses.js"></script>

</body>
</html>
