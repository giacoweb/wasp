<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack &middot; Compound Interest</title>

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

<main class="container section">

    <nav aria-label="breadcrumb">
        <p class="breadcrumb">
            <a href="${ctx}/guest/report">Calculators</a>
            <span>&rsaquo; Compound interest</span>
        </p>
    </nav>

    <div class="calc-split">

        <div class="card card--padded">
            <div class="card-header">
                <div class="calc-form-icon"><i data-lucide="trending-up"></i></div>
                <div class="card-title">Compound interest</div>
            </div>

            <div class="form">

                <div class="form-field" id="field-initial">
                    <label class="form-label" for="initialAmount">Initial amount</label>
                    <div class="input-adornment input-adornment--prefix">
                        <span class="input-adornment__prefix">&euro;</span>
                        <input class="form-input" type="number" id="initialAmount"
                               min="1" step="100" required placeholder="10000" value="10000">
                    </div>
                    <span class="form-help">Starting investment</span>
                    <span class="form-error" id="err-initial"></span>
                </div>

                <div class="form-field" id="field-monthly">
                    <label class="form-label" for="monthlyContrib">Monthly contribution</label>
                    <div class="input-adornment input-adornment--prefix">
                        <span class="input-adornment__prefix">&euro;</span>
                        <input class="form-input" type="number" id="monthlyContrib"
                               min="0" step="10" required placeholder="250" value="250">
                    </div>
                    <span class="form-help">Added every month</span>
                    <span class="form-error" id="err-monthly"></span>
                </div>

                <div class="form-field" id="field-rate">
                    <label class="form-label" for="annualReturn">Annual return</label>
                    <div class="input-adornment input-adornment--suffix">
                        <input class="form-input" type="number" id="annualReturn"
                               min="0" max="100" step="0.1" required placeholder="7" value="7">
                        <span class="input-adornment__suffix">%</span>
                    </div>
                    <span class="form-help">Expected annual return</span>
                    <span class="form-error" id="err-rate"></span>
                </div>

                <div class="form-field" id="field-years">
                    <label class="form-label" for="durationYears">Duration in years</label>
                    <input class="form-input" type="number" id="durationYears"
                           min="1" max="50" step="1" required placeholder="10" value="10">
                    <span class="form-help">Investment horizon</span>
                    <span class="form-error" id="err-years"></span>
                </div>

                <button type="button" class="btn btn-primary btn-block" id="btn-calculate">
                    <i data-lucide="square"></i>
                    Calculate
                </button>

                <div class="calc-info-box">
                    <div class="calc-info-box__icon">i</div>
                    <div>
                        <div class="calc-info-box__title">Compounding effect</div>
                        <p class="calc-info-box__text">
                            With regular monthly contributions, your interest starts small but becomes
                            a larger share of the final balance over time.
                        </p>
                    </div>
                </div>

            </div>
        </div>

        <div class="calc-right-col">

            <div class="calc-kpi-strip" id="kpi-strip" hidden>
                <div class="card kpi-card">
                    <div class="kpi-card__header">
                        <div class="kpi-card__icon kpi-card__icon--teal"><i data-lucide="trending-up"></i></div>
                        <span class="kpi-card__label">Future value</span>
                    </div>
                    <div class="kpi-card__value" id="kpi-future-value">&mdash;</div>
                    <div class="kpi-card__caption" id="kpi-future-caption">After X years</div>
                </div>
                <div class="card kpi-card">
                    <div class="kpi-card__header">
                        <div class="kpi-card__icon kpi-card__icon--green"><i data-lucide="plus-circle"></i></div>
                        <span class="kpi-card__label">Total contributed</span>
                    </div>
                    <div class="kpi-card__value" id="kpi-contributed">&mdash;</div>
                    <div class="kpi-card__caption">Initial + monthly deposits</div>
                </div>
                <div class="card kpi-card">
                    <div class="kpi-card__header">
                        <div class="kpi-card__icon kpi-card__icon--amber"><i data-lucide="percent"></i></div>
                        <span class="kpi-card__label">Interest earned</span>
                    </div>
                    <div class="kpi-card__value" id="kpi-interest">&mdash;</div>
                    <div class="kpi-card__caption">From compound growth</div>
                </div>
            </div>

            <div id="chart-panel">
                <div class="card card--empty">
                    <div class="empty-state__icon"><i data-lucide="bar-chart-2"></i></div>
                    <p class="empty-state__desc">
                        Fill in the investment details and click <strong>Calculate</strong> to see the growth projection.
                    </p>
                </div>
            </div>

        </div>

    </div>

    <div id="projection-section" hidden>
        <div class="card" id="projection-table">
            <div class="projection-title">Year-by-year projection</div>
            <div class="table-wrapper">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Year</th>
                            <th class="num">Contributed</th>
                            <th class="num">Interest earned</th>
                            <th class="num">Balance</th>
                        </tr>
                    </thead>
                    <tbody id="projection-body"></tbody>
                </table>
            </div>
        </div>
    </div>

</main>

<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
<script src="${ctx}/static/js/icons.js"></script>
<script src="${ctx}/static/js/compound-calculator.js"></script>
</body>
</html>
