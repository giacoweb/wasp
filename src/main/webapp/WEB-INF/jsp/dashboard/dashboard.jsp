<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="me"  value="${sessionScope.authenticatedUser}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FinTrack - Dashboard</title>

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
</head>
<body>

<div class="app-shell">

    <jsp:include page="/WEB-INF/jsp/common/_sidebar.jsp">
        <jsp:param name="navActive" value="dashboard"/>
    </jsp:include>

    <main class="app-main">
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error dashboard-alert">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <div class="dashboard-header">
            <h1>Hi, <c:out value="${me.name}"/> 👋</h1>
            <a href="${ctx}/report/export" class="btn btn-ghost dashboard-report-link">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
                Download report
            </a>
        </div>

        <c:if test="${not empty summary}">

            <div class="grid grid--3">
                <c:choose>
                    <c:when test="${not empty summary.statsByCurrency}">
                        <c:forEach var="stats" items="${summary.statsByCurrency}">
                            <div class="card">
                                <div class="card-subtitle">Total Equity (<c:out value="${stats.currency()}"/>)</div>
                                <div class="card-title dashboard-kpi-value">
                                    <c:out value="${stats.currency()}"/> <fmt:formatNumber value="${stats.totalEquity()}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                                </div>
                                <span class="badge badge-income">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="dashboard-check-icon"><path d="M5 12l5 5L20 7"/></svg>
                                    Up to date
                                </span>
                            </div>

                            <div class="card">
                                <div class="card-subtitle">YTD Expenses (<c:out value="${stats.currency()}"/>)</div>
                                <div class="card-title dashboard-kpi-value">
                                    <c:out value="${stats.currency()}"/> <fmt:formatNumber value="${stats.ytdExpenses()}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                                </div>
                                <span class="badge badge-expense">YTD</span>
                            </div>

                            <div class="card">
                                <div class="card-subtitle">YTD Savings (<c:out value="${stats.currency()}"/>)</div>
                                <div class="card-title dashboard-kpi-value">
                                    <c:out value="${stats.currency()}"/> <fmt:formatNumber value="${stats.ytdSavings()}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                                </div>
                                <span class="badge badge-income">YTD</span>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="card"><p class="muted dashboard-empty-copy">No financial data yet.</p></div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="card-header dashboard-section-header">
                <h2 class="card-title">Your wallets</h2>
                <a href="${ctx}/wallets" class="btn btn-primary">+ New wallet</a>
            </div>

            <div class="grid grid--3">
                <c:choose>
                    <c:when test="${not empty summary.wallets}">
                        <c:forEach var="wallet" items="${summary.wallets}">
                            <c:set var="typeName" value="${wallet.type}"/>
                            <c:choose>
                                <c:when test="${typeName eq 'CASH'}">
                                    <c:set var="badgeClass" value="badge-wallet-cash"/>
                                    <c:set var="typeLabel"  value="Cash"/>
                                </c:when>
                                <c:when test="${typeName eq 'BANK_ACCOUNT'}">
                                    <c:set var="badgeClass" value="badge-wallet-bank"/>
                                    <c:set var="typeLabel"  value="Bank"/>
                                </c:when>
                                <c:when test="${typeName eq 'CREDIT_CARD'}">
                                    <c:set var="badgeClass" value="badge-wallet-credit-card"/>
                                    <c:set var="typeLabel"  value="Credit Card"/>
                                </c:when>
                                <c:when test="${typeName eq 'INVESTMENT'}">
                                    <c:set var="badgeClass" value="badge-wallet-investment"/>
                                    <c:set var="typeLabel"  value="Investment"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="badgeClass" value="badge-primary"/>
                                    <c:set var="typeLabel"  value="${typeName}"/>
                                </c:otherwise>
                            </c:choose>

                            <div class="card">
                                <div class="dashboard-wallet-meta">
                                    <span class="badge ${badgeClass}">${typeLabel}</span>
                                    <span class="muted dashboard-wallet-currency"><c:out value="${wallet.currency}"/></span>
                                </div>
                                <div class="card-title dashboard-wallet-name"><c:out value="${wallet.name}"/></div>
                                <div class="card-title dashboard-wallet-balance">
                                    <c:choose>
                                        <c:when test="${wallet.balance.signum() lt 0}">
                                            <span class="dashboard-wallet-balance--negative"><fmt:formatNumber value="${wallet.balance}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatNumber value="${wallet.balance}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="card dashboard-empty-card">
                            <p class="muted">No wallets yet.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="card-header dashboard-section-header">
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
            <div id="tx-footer" class="dashboard-tx-footer">
                <span class="muted dashboard-tx-count" id="tx-count"></span>
                <a href="${ctx}/transactions" class="dashboard-tx-link">View all transactions &rarr;</a>
            </div>

        </c:if>
    </main>
</div>

<div id="dashboard-data" hidden data-context-path="${ctx}">
    <c:if test="${not empty summary.wallets}">
        <c:forEach var="w" items="${summary.wallets}">
            <span class="js-dashboard-wallet"
                  data-id="${w.id}"
                  data-name="${fn:escapeXml(w.name)}"></span>
        </c:forEach>
    </c:if>
</div>
<script src="${ctx}/static/js/dashboard.js"></script>

</body>
</html>
