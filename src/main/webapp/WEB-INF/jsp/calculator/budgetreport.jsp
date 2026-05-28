<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="it.unipd.dei.webapp.model.BudgetResult" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<%@ page import="java.math.BigDecimal" %>
<%
    String ctxPath = request.getContextPath();

    BudgetResult result = (BudgetResult) request.getAttribute("budgetResult");
    boolean hasData = result != null;

    // Get parameters submitted by the form
    String[] incomeCategories = request.getParameterValues("incomeCategory");
    String[] incomeAmounts = request.getParameterValues("incomeAmount");
    String[] expenseCategories = request.getParameterValues("expenseCategory");
    String[] expenseAmounts = request.getParameterValues("expenseAmount");
    String savingsGoalStr = request.getParameter("savingsGoal");

    double savingsGoal = 0.0;
    if (savingsGoalStr != null && !savingsGoalStr.trim().isEmpty()) {
        try {
            savingsGoal = Double.parseDouble(savingsGoalStr);
        } catch (NumberFormatException e) {
            savingsGoal = 0.0;
        }
    }

    double totalIncome = 0.0;
    double totalExpenses = 0.0;
    double netBalance = 0.0;
    Map<String, BigDecimal> expensesMap = new LinkedHashMap<>();
    double actualSavings = 0.0;
    double savingsRate = 0.0;
    
    double budgetHealth = 100.0;
    String healthStatus = "Excellent";
    String healthStatusClass = "status-text-positive";

    if (hasData) {
        totalIncome = result.getTotalIncome().doubleValue();
        totalExpenses = result.getTotalExpenses().doubleValue();
        netBalance = result.getNetBalance().doubleValue();

        for (Map.Entry<String, BigDecimal> entry : result.getExpensesByCategory().entrySet()) {
            expensesMap.put(entry.getKey(), entry.getValue());
        }

        actualSavings = netBalance > 0 ? netBalance : 0.0;
        savingsRate = totalIncome > 0 ? (actualSavings / totalIncome) * 100 : 0.0;

        if (totalIncome > 0) {
            double expenseRatio = (totalExpenses / totalIncome) * 100;
            budgetHealth = 100.0 - (expenseRatio * 0.8);
            
            if (savingsGoal > 0 && netBalance < savingsGoal) {
                double deficit = savingsGoal - netBalance;
                double deficitRatio = (deficit / savingsGoal) * 100;
                budgetHealth -= (deficitRatio * 0.3);
            }
            
            if (budgetHealth < 0) budgetHealth = 0;
            if (budgetHealth > 100) budgetHealth = 100;
            
            if (budgetHealth >= 80) {
                healthStatus = "Excellent";
                healthStatusClass = "status-text-positive";
            } else if (budgetHealth >= 60) {
                healthStatus = "Good, needs attention";
                healthStatusClass = "status-text-warning";
            } else {
                healthStatus = "Critical, high spending";
                healthStatusClass = "status-text-negative";
            }
        } else {
            budgetHealth = 0.0;
            healthStatus = "No income";
            healthStatusClass = "status-text-muted";
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack - Budget Report</title>

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
        <a href="<%= ctxPath %>/guest/report" class="muted breadcrumb-link">Calculators</a>
        <span class="muted">&rsaquo;</span>
        <strong>Budget analysis</strong>
    </nav>

    <datalist id="incomeCategories">
        <option value="Salary"></option>
        <option value="Freelance"></option>
        <option value="Investments"></option>
        <option value="Gifts"></option>
        <option value="Other"></option>
    </datalist>

    <datalist id="expenseCategories">
        <option value="Rent"></option>
        <option value="Groceries"></option>
        <option value="Utilities"></option>
        <option value="Transport"></option>
        <option value="Entertainment"></option>
        <option value="Dining Out"></option>
        <option value="Healthcare"></option>
        <option value="Insurance"></option>
        <option value="Other"></option>
    </datalist>

    <header class="mb-10">
        <h1 class="text-3xl mb-2">Budget analysis</h1>
        <p class="text-lg muted m-0">Analyze your monthly inflows and outflows, savings progression, and budget structure.</p>
    </header>

    <div class="calc-layout">
        
        <!-- LEFT COLUMN: INPUT FORM -->
        <aside>
            <article class="card">
                <div class="flex-row gap-3 mb-6">
                    <div class="form-header-icon">
                        <i data-lucide="wallet"></i>
                    </div>
                    <h2 class="text-2xl font-semibold mb-0">Budget analysis</h2>
                </div>

                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="alert alert-error mb-4">
                        <i data-lucide="alert-circle"></i>
                        <span><%= HtmlUtil.escape((String) request.getAttribute("errorMessage")) %></span>
                    </div>
                <% } %>

                <form class="form" action="<%= ctxPath %>/report/budget" method="POST">
                    
                    <!-- Monthly Income Section -->
                    <div class="form-field mb-6">
                        <h3 class="text-sm font-semibold mb-3 flex-row gap-2">
                            <i data-lucide="trending-up" class="text-primary"></i> Monthly Income
                        </h3>
                        <div id="income-rows">
                            <%
                                boolean hasIncomes = incomeCategories != null && incomeCategories.length > 0;
                                int incomeLen = hasIncomes ? incomeCategories.length : 1;
                                for (int i = 0; i < incomeLen; i++) {
                                    String cat = hasIncomes ? incomeCategories[i] : "";
                                    String amt = (hasIncomes && incomeAmounts != null && i < incomeAmounts.length) ? incomeAmounts[i] : "";
                            %>
                            <div class="calc-dynamic-row">
                                <input type="text" name="incomeCategory" class="form-input" placeholder="e.g. Salary" list="incomeCategories" value="<%= HtmlUtil.escape(cat) %>" required>
                                <div class="form-input-group form-input-group--prefix-calc">
                                    <span class="input-prefix-calc">€</span>
                                    <input type="text" name="incomeAmount" class="form-input" placeholder="0.00" pattern="^[0-9]+([.,][0-9]+)?$" title="Please enter a valid amount (e.g. 100 or 100.50)" value="<%= HtmlUtil.escape(amt) %>" required>
                                </div>
                                <button type="button" class="btn btn-ghost btn-icon btn-sm" onclick="removeRow(this)">
                                    <i data-lucide="trash-2"></i>
                                </button>
                            </div>
                            <% } %>
                        </div>
                        <div class="flex-col mt-2">
                            <button type="button" class="btn btn-ghost btn-sm btn-block" onclick="addRow('income')">
                                <i data-lucide="plus"></i> Add Income
                            </button>
                        </div>
                    </div>

                    <!-- Savings Goal Section -->
                    <div class="form-field mb-6">
                        <label class="form-label flex-row gap-2">
                            <i data-lucide="target" class="text-primary"></i> Savings goal
                        </label>
                        <div class="form-input-group form-input-group--prefix-calc">
                            <span class="input-prefix-calc">€</span>
                            <input type="text" name="savingsGoal" class="form-input" value="<%= savingsGoal > 0 ? String.format(Locale.US, "%.2f", savingsGoal) : "" %>" placeholder="0.00" pattern="^[0-9]+([.,][0-9]+)?$" title="Please enter a valid amount (e.g. 100 or 100.50)">
                        </div>
                        <span class="form-help">Your target savings for the month</span>
                    </div>

                    <!-- Expenses Section -->
                    <div class="form-field mb-6">
                        <h3 class="text-sm font-semibold mb-3 flex-row gap-2">
                            <i data-lucide="receipt" class="text-primary"></i> Expenses
                        </h3>
                        <div id="expense-rows">
                            <%
                                boolean hasExpenses = expenseCategories != null && expenseCategories.length > 0;
                                int expenseLen = hasExpenses ? expenseCategories.length : 1;
                                for (int i = 0; i < expenseLen; i++) {
                                    String cat = hasExpenses ? expenseCategories[i] : "";
                                    String amt = (hasExpenses && expenseAmounts != null && i < expenseAmounts.length) ? expenseAmounts[i] : "";
                            %>
                            <div class="calc-dynamic-row">
                                <input type="text" name="expenseCategory" class="form-input" placeholder="e.g. Rent, Groceries" list="expenseCategories" value="<%= HtmlUtil.escape(cat) %>" required>
                                <div class="form-input-group form-input-group--prefix-calc">
                                    <span class="input-prefix-calc">€</span>
                                    <input type="text" name="expenseAmount" class="form-input" placeholder="0.00" pattern="^[0-9]+([.,][0-9]+)?$" title="Please enter a valid amount (e.g. 100 or 100.50)" value="<%= HtmlUtil.escape(amt) %>" required>
                                </div>
                                <button type="button" class="btn btn-ghost btn-icon btn-sm" onclick="removeRow(this)">
                                    <i data-lucide="trash-2"></i>
                                </button>
                            </div>
                            <% } %>
                        </div>
                        <div class="flex-col mt-2">
                            <button type="button" class="btn btn-ghost btn-sm btn-block" onclick="addRow('expense')">
                                <i data-lucide="plus"></i> Add Expense
                            </button>
                        </div>
                    </div>

                    <!-- Submit Button -->
                    <div class="form-field mt-6">
                        <button type="submit" class="btn btn-primary btn-block">
                            <i data-lucide="calculator"></i> Calculate Budget
                        </button>
                    </div>
                </form>
            </article>

            <!-- Budget Health Advice Card -->
            <% if (hasData) { %>
            <article class="card flex-row gap-3 advice-card">
                <div class="form-header-icon advice-icon">
                    <i data-lucide="info"></i>
                </div>
                <div>
                    <h4 class="text-sm font-semibold m-0 advice-title">Budget health</h4>
                    <p class="text-sm muted m-0 advice-text">
                        <% if (netBalance >= savingsGoal) { %>
                            Your savings goal is realistic and fully met! You have a healthy surplus of €<%= String.format(Locale.US, "%,.0f", netBalance - savingsGoal) %> this month.
                        <% } else if (netBalance > 0) { %>
                            Your savings goal is not fully met. Try reducing your largest expense categories to save €<%= String.format(Locale.US, "%,.0f", savingsGoal - netBalance) %> more.
                        <% } else { %>
                            You are in a budget deficit! Your expenses exceed your monthly income by €<%= String.format(Locale.US, "%,.0f", -netBalance) %>. You need to reduce spending immediately.
                        <% } %>
                    </p>
                </div>
            </article>
            <% } %>
        </aside>

        <!-- RIGHT COLUMN: REPORT RESULTS -->
        <% if (hasData) { %>
        <div class="flex-col gap-6">
            
            <!-- Summary Metric Cards -->
            <div class="grid grid--3">
                <!-- Remaining Income Card -->
                <article class="card">
                    <div class="flex-row gap-2 mb-3 muted text-sm">
                        <div class="badge badge-income badge-icon">
                            <i data-lucide="arrow-up-right"></i>
                        </div>
                        <span>Remaining income</span>
                    </div>
                    <h3 class="text-3xl font-normal mb-1">
                        <%= String.format("€%,.0f", netBalance) %>
                    </h3>
                    <p class="text-xs muted m-0">After expenses</p>
                </article>

                <!-- Savings Rate Card -->
                <article class="card">
                    <div class="flex-row gap-2 mb-3 muted text-sm">
                        <div class="badge badge-income badge-icon">
                            <i data-lucide="percent"></i>
                        </div>
                        <span>Savings rate</span>
                    </div>
                    <h3 class="text-3xl font-normal mb-1">
                        <%= String.format("%.1f%%", savingsRate) %>
                    </h3>
                    <p class="text-xs muted m-0">Of monthly income</p>
                </article>

                <!-- Budget Health Card -->
                <article class="card">
                    <div class="flex-row gap-2 mb-3 muted text-sm">
                        <div class="badge badge-wallet-cash badge-icon badge-warning">
                            <i data-lucide="activity"></i>
                        </div>
                        <span>Budget health</span>
                    </div>
                    <h3 class="text-3xl font-normal mb-1">
                        <%= String.format("%.0f%%", budgetHealth) %>
                    </h3>
                    <p class="text-xs m-0 font-medium <%= healthStatusClass %>"><%= healthStatus %></p>
                </article>
            </div>

            <!-- Budget Breakdown Card -->
            <article class="card">
                <div class="mb-6">
                    <h3 class="card-title m-0">Budget breakdown</h3>
                    <p class="card-subtitle m-0">How your monthly income is distributed.</p>
                </div>
                <div class="grid grid--2 align-center">
                    <!-- Progress Bars -->
                    <div class="flex-col gap-6">
                        <!-- Expenses Progress -->
                        <div>
                            <div class="flex-row mb-1 justify-between">
                                <span class="text-sm font-semibold">Expenses</span>
                                <span class="text-sm font-semibold"><%= String.format("€%,.2f", totalExpenses) %></span>
                            </div>
                            <div class="progress-track">
                                <div class="progress-bar progress-bar--negative" style="width: <%= totalIncome > 0 ? Math.min(100, (totalExpenses / totalIncome) * 100) : 0 %>%;"></div>
                            </div>
                            <span class="text-xs muted"><%= String.format("%.1f%%", totalIncome > 0 ? (totalExpenses / totalIncome) * 100 : 0) %> of income</span>
                        </div>

                        <!-- Savings Progress -->
                        <div>
                            <div class="flex-row mb-1 justify-between">
                                <span class="text-sm font-semibold">Savings / Net balance</span>
                                <span class="text-sm font-semibold"><%= String.format("€%,.2f", netBalance) %></span>
                            </div>
                            <div class="progress-track">
                                <div class="progress-bar progress-bar--warning" style="width: <%= totalIncome > 0 ? Math.min(100, (netBalance > 0 ? (netBalance / totalIncome) * 100 : 0)) : 0 %>%;"></div>
                            </div>
                            <span class="text-xs muted"><%= String.format("%.1f%%", totalIncome > 0 ? (netBalance / totalIncome) * 100 : 0) %> of income</span>
                        </div>

                        <% if (savingsGoal > 0) { %>
                        <!-- Savings Goal Progress -->
                        <div>
                            <div class="flex-row mb-1 justify-between">
                                <span class="text-sm font-semibold">Savings goal</span>
                                <span class="text-sm font-semibold"><%= String.format("€%,.2f", savingsGoal) %></span>
                            </div>
                            <div class="progress-track">
                                <div class="progress-bar progress-bar--primary" style="width: <%= totalIncome > 0 ? Math.min(100, (savingsGoal / totalIncome) * 100) : 0 %>%;"></div>
                            </div>
                            <span class="text-xs muted"><%= String.format("%.1f%%", totalIncome > 0 ? (savingsGoal / totalIncome) * 100 : 0) %> of income</span>
                        </div>
                        <% } %>
                    </div>

                    <!-- Donut Chart -->
                    <div class="center donut-wrapper">
                        <div class="donut-chart-container">
                            <canvas id="budgetDonutChart" width="200" height="200"></canvas>
                            <div class="donut-chart-center-text">
                                <span class="donut-val"><%= String.format("%.0f%%", totalIncome > 0 ? (netBalance > 0 ? (netBalance / totalIncome) * 100 : 0) : 0) %></span>
                                <span class="donut-lbl">free</span>
                            </div>
                        </div>
                    </div>
                </div>
            </article>

            <!-- Expenses Breakdown Table Card -->
            <article class="card card--table">
                <div class="card-header-border">
                    <h3 class="card-title m-0">Expenses breakdown</h3>
                </div>
                <div class="table-wrapper">
                    <table class="table">
                        <thead>
                            <tr>
                                <th class="pl-6">Category</th>
                                <th class="num">Amount</th>
                                <th class="pr-6 num">% of Expenses</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                                for (Map.Entry<String, BigDecimal> entry : expensesMap.entrySet()) { 
                                    String categoryName = entry.getKey();
                                    double amount = entry.getValue().doubleValue();
                                    double percentage = totalExpenses > 0 ? (amount / totalExpenses) * 100 : 0.0;
                            %>
                            <tr>
                                <td class="pl-6 font-semibold"><%= HtmlUtil.escape(categoryName) %></td>
                                <td class="num"><%= String.format("€%,.2f", amount) %></td>
                                <td class="pr-6 num"><%= String.format("%.1f%%", percentage) %></td>
                            </tr>
                            <% 
                                } 
                            %>
                            <% if (expensesMap.isEmpty()) { %>
                            <tr>
                                <td colspan="3" class="center muted mt-4 mb-4 table-empty-cell">No expenses recorded.</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </article>
        </div>
        <% } else { %>
        <!-- EMPTY STATE -->
        <article class="card calc-empty-state">
            <div class="calc-empty-icon">
                <i data-lucide="bar-chart-3"></i>
            </div>
            <h3 class="calc-empty-title">No projection data</h3>
            <p class="calc-empty-text">
                Enter your financial details in the form on the left and click <strong>Calculate Budget</strong> to see your compound growth projection and year-by-year breakdown.
            </p>
        </article>
        <% } %>

    </div>
</main>

<footer class="site-footer mt-10">
    <p>FinTrack &middot; University Project &middot; 2026</p>
</footer>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>

<script>
    // Initialize Lucide icons
    lucide.createIcons();

    // JavaScript functions for dynamic rows
    function addRow(type) {
        const container = document.getElementById(type + '-rows');
        const row = document.createElement('div');
        row.className = 'calc-dynamic-row';

        let catName, amtName, catPlaceholder, catList;
        if (type === 'income') {
            catName = 'incomeCategory';
            amtName = 'incomeAmount';
            catPlaceholder = 'e.g. Salary';
            catList = 'incomeCategories';
        } else if (type === 'expense') {
            catName = 'expenseCategory';
            amtName = 'expenseAmount';
            catPlaceholder = 'e.g. Rent, Groceries';
            catList = 'expenseCategories';
        }

        row.innerHTML = 
            '<input type="text" name="' + catName + '" class="form-input" placeholder="' + catPlaceholder + '" list="' + catList + '" required>' +
            '<div class="form-input-group form-input-group--prefix-calc">' +
                '<span class="input-prefix-calc">€</span>' +
                '<input type="text" name="' + amtName + '" class="form-input" placeholder="0.00" pattern="^[0-9]+([.,][0-9]+)?$" title="Please enter a valid amount (e.g. 100 or 100.50)" required>' +
            '</div>' +
            '<button type="button" class="btn btn-ghost btn-icon btn-sm" onclick="removeRow(this)">' +
                '<i data-lucide="trash-2"></i>' +
            '</button>';

        container.appendChild(row);
        lucide.createIcons();
    }

    function removeRow(btn) {
        const row = btn.closest('.calc-dynamic-row');
        const container = row.parentElement;
        if (container.querySelectorAll('.calc-dynamic-row').length > 1) {
            row.remove();
        } else {
            row.querySelectorAll('input[type="text"]').forEach(input => input.value = '');
        }
    }

    // Replace comma with dot on form submission to prevent server-side errors
    document.querySelector('form.form').addEventListener('submit', function(e) {
        this.querySelectorAll('input[name="incomeAmount"], input[name="expenseAmount"], input[name="savingsGoal"]').forEach(input => {
            if (input.value) {
                input.value = input.value.replace(',', '.').trim();
            }
        });
    });

    // Chart.js Donut implementation
    <% if (hasData) { %>
    (function() {
        const ctx = document.getElementById('budgetDonutChart').getContext('2d');
        const expenseVal = parseFloat('<%= totalExpenses %>') || 0;
        const savingsVal = parseFloat('<%= actualSavings %>') || 0;
        
        let dataVals = [expenseVal, savingsVal];
        
        // If everything is zero, show a placeholder segment so chart doesn't crash
        if (expenseVal === 0 && savingsVal === 0) {
            dataVals = [0, 1]; // represent empty state
        }

        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Expenses', 'Savings'],
                datasets: [{
                    data: dataVals,
                    backgroundColor: [
                        '#ccfbf1', // Light teal/green for Expenses
                        '#FDE047'  // Yellow for Savings (free)
                    ],
                    borderWidth: 0,
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: false,
                cutout: '75%',
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        enabled: (expenseVal > 0 || savingsVal > 0),
                        callbacks: {
                            label: function(context) {
                                return ' ' + context.label + ': €' + context.raw.toFixed(2);
                            }
                        }
                    }
                }
            }
        });
    })();
    <% } %>
</script>

</body>
</html>
