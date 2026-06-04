<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:useBean id="today" class="java.util.Date"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<fmt:formatDate value="${today}" pattern="M"    var="curMonth"/>
<fmt:formatDate value="${today}" pattern="yyyy" var="curYear"/>
<c:set var="monthNames" value="${['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack &middot; Loan Amortization</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

    <link rel="stylesheet" href="${ctx}/static/css/tokens.css">
    <link rel="stylesheet" href="${ctx}/static/css/base.css">
    <link rel="stylesheet" href="${ctx}/static/css/layout.css">
    <link rel="stylesheet" href="${ctx}/static/css/components.css">
    <link rel="stylesheet" href="${ctx}/static/css/forms.css">
    <link rel="stylesheet" href="${ctx}/static/css/tables.css">
    <link rel="stylesheet" href="${ctx}/static/css/guest.css">
</head>
<body data-context-path="${ctx}">

<header class="site-header">
    <a href="${ctx}/" class="brand">
        <i data-lucide="triangle"></i>
        FinTrack
    </a>
    <nav class="site-header__nav" aria-label="Primary">
        <a href="${ctx}/guest/report" class="guest-session-btn">
            <i data-lucide="alert-triangle"></i>
            Guest session
        </a>
        <a href="${ctx}/login"    class="btn btn-ghost">Log in</a>
        <a href="${ctx}/register" class="btn btn-primary">Create account</a>
    </nav>
</header>

<main class="section" style="padding-left: 80px; padding-right: 80px;">

    <a href="${ctx}/guest/report" class="calc-back">
        <i data-lucide="arrow-left"></i>
        Back to calculators
    </a>

    <h1>Loan Amortization</h1>
    <p class="muted" style="margin-bottom: var(--space-6);">
        Estimate monthly payments, total interest, and the full repayment schedule.
    </p>

    <div class="calc-split">

        <div class="card card--padded">
            <div class="card-header" style="justify-content: flex-start; gap: var(--space-3); align-items: flex-start;">
                <div class="calc-form-icon" style="flex-shrink:0;">
                    <i data-lucide="table-2"></i>
                </div>
                <div>
                    <div class="card-title">Loan details</div>
                    <div class="card-subtitle">Adjust the values to estimate your monthly payment.</div>
                </div>
            </div>

            <div class="form" id="loan-form">

                <div class="form-grid-2">
                    <div class="form-field" id="field-principal">
                        <label class="form-label" for="principal">Loan amount</label>
                        <div class="input-adornment input-adornment--prefix">
                            <span class="input-adornment__prefix">&euro;</span>
                            <input class="form-input" type="number" id="principal"
                                   min="1" step="100" required placeholder="25000" value="25000">
                        </div>
                        <span class="form-error" id="err-principal"></span>
                    </div>

                    <div class="form-field" id="field-rate">
                        <label class="form-label" for="annualRate">Annual interest rate</label>
                        <div class="input-adornment input-adornment--suffix">
                            <input class="form-input" type="number" id="annualRate"
                                   min="0" max="100" step="0.1" required placeholder="6.5" value="6.5">
                            <span class="input-adornment__suffix">%</span>
                        </div>
                        <span class="form-error" id="err-rate"></span>
                    </div>
                </div>

                <div class="form-grid-2">
                    <div class="form-field" id="field-months">
                        <label class="form-label" for="months">Loan term</label>
                        <div class="input-adornment input-adornment--suffix">
                            <input class="form-input" type="number" id="months"
                                   min="1" max="600" step="1" required placeholder="60" value="60">
                            <span class="input-adornment__suffix">months</span>
                        </div>
                        <span class="form-error" id="err-months"></span>
                    </div>

                    <div class="form-field">
                        <label class="form-label">Start month</label>
                        <div style="display:flex; gap:var(--space-2);">
                            <select class="form-select" id="startMonth" style="flex:1;">
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}" ${m == curMonth ? 'selected' : ''}>${monthNames[m - 1]}</option>
                                </c:forEach>
                            </select>
                            <select class="form-select" id="startYear" style="flex:1;">
                                <c:forEach var="y" begin="${curYear}" end="${curYear + 5}">
                                    <option value="${y}" ${y == curYear ? 'selected' : ''}>${y}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>

                <div style="display:flex; gap:var(--space-3); margin-top:var(--space-2);">
                    <button type="button" class="btn btn-primary" id="btn-calculate" style="flex:1;">
                        <i data-lucide="refresh-cw"></i>
                        Calculate payment
                    </button>
                    <button type="button" class="btn btn-ghost" id="btn-reset">Reset</button>
                </div>

            </div>
        </div>

        <div id="results-panel">
            <div class="card card--empty">
                <div class="empty-state__icon">
                    <i data-lucide="calculator"></i>
                </div>
                <p class="empty-state__desc">
                    Fill in the loan details and click <strong>Calculate payment</strong> to see your summary.
                </p>
            </div>
        </div>

    </div>

    <div id="schedule-section" hidden style="margin-top: var(--space-6);">
        <div class="card" style="padding:0; overflow:hidden;">
            <div style="display:flex; justify-content:space-between; align-items:center; padding: var(--space-5) var(--space-6);">
                <h2 class="card-title" style="margin:0;">Amortization schedule</h2>
                <button type="button" class="btn btn-ghost btn-sm" id="btn-export-csv">
                    <i data-lucide="download"></i>
                    Export CSV
                </button>
            </div>
            <div class="table-wrapper">
                <table class="table" id="schedule-table">
                    <thead>
                        <tr>
                            <th>Month</th>
                            <th class="num">Payment</th>
                            <th class="num">Principal</th>
                            <th class="num">Interest</th>
                            <th class="num">Remaining Balance</th>
                        </tr>
                    </thead>
                    <tbody id="schedule-body"></tbody>
                </table>
            </div>
        </div>
    </div>

</main>

<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
<script>window.lucide && lucide.createIcons();</script>
<script src="${ctx}/static/js/loan-calculator.js"></script>
</body>
</html>
