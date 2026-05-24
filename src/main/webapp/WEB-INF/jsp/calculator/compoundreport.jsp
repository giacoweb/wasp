<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String ctxPath = request.getContextPath();

    List<CompoundResult> result = (List<CompoundResult>) request.getAttribute("compoundList");
    boolean hasData = (result != null && !result.isEmpty());

    Double initialCapital = (Double) request.getAttribute("initialCapital");
    Double monthlyContribution = (Double) request.getAttribute("monthlyContribution");
    Double annualRate = (Double) request.getAttribute("annualRate");

    String initialCapStr = initialCapital != null ? String.format(Locale.US, "%.0f", initialCapital) : HtmlUtil.escape(request.getParameter("initialCapital"));
    String monthlyContStr = monthlyContribution != null ? String.format(Locale.US, "%.0f", monthlyContribution) : HtmlUtil.escape(request.getParameter("monthlyContribution"));
    String annualRateStr = annualRate != null ? String.format(Locale.US, "%.0f", annualRate) : HtmlUtil.escape(request.getParameter("annualRate"));
    String durationStr = hasData ? String.valueOf(result.size()) : HtmlUtil.escape(request.getParameter("years"));

    String stringResult = "[]";
    String stringYear = "[]";
    String stringContributions = "[]";
    double futureValue = 0.0;
    double totalContributed = 0.0;
    double interestEarned = 0.0;

    if (hasData) {
        stringResult = "[";
        stringYear = "[";
        stringContributions = "[";
        double currentContributed = initialCapital;
        for( int i = 0; i < result.size(); i++) {
            currentContributed += (monthlyContribution * 12);
            if (i== result.size()-1) {
                stringResult += result.get(i).getTotalValue();
                stringYear += "\"Year " + (i+1)+"\"";
                stringContributions += currentContributed;
            } else {
                stringResult += result.get(i).getTotalValue()+",";
                stringYear += "\"Year " + (i+1) + "\",";
                stringContributions += currentContributed + ",";
            }
        }
        stringResult+="]";
        stringYear+="]";
        stringContributions+="]";

        futureValue = result.get(result.size()-1).getTotalValue().doubleValue();
        totalContributed = initialCapital + (monthlyContribution * 12 * result.size());
        interestEarned = futureValue - totalContributed;
    }
%>
<%@ page import="java.util.*" %>
<%@ page import="it.unipd.dei.webapp.model.CompoundResult" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack - Compound Report</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap">

    <link rel="stylesheet" href="<%= ctxPath %>/static/css/tokens.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/base.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/layout.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/components.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/tables.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/forms.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/calculator.css">


</head>
<body>

<header class="site-header">
    <a href="<%= ctxPath %>/" class="brand">
        <i data-lucide="triangle"></i> FinTrack
    </a>
    <nav class="site-header__nav flex-row" aria-label="Primary">
        <button class="btn btn-ghost btn-guest-session">
            <i data-lucide="beaker"></i> Guest session
        </button>
        <div class="nav-separator"></div>
        <a href="<%= ctxPath %>/login" class="btn btn-ghost">Log in</a>
        <a href="<%= ctxPath %>/register" class="btn btn-primary">Create account</a>
    </nav>
</header>

<main class="container container--wide section">

    <nav class="flex-row gap-2 mb-6 text-sm">
        <a href="<%= ctxPath %>/guest/report" class="muted">Calculators</a>
        <span class="muted">&rsaquo;</span>
        <strong>Compound interest</strong>
    </nav>

    <div class="calc-layout">
        
        <aside>
            <article class="card">
                <div class="flex-row gap-3 mb-6">
                    <div class="form-header-icon">
                        <i data-lucide="arrow-up-right"></i>
                    </div>
                    <h2 class="text-2xl font-semibold mb-0">Compound interest</h2>
                </div>

                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="calc-error-alert">
                        <i data-lucide="alert-circle"></i>
                        <span><%= HtmlUtil.escape((String) request.getAttribute("errorMessage")) %></span>
                    </div>
                <% } %>

                <form class="form" action="<%= ctxPath %>/report/compound" method="POST">
                    <div class="form-field form-field--calc">
                        <label class="form-label">Initial amount</label>
                        <div class="form-input-group form-input-group--prefix-calc">
                            <span class="input-prefix-calc">€</span>
                            <input type="text" name="initialCapital" class="form-input form-input--calc" value="<%= initialCapStr %>" />
                        </div>
                        <span class="form-help">Starting investment</span>
                    </div>

                    <div class="form-field form-field--calc">
                        <label class="form-label">Monthly contribution</label>
                        <div class="form-input-group form-input-group--prefix-calc">
                            <span class="input-prefix-calc">€</span>
                            <input type="text" name="monthlyContribution" class="form-input form-input--calc" value="<%= monthlyContStr %>" />
                        </div>
                        <span class="form-help">Added every month</span>
                    </div>

                    <div class="form-field form-field--calc">
                        <label class="form-label">Annual return</label>
                        <div class="form-input-group form-input-group--suffix-calc">
                            <input type="text" name="annualRate" class="form-input form-input--calc" value="<%= annualRateStr %>" />
                            <span class="input-suffix-calc">%</span>
                        </div>
                        <span class="form-help">Expected annual return</span>
                    </div>

                    <div class="form-field form-field--calc">
                        <label class="form-label">Duration in years</label>
                        <input type="text" name="years" class="form-input form-input--calc" value="<%= durationStr %>" />
                        <span class="form-help">Investment horizon</span>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block btn-primary--calc" style="margin-top: var(--space-4);">
                        <i data-lucide="calculator"></i> Calculate
                    </button>
                </form>
            </article>

            <article class="card mt-4 flex-row gap-3" style="align-items: flex-start;">
                <div class="form-header-icon" style="width: 32px; height: 32px; background: var(--color-primary-tint); color: var(--color-primary); flex-shrink: 0;">
                    <i data-lucide="info" style="width: 16px; height: 16px;"></i>
                </div>
                <div>
                    <h4 class="text-sm m-0">Compounding effect</h4>
                    <p class="text-sm muted m-0">
                        With regular monthly contributions, your interest starts small but becomes a larger share of the final balance over time.
                    </p>
                </div>
            </article>
        </aside>

        <!-- MAIN CONTENT -->
        <div class="flex-col gap-6">

            <% if (hasData) { %>
            <div class="grid grid--3">
                <article class="card">
                    <div class="flex-row gap-2 mb-3 muted text-sm">
                        <div class="badge badge-primary badge-icon">
                            <i data-lucide="arrow-up-right"></i>
                        </div>
                        <span>Future value</span>
                    </div>
                    <h3 class="text-3xl text-primary font-normal mb-0">
                        <%= String.format("€%,.0f", futureValue) %>
                    </h3>
                    <p class="text-xs muted m-0">After <%= result.size() %> years</p>
                </article>

                <article class="card">
                    <div class="flex-row gap-2 mb-3 muted text-sm">
                        <div class="badge badge-neutral badge-icon">
                            <i data-lucide="plus"></i>
                        </div>
                        <span>Total contributed</span>
                    </div>
                    <h3 class="text-3xl font-normal mb-0">
                        <%= String.format("€%,.0f", totalContributed) %>
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
                        <%= String.format("€%,.0f", interestEarned) %>
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
                    <canvas id="compoundChart" style="max-height: 300px; width: 100%;"></canvas>
                </div>
            </article>

            <article class="card card--table table-paginated-container" data-per-page="12" data-item-name="years">
                <div class="card-header-border">
                    <h3 class="card-title m-0">Year-by-year projection</h3>
                </div>
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
                    <%
                        double currentContributed = initialCapital;
                        for (int i = 0; i < result.size(); i++) {
                            CompoundResult row = result.get(i);
                            currentContributed += (monthlyContribution * 12);
                            double currentInterest = row.getTotalValue().doubleValue() - currentContributed;
                    %>
                    <tr>
                        <td class="pl-6 muted">Year <%= row.getYear() %></td>
                        <td class="font-medium"><%= String.format("€%,.0f", currentContributed) %></td>
                        <td class="text-primary font-medium"><%= String.format("€%,.0f", currentInterest) %></td>
                        <td class="pr-6 font-semibold"><%= String.format("€%,.0f", row.getTotalValue().doubleValue()) %></td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
                <div class="table-footer">
                    <span class="text-sm muted table-pagination-info"></span>
                    <div class="table-footer--buttons table-pagination-buttons"></div>
                </div>
            </article>

            <% } else { %>
            <!-- EMPTY STATE -->
            <article class="card" style="display: flex; flex-direction: column; align-items: center; justify-content: center; padding: var(--space-12) var(--space-6); text-align: center; height: 100%; min-height: 400px;">
                <div style="width: 64px; height: 64px; background: var(--color-neutral-100); border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: var(--space-4); color: var(--color-text-muted);">
                    <i data-lucide="bar-chart-3" style="width: 32px; height: 32px;"></i>
                </div>
                <h3 style="font-size: 20px; font-weight: 600; margin-bottom: var(--space-2);">No projection data</h3>
                <p style="color: var(--color-text-muted); max-width: 400px; margin: 0 auto;">
                    Enter your financial details in the form on the left and click <strong>Calculate</strong> to see your compound growth projection and year-by-year breakdown.
                </p>
            </article>
            <% } %>

        </div>
    </div>
</main>

<footer class="site-footer">
    <p>FinTrack &middot; University Project &middot; 2026</p>
</footer>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
<script src="<%= ctxPath %>/static/js/table-control.js"></script>

<script>
    lucide.createIcons();

    <% if (hasData) { %>
    const ctx = document.getElementById('compoundChart');
    const DataObject = JSON.parse('<%= stringResult %>');
    const ContributionsObject = JSON.parse('<%= stringContributions %>');
    const LabelObject = JSON.parse('<%= stringYear %>');

    new Chart(ctx, {
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
            plugins: {
                legend: { display: false }
            },
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
    <% } %>
</script>

</body>
</html>