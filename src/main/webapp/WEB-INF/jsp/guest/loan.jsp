<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String ctx = request.getContextPath();
    java.time.LocalDate now = java.time.LocalDate.now();
    int defMonth = now.getMonthValue();
    int defYear  = now.getYear();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack &middot; Loan Amortization</title>

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

<main class="section" style="padding-left: 80px; padding-right: 80px;">

    <a href="<%= ctx %>/guest/report" class="calc-back">
        <i data-lucide="arrow-left"></i>
        Back to calculators
    </a>

    <h1>Loan Amortization</h1>
    <p class="muted" style="margin-bottom: var(--space-6);">
        Estimate monthly payments, total interest, and the full repayment schedule.
    </p>

    <div class="calc-split">

        <!-- LEFT: Form — uses .card.card--padded + .card-header/.card-title/.card-subtitle -->
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
                                   min="1" step="100" placeholder="25000" value="25000">
                        </div>
                        <span class="form-error" id="err-principal"></span>
                    </div>

                    <div class="form-field" id="field-rate">
                        <label class="form-label" for="annualRate">Annual interest rate</label>
                        <div class="input-adornment input-adornment--suffix">
                            <input class="form-input" type="number" id="annualRate"
                                   min="0" max="100" step="0.1" placeholder="6.5" value="6.5">
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
                                   min="1" step="1" placeholder="60" value="60">
                            <span class="input-adornment__suffix">months</span>
                        </div>
                        <span class="form-error" id="err-months"></span>
                    </div>

                    <div class="form-field">
                        <label class="form-label">Start month</label>
                        <div style="display:flex; gap:var(--space-2);">
                            <select class="form-select" id="startMonth" style="flex:1;">
                                <option value="1"  <%= defMonth==1  ? "selected":"" %>>Jan</option>
                                <option value="2"  <%= defMonth==2  ? "selected":"" %>>Feb</option>
                                <option value="3"  <%= defMonth==3  ? "selected":"" %>>Mar</option>
                                <option value="4"  <%= defMonth==4  ? "selected":"" %>>Apr</option>
                                <option value="5"  <%= defMonth==5  ? "selected":"" %>>May</option>
                                <option value="6"  <%= defMonth==6  ? "selected":"" %>>Jun</option>
                                <option value="7"  <%= defMonth==7  ? "selected":"" %>>Jul</option>
                                <option value="8"  <%= defMonth==8  ? "selected":"" %>>Aug</option>
                                <option value="9"  <%= defMonth==9  ? "selected":"" %>>Sep</option>
                                <option value="10" <%= defMonth==10 ? "selected":"" %>>Oct</option>
                                <option value="11" <%= defMonth==11 ? "selected":"" %>>Nov</option>
                                <option value="12" <%= defMonth==12 ? "selected":"" %>>Dec</option>
                            </select>
                            <select class="form-select" id="startYear" style="flex:1;">
                                <% for (int y = defYear; y <= defYear + 5; y++) { %>
                                <option value="<%= y %>" <%= y==defYear ? "selected":"" %>><%= y %></option>
                                <% } %>
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

        <!-- RIGHT: results — replaced by JS; initial empty state uses .card.card--empty -->
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

    <!-- Amortization schedule -->
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
<script src="<%= ctx %>/static/js/loan-calculator.js"></script>
</body>
</html>
