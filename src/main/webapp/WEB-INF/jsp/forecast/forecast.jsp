<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.model.User" %>
<%
    User user = (User) session.getAttribute("authenticatedUser");
    String ctxPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forecast &middot; FinTrack</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

    <link rel="stylesheet" href="<%= ctxPath %>/static/css/tokens.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/base.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/layout.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/components.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/tables.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/forms.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/dashboard.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/forecast.css">
</head>
<body data-context-path="<%= ctxPath %>">

<div class="app-shell">

    <jsp:include page="/WEB-INF/jsp/common/_sidebar.jsp">
        <jsp:param name="navActive" value="forecast"/>
    </jsp:include>

    <main class="app-main">

        <nav class="breadcrumb" aria-label="Breadcrumb">
            <a href="<%= ctxPath %>/dashboard">FinTrack</a> / <span>Forecast</span>
        </nav>

        <header class="page-header">
            <div>
                <h1>Forecast</h1>
                <p class="muted">Project your finances 24 months ahead.</p>
            </div>
            <button class="btn btn-ghost" id="recompute-btn" type="button">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <polyline points="23 4 23 10 17 10"/>
                    <polyline points="1 20 1 14 7 14"/>
                    <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
                </svg>
                Recompute
            </button>
        </header>

        <section class="scenario-selector" aria-label="Scenario">

            <div class="scenario-tabs" role="radiogroup" aria-label="Forecast scenario">
                <button class="scenario-pill" type="button" role="radio"
                        data-scenario="OPTIMISTIC" aria-checked="false">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                        <circle cx="12" cy="12" r="5"/>
                        <line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/>
                        <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
                        <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
                        <line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/>
                        <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
                        <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
                    </svg>
                    Optimistic
                </button>

                <button class="scenario-pill is-active" type="button" role="radio"
                        data-scenario="CONSERVATIVE" aria-checked="true">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                        <path d="M17 18a5 5 0 0 0-10 0"/>
                        <line x1="12" y1="2" x2="12" y2="9"/>
                        <line x1="4.22" y1="10.22" x2="5.64" y2="11.64"/>
                        <line x1="1" y1="18" x2="3" y2="18"/>
                        <line x1="21" y1="18" x2="23" y2="18"/>
                        <line x1="18.36" y1="11.64" x2="19.78" y2="10.22"/>
                        <line x1="23" y1="22" x2="1" y2="22"/>
                    </svg>
                    Conservative
                </button>

                <button class="scenario-pill" type="button" role="radio"
                        data-scenario="STRESS_TEST" aria-checked="false">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                        <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                    </svg>
                    Stress test
                </button>
            </div>

            <article class="scenario-info" role="note">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="12" y1="16" x2="12" y2="12"/>
                    <line x1="12" y1="8" x2="12.01" y2="8"/>
                </svg>
                <p id="scenario-info-text">Income unchanged, expenses with 3% monthly compounding inflation.</p>
            </article>
        </section>

        <div id="state-loading" class="card forecast-state" role="status" aria-live="polite">
            <div class="forecast-state__inner">
                <p class="muted">Loading forecast&hellip;</p>
            </div>
        </div>

        <div id="state-empty" class="card forecast-state" hidden>
            <div class="empty-state">
                <div class="empty-state__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/>
                        <polyline points="16 7 22 7 22 13"/>
                    </svg>
                </div>
                <h2 class="empty-state__title">No forecast for this scenario yet</h2>
                <p class="empty-state__desc">Compute a projection from your current wallets, recent transactions, and active PAC plans.</p>
                <button class="btn btn-primary" id="empty-recompute-btn" type="button">Compute forecast</button>
            </div>
        </div>

        <div id="state-error" class="alert alert-error" role="alert" hidden>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            <span id="state-error-message">Something went wrong.</span>
        </div>

        <div id="state-ready" hidden>

            <section class="forecast-kpis" aria-label="Projection summary">

                <article class="card forecast-kpi">
                    <div class="forecast-kpi__icon" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/>
                            <polyline points="17 6 23 6 23 12"/>
                        </svg>
                    </div>
                    <div>
                        <p class="forecast-kpi__label">Projected end balance (month 24)</p>
                        <p class="forecast-kpi__value tabular" id="kpi-end-balance">&mdash;</p>
                    </div>
                </article>

                <article class="card forecast-kpi">
                    <div class="forecast-kpi__icon forecast-kpi__icon--positive" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="12" y1="8" x2="12" y2="16"/>
                            <polyline points="8 12 12 16 16 12"/>
                        </svg>
                    </div>
                    <div>
                        <p class="forecast-kpi__label">Total projected income</p>
                        <p class="forecast-kpi__value tabular" id="kpi-total-income">&mdash;</p>
                    </div>
                </article>

                <article class="card forecast-kpi">
                    <div class="forecast-kpi__icon forecast-kpi__icon--negative" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="12" y1="16" x2="12" y2="8"/>
                            <polyline points="8 12 12 8 16 12"/>
                        </svg>
                    </div>
                    <div>
                        <p class="forecast-kpi__label">Total projected expenses</p>
                        <p class="forecast-kpi__value tabular" id="kpi-total-expenses">&mdash;</p>
                    </div>
                </article>

                <article class="card forecast-kpi">
                    <div class="forecast-kpi__icon" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="17 1 21 5 17 9"/>
                            <path d="M3 11V9a4 4 0 0 1 4-4h14"/>
                            <polyline points="7 23 3 19 7 15"/>
                            <path d="M21 13v2a4 4 0 0 1-4 4H3"/>
                        </svg>
                    </div>
                    <div>
                        <p class="forecast-kpi__label">Total PAC contributions</p>
                        <p class="forecast-kpi__value tabular" id="kpi-total-pac">&mdash;</p>
                    </div>
                </article>
            </section>

            <section class="card forecast-chart-card">
                <header class="forecast-chart-card__head">
                    <h2 class="card-title">24-month projection</h2>
                    <ul class="forecast-legend" aria-label="Chart legend">
                        <li><span class="forecast-legend__dot forecast-legend__dot--income"></span> Income</li>
                        <li><span class="forecast-legend__dot forecast-legend__dot--expense"></span> Expense</li>
                        <li><span class="forecast-legend__dot forecast-legend__dot--balance"></span> Balance</li>
                    </ul>
                </header>
                <svg id="forecast-chart" class="forecast-chart" viewBox="0 0 800 320"
                     preserveAspectRatio="xMidYMid meet" role="img"
                     aria-label="Income, expense and balance over 24 months"></svg>
            </section>

            <section class="card">
                <header class="card-header">
                    <h2 class="card-title">Monthly breakdown</h2>
                </header>
                <div class="table-wrapper forecast-table-wrapper">
                    <table class="table table--striped">
                        <thead>
                            <tr>
                                <th>Month</th>
                                <th class="num">Income</th>
                                <th class="num">Expense</th>
                                <th class="num">PAC</th>
                                <th class="num">Net flow</th>
                                <th class="num">Balance</th>
                            </tr>
                        </thead>
                        <tbody id="forecast-tbody"></tbody>
                    </table>
                </div>
            </section>

        </div>

    </main>
</div>

<script src="<%= ctxPath %>/static/js/forecast.js"></script>

</body>
</html>
