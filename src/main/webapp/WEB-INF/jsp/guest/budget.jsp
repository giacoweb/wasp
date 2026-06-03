<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack &middot; Budget Analysis</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

    <link rel="stylesheet" href="<%= ctx %>/static/css/tokens.css">
    <link rel="stylesheet" href="<%= ctx %>/static/css/base.css">
    <link rel="stylesheet" href="<%= ctx %>/static/css/layout.css">
    <link rel="stylesheet" href="<%= ctx %>/static/css/components.css">
    <link rel="stylesheet" href="<%= ctx %>/static/css/forms.css">
    <link rel="stylesheet" href="<%= ctx %>/static/css/tables.css">
    <link rel="stylesheet" href="<%= ctx %>/static/css/guest.css">
</head>
<body data-context-path="<%= ctx %>">

<header class="site-header">
    <a href="<%= ctx %>/" class="brand">
        <i data-lucide="triangle"></i>
        FinTrack
    </a>
    <nav class="site-header__nav" aria-label="Primary">
        <a href="<%= ctx %>/guest/report" class="guest-session-btn">
            <i data-lucide="alert-triangle"></i>
            Guest session
        </a>
        <a href="<%= ctx %>/login"    class="btn btn-ghost">Log in</a>
        <a href="<%= ctx %>/register" class="btn btn-primary">Create account</a>
    </nav>
</header>

<main class="container section">

    <nav aria-label="breadcrumb">
        <p class="breadcrumb">
            <a href="<%= ctx %>/guest/report">Calculators</a>
            <span>&rsaquo; Budget analysis</span>
        </p>
    </nav>

    <div class="calc-split">

        <!-- LEFT: Form -->
        <div class="card card--padded">
            <div class="card-header">
                <div class="calc-form-icon"><i data-lucide="minus-square"></i></div>
                <div class="card-title">Budget analysis</div>
            </div>

            <div class="form">

                <div class="form-field" id="field-income">
                    <label class="form-label" for="monthlyIncome">Monthly income</label>
                    <div class="input-adornment input-adornment--prefix">
                        <span class="input-adornment__prefix">&euro;</span>
                        <input class="form-input" type="number" id="monthlyIncome"
                               min="1" step="100" placeholder="3200" value="3200">
                    </div>
                    <span class="form-help">Total net income</span>
                    <span class="form-error" id="err-income"></span>
                </div>

                <div class="form-field" id="field-fixed">
                    <label class="form-label" for="fixedExpenses">Fixed expenses</label>
                    <div class="input-adornment input-adornment--prefix">
                        <span class="input-adornment__prefix">&euro;</span>
                        <input class="form-input" type="number" id="fixedExpenses"
                               min="0" step="50" placeholder="1450" value="1450">
                    </div>
                    <span class="form-help">Rent, bills, subscriptions</span>
                    <span class="form-error" id="err-fixed"></span>
                </div>

                <div class="form-field" id="field-variable">
                    <label class="form-label" for="variableExpenses">Variable expenses</label>
                    <div class="input-adornment input-adornment--prefix">
                        <span class="input-adornment__prefix">&euro;</span>
                        <input class="form-input" type="number" id="variableExpenses"
                               min="0" step="50" placeholder="820" value="820">
                    </div>
                    <span class="form-help">Food, transport, shopping</span>
                    <span class="form-error" id="err-variable"></span>
                </div>

                <div class="form-field" id="field-savings">
                    <label class="form-label" for="savingsGoal">Savings goal</label>
                    <div class="input-adornment input-adornment--prefix">
                        <span class="input-adornment__prefix">&euro;</span>
                        <input class="form-input" type="number" id="savingsGoal"
                               min="0" step="50" placeholder="500" value="500">
                    </div>
                    <span class="form-help">Target monthly savings</span>
                    <span class="form-error" id="err-savings"></span>
                </div>

                <button type="button" class="btn btn-primary btn-block" id="btn-analyze">
                    <i data-lucide="square"></i>
                    Analyze budget
                </button>

                <%-- Info box revealed after analysis --%>
                <div class="calc-info-box" id="info-box" hidden>
                    <div class="calc-info-box__icon">i</div>
                    <div>
                        <div class="calc-info-box__title">Budget health</div>
                        <p class="calc-info-box__text" id="info-box-text"></p>
                    </div>
                </div>

            </div>
        </div>

        <!-- RIGHT: KPI strip + Breakdown + Category review stacked in a column -->
        <div class="calc-right-col">

            <div class="calc-kpi-strip" id="kpi-strip" hidden>
                <div class="card kpi-card">
                    <div class="kpi-card__header">
                        <div class="kpi-card__icon kpi-card__icon--teal"><i data-lucide="trending-up"></i></div>
                        <span class="kpi-card__label">Remaining income</span>
                    </div>
                    <div class="kpi-card__value" id="kpi-remaining">&mdash;</div>
                    <div class="kpi-card__caption">After expenses</div>
                </div>
                <div class="card kpi-card">
                    <div class="kpi-card__header">
                        <div class="kpi-card__icon kpi-card__icon--green"><i data-lucide="percent"></i></div>
                        <span class="kpi-card__label">Savings rate</span>
                    </div>
                    <div class="kpi-card__value" id="kpi-savings-rate">&mdash;</div>
                    <div class="kpi-card__caption">Of monthly income</div>
                </div>
                <div class="card kpi-card">
                    <div class="kpi-card__header">
                        <div class="kpi-card__icon kpi-card__icon--amber"><i data-lucide="activity"></i></div>
                        <span class="kpi-card__label">Budget health</span>
                    </div>
                    <div class="kpi-card__value" id="kpi-health">&mdash;</div>
                    <div class="kpi-card__caption" id="kpi-health-label">&mdash;</div>
                </div>
            </div>

            <div id="breakdown-panel">
                <div class="card card--empty">
                    <div class="empty-state__icon"><i data-lucide="pie-chart"></i></div>
                    <p class="empty-state__desc">
                        Fill in your budget numbers and click <strong>Analyze budget</strong> to see the breakdown.
                    </p>
                </div>
            </div>

            <!-- Category review table -->
            <div id="review-section" hidden>
                <div class="card" id="review-table">
                    <div class="projection-title">Category review</div>
                    <div class="table-wrapper">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Category</th>
                                    <th class="num">Amount</th>
                                    <th class="num">% of income</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody id="review-body"></tbody>
                        </table>
                    </div>
                </div>
            </div>

        </div>

    </div>

</main>

<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
<script>window.lucide && lucide.createIcons();</script>
<script src="<%= ctx %>/static/js/budget-calculator.js"></script>
</body>
</html>
