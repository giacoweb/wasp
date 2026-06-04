<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="hasData" value="${not empty compoundList}"/>

<c:if test="${hasData}">
    <c:set var="resultSize" value="${fn:length(compoundList)}"/>
    <c:set var="futureValue" value="${compoundList[resultSize - 1].totalValue}"/>
    <c:set var="totalContributed" value="${initialCapital + (monthlyContribution * 12 * resultSize)}"/>
    <c:set var="interestEarned" value="${futureValue - totalContributed}"/>
</c:if>

<c:choose>
    <c:when test="${not empty initialCapital}"><c:set var="initialCapStr"><fmt:formatNumber value="${initialCapital}" maxFractionDigits="0" groupingUsed="false"/></c:set></c:when>
    <c:otherwise><c:set var="initialCapStr" value="${param.initialCapital}"/></c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${not empty monthlyContribution}"><c:set var="monthlyContStr"><fmt:formatNumber value="${monthlyContribution}" maxFractionDigits="0" groupingUsed="false"/></c:set></c:when>
    <c:otherwise><c:set var="monthlyContStr" value="${param.monthlyContribution}"/></c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${not empty annualRate}"><c:set var="annualRateStr"><fmt:formatNumber value="${annualRate}" maxFractionDigits="2" groupingUsed="false"/></c:set></c:when>
    <c:otherwise><c:set var="annualRateStr" value="${param.annualRate}"/></c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${hasData}"><c:set var="durationStr" value="${resultSize}"/></c:when>
    <c:otherwise><c:set var="durationStr" value="${param.years}"/></c:otherwise>
</c:choose>

<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack - Compound Report</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap">

    <link rel="stylesheet" href="${ctx}/static/css/tokens.css">
    <link rel="stylesheet" href="${ctx}/static/css/base.css">
    <link rel="stylesheet" href="${ctx}/static/css/layout.css">
    <link rel="stylesheet" href="${ctx}/static/css/components.css">
    <link rel="stylesheet" href="${ctx}/static/css/tables.css">
    <link rel="stylesheet" href="${ctx}/static/css/forms.css">
    <link rel="stylesheet" href="${ctx}/static/css/calculator.css">


</head>
<body>

<header class="site-header">
    <a href="${ctx}/" class="brand">
        <i data-lucide="triangle"></i> FinTrack
    </a>
    <nav class="site-header__nav flex-row" aria-label="Primary">
        <button class="btn btn-ghost btn-guest-session">
            <i data-lucide="beaker"></i> Guest session
        </button>
        <div class="nav-separator"></div>
        <a href="${ctx}/login" class="btn btn-ghost">Log in</a>
        <a href="${ctx}/register" class="btn btn-primary">Create account</a>
    </nav>
</header>

<main class="container container--wide section">

    <nav class="breadcrumb">
        <a href="${ctx}/guest/report">Calculators</a> / <span>Compound interest</span>
    </nav>

    <header class="mb-10">
        <h1 class="text-3xl mb-2">Compound Interest</h1>
        <p class="text-lg muted m-0">Estimate your future wealth, compound growth, and the power of consistent investing.</p>
    </header>

    <div class="calc-layout mb-10">

        <aside>
            <article class="card">
                <div class="flex-row gap-3 mb-6">
                    <div class="form-header-icon">
                        <i data-lucide="arrow-up-right"></i>
                    </div>
                    <h2 class="text-2xl font-semibold mb-0">Compound interest</h2>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error mb-4">
                        <i data-lucide="alert-circle"></i>
                        <span><c:out value="${errorMessage}"/></span>
                    </div>
                </c:if>

                <form class="form" action="${ctx}/report/compound" method="POST">
                    <div class="form-field">
                        <label class="form-label">Initial amount</label>
                        <div class="form-input-group form-input-group--prefix-calc">
                            <span class="input-prefix-calc">€</span>
                            <input type="text" name="initialCapital" class="form-input" value="<c:out value='${initialCapStr}'/>" />
                        </div>
                        <span class="form-help">Starting investment</span>
                    </div>

                    <div class="form-field">
                        <label class="form-label">Monthly contribution</label>
                        <div class="form-input-group form-input-group--prefix-calc">
                            <span class="input-prefix-calc">€</span>
                            <input type="text" name="monthlyContribution" class="form-input" value="<c:out value='${monthlyContStr}'/>" />
                        </div>
                        <span class="form-help">Added every month</span>
                    </div>

                    <div class="form-field">
                        <label class="form-label">Annual return</label>
                        <div class="form-input-group form-input-group--suffix-calc">
                            <input type="text" name="annualRate" class="form-input" value="<c:out value='${annualRateStr}'/>" />
                            <span class="input-suffix-calc">%</span>
                        </div>
                        <span class="form-help">Expected annual return</span>
                    </div>

                    <div class="form-field">
                        <label class="form-label">Duration in years</label>
                        <input type="text" name="years" class="form-input" value="<c:out value='${durationStr}'/>" />
                        <span class="form-help">Investment horizon</span>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block mt-4">
                        <i data-lucide="calculator"></i> Calculate
                    </button>
                </form>
            </article>

            <article class="card mt-4 flex align-start gap-3">
                <div class="advice-icon">
                    <i data-lucide="info"></i>
                </div>
                <div>
                    <h4 class="text-sm m-0 mb-1">Compounding effect</h4>
                    <p class="text-sm muted m-0">
                        With regular monthly contributions, your interest starts small but becomes a larger share of the final balance over time.
                    </p>
                </div>
            </article>
        </aside>

        <div class="flex-col gap-6">

            <c:choose>
                <c:when test="${hasData}">
                    <div class="grid grid--3">
                        <article class="card">
                            <div class="flex-row gap-2 mb-3 muted text-sm">
                                <div class="badge badge-primary badge-icon">
                                    <i data-lucide="arrow-up-right"></i>
                                </div>
                                <span>Future value</span>
                            </div>
                            <h3 class="text-3xl text-primary font-normal mb-0">
                                €<fmt:formatNumber value="${futureValue}" maxFractionDigits="0"/>
                            </h3>
                            <p class="text-xs muted m-0">After ${resultSize} years</p>
                        </article>

                        <article class="card">
                            <div class="flex-row gap-2 mb-3 muted text-sm">
                                <div class="badge badge-neutral badge-icon">
                                    <i data-lucide="plus"></i>
                                </div>
                                <span>Total contributed</span>
                            </div>
                            <h3 class="text-3xl font-normal mb-0">
                                €<fmt:formatNumber value="${totalContributed}" maxFractionDigits="0"/>
                            </h3>
                            <p class="text-xs muted m-0">Initial + monthly deposits</p>
                        </article>

                        <article class="card">
                            <div class="flex-row gap-2 mb-3 muted text-sm">
                                <div class="badge badge-income badge-icon">
                                    <i data-lucide="percent"></i>
                                </div>
                                <span>Interest earned</span>
                            </div>
                            <h3 class="text-3xl font-normal mb-0">
                                €<fmt:formatNumber value="${interestEarned}" maxFractionDigits="0"/>
                            </h3>
                            <p class="text-xs muted m-0">From compound growth</p>
                        </article>
                    </div>

                    <article class="card">
                        <div class="card-header mb-6">
                            <h3 class="card-title m-0">Growth projection</h3>
                            <div class="chart-legend">
                                <span class="flex-row gap-1">
                                    <span class="legend-dot legend-dot--contrib"></span> Contributions
                                </span>
                                <span class="flex-row gap-1">
                                    <span class="legend-dot legend-dot--interest"></span> Interest
                                </span>
                            </div>
                        </div>
                        <div>
                            <canvas id="compoundChart" class="chart-canvas"></canvas>
                        </div>
                    </article>

                    <article class="card card--table table-paginated-container" data-per-page="12" data-item-name="years">
                        <div class="card-header-border">
                            <h3 class="card-title m-0">Year-by-year projection</h3>
                        </div>
                        <div class="table-wrapper">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th class="pl-6">YEAR</th>
                                    <th>CONTRIBUTED</th>
                                    <th>INTEREST EARNED</th>
                                    <th class="pr-6">BALANCE</th>
                                </tr>
                                </thead>
                                <tbody class="table-paginate-body">
                                    <c:set var="cc" value="${initialCapital}"/>
                                    <c:forEach var="row" items="${compoundList}">
                                        <c:set var="cc" value="${cc + (monthlyContribution * 12)}"/>
                                        <c:set var="ci" value="${row.totalValue - cc}"/>
                                        <tr>
                                            <td class="pl-6 muted">Year ${row.year}</td>
                                            <td class="font-medium">€<fmt:formatNumber value="${cc}" maxFractionDigits="0"/></td>
                                            <td class="text-primary font-medium">€<fmt:formatNumber value="${ci}" maxFractionDigits="0"/></td>
                                            <td class="pr-6 font-semibold">€<fmt:formatNumber value="${row.totalValue}" maxFractionDigits="0"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="table-footer">
                            <span class="text-sm muted table-pagination-info"></span>
                            <div class="table-footer--buttons table-pagination-buttons"></div>
                        </div>
                    </article>
                </c:when>
                <c:otherwise>
                    <article class="card empty-state flex-col align-center justify-center" style="min-height: 400px; height: 100%;">
                        <div class="empty-state__icon">
                            <i data-lucide="bar-chart-3"></i>
                        </div>
                        <h3 class="empty-state__title">No projection data</h3>
                        <p class="empty-state__desc">
                            Enter your financial details in the form on the left and click <strong>Calculate</strong> to see your compound growth projection and year-by-year breakdown.
                        </p>
                    </article>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</main>

<footer class="site-footer">
    <p>FinTrack &middot; University Project &middot; 2026</p>
</footer>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
<script src="${ctx}/static/js/table-control.js"></script>

<script>
    lucide.createIcons();
</script>

<c:if test="${hasData}">
<script>
(function () {
    const ctxEl = document.getElementById('compoundChart');
    const DataObject = [
        <c:forEach var="r" items="${compoundList}" varStatus="s">${r.totalValue}<c:if test="${not s.last}">,</c:if></c:forEach>
    ];
    const LabelObject = [
        <c:forEach var="r" items="${compoundList}" varStatus="s">"Year ${s.index + 1}"<c:if test="${not s.last}">,</c:if></c:forEach>
    ];
    const ContributionsObject = [
        <c:set var="cc2" value="${initialCapital}"/>
        <c:forEach var="r" items="${compoundList}" varStatus="s"><c:set var="cc2" value="${cc2 + (monthlyContribution * 12)}"/>${cc2}<c:if test="${not s.last}">,</c:if></c:forEach>
    ];

    new Chart(ctxEl, {
        type: 'line',
        data: {
            labels: LabelObject,
            datasets: [
                {
                    label: 'Contributions',
                    data: ContributionsObject,
                    borderColor: '#CBD5E1',
                    backgroundColor: 'rgba(203, 213, 225, 0.5)',
                    borderWidth: 2,
                    tension: 0.3,
                    fill: true,
                    pointBackgroundColor: '#FFFFFF',
                    pointBorderColor: '#CBD5E1',
                    pointHoverBackgroundColor: '#CBD5E1',
                    pointHoverBorderColor: '#FFFFFF'
                },
                {
                    label: 'Compound Value',
                    data: DataObject,
                    borderColor: '#0F766E',
                    backgroundColor: 'rgba(15, 118, 110, 0.15)',
                    borderWidth: 2,
                    tension: 0.3,
                    fill: '-1',
                    pointBackgroundColor: '#FFFFFF',
                    pointBorderColor: '#0F766E',
                    pointHoverBackgroundColor: '#0F766E',
                    pointHoverBorderColor: '#FFFFFF'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: {
                    ticks: { font: { family: "'Inter', sans-serif" }, color: '#64748B' },
                    grid: { display: false },
                    border: { color: '#ECECEC' }
                },
                y: {
                    beginAtZero: true,
                    ticks: { font: { family: "'Inter', sans-serif" }, color: '#64748B' },
                    grid: { color: '#ECECEC', drawBorder: false },
                    border: { display: false }
                }
            }
        }
    });
})();
</script>
</c:if>

</body>
</html>
