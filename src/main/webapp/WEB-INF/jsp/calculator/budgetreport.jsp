<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="hasData" value="${not empty budgetResult}"/>

<c:set var="savingsGoal" value="0"/>
<c:if test="${not empty param.savingsGoal}">
    <c:catch var="parseErr"><c:set var="savingsGoal" value="${param.savingsGoal + 0}"/></c:catch>
    <c:if test="${not empty parseErr}"><c:set var="savingsGoal" value="0"/></c:if>
</c:if>

<c:if test="${hasData}">
    <c:set var="totalIncome"   value="${budgetResult.totalIncome}"/>
    <c:set var="totalExpenses" value="${budgetResult.totalExpenses}"/>
    <c:set var="netBalance"    value="${budgetResult.netBalance}"/>
    <c:set var="expensesMap"   value="${budgetResult.expensesByCategory}"/>
    <c:set var="actualSavings" value="${netBalance > 0 ? netBalance : 0}"/>
    <c:set var="savingsRate"   value="${totalIncome > 0 ? (actualSavings / totalIncome) * 100 : 0}"/>

    <c:choose>
        <c:when test="${totalIncome > 0}">
            <c:set var="expenseRatio" value="${(totalExpenses / totalIncome) * 100}"/>
            <c:set var="budgetHealth" value="${100 - (expenseRatio * 0.8)}"/>
            <c:if test="${savingsGoal > 0 and netBalance < savingsGoal}">
                <c:set var="deficit"       value="${savingsGoal - netBalance}"/>
                <c:set var="deficitRatio"  value="${(deficit / savingsGoal) * 100}"/>
                <c:set var="budgetHealth"  value="${budgetHealth - (deficitRatio * 0.3)}"/>
            </c:if>
            <c:if test="${budgetHealth < 0}"><c:set var="budgetHealth" value="0"/></c:if>
            <c:if test="${budgetHealth > 100}"><c:set var="budgetHealth" value="100"/></c:if>

            <c:choose>
                <c:when test="${budgetHealth >= 80}">
                    <c:set var="healthStatus"      value="Excellent"/>
                    <c:set var="healthStatusClass" value="status-text-positive"/>
                </c:when>
                <c:when test="${budgetHealth >= 60}">
                    <c:set var="healthStatus"      value="Good, needs attention"/>
                    <c:set var="healthStatusClass" value="status-text-warning"/>
                </c:when>
                <c:otherwise>
                    <c:set var="healthStatus"      value="Critical, high spending"/>
                    <c:set var="healthStatusClass" value="status-text-negative"/>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <c:set var="budgetHealth"      value="0"/>
            <c:set var="healthStatus"      value="No income"/>
            <c:set var="healthStatusClass" value="status-text-muted"/>
        </c:otherwise>
    </c:choose>

    <c:set var="expensePct" value="${totalIncome > 0 ? (totalExpenses / totalIncome) * 100 : 0}"/>
    <c:if test="${expensePct > 100}"><c:set var="expensePct" value="100"/></c:if>

    <c:set var="savingsPct" value="${totalIncome > 0 and netBalance > 0 ? (netBalance / totalIncome) * 100 : 0}"/>
    <c:if test="${savingsPct > 100}"><c:set var="savingsPct" value="100"/></c:if>

    <c:set var="goalPct" value="${totalIncome > 0 and savingsGoal > 0 ? (savingsGoal / totalIncome) * 100 : 0}"/>
    <c:if test="${goalPct > 100}"><c:set var="goalPct" value="100"/></c:if>

    <c:set var="donutFreePct" value="${totalIncome > 0 and netBalance > 0 ? (netBalance / totalIncome) * 100 : 0}"/>
</c:if>

<c:set var="incomeCategories" value="${paramValues.incomeCategory}"/>
<c:set var="incomeAmounts"    value="${paramValues.incomeAmount}"/>
<c:set var="expenseCategories" value="${paramValues.expenseCategory}"/>
<c:set var="expenseAmounts"    value="${paramValues.expenseAmount}"/>
<c:set var="hasIncomeRows"  value="${not empty incomeCategories}"/>
<c:set var="hasExpenseRows" value="${not empty expenseCategories}"/>

<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack - Budget Report</title>

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
        <a href="${ctx}/guest/report">Calculators</a> / <span>Budget analysis</span>
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

        <aside>
            <article class="card">
                <div class="flex-row gap-3 mb-6">
                    <div class="form-header-icon">
                        <i data-lucide="wallet"></i>
                    </div>
                    <h2 class="text-2xl font-semibold mb-0">Budget analysis</h2>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error mb-4">
                        <i data-lucide="alert-circle"></i>
                        <span><c:out value="${errorMessage}"/></span>
                    </div>
                </c:if>

                <form class="form" action="${ctx}/report/budget" method="POST">

                    <div class="form-field mb-6">
                        <h3 class="text-sm font-semibold mb-3 flex-row gap-2">
                            <i data-lucide="trending-up" class="text-primary"></i> Monthly Income
                        </h3>
                        <div id="income-rows">
                            <c:choose>
                                <c:when test="${hasIncomeRows}">
                                    <c:forEach var="cat" items="${incomeCategories}" varStatus="s">
                                        <c:set var="amt" value="${incomeAmounts[s.index]}"/>
                                        <div class="calc-dynamic-row">
                                            <input type="text" name="incomeCategory" class="form-input" placeholder="e.g. Salary" list="incomeCategories" value="<c:out value='${cat}'/>" required>
                                            <div class="form-input-group form-input-group--prefix-calc">
                                                <span class="input-prefix-calc">€</span>
                                                <input type="text" name="incomeAmount" class="form-input" placeholder="0.00" pattern="^[0-9]+([.,][0-9]+)?$" title="Please enter a valid amount (e.g. 100 or 100.50)" value="<c:out value='${amt}'/>" required>
                                            </div>
                                            <button type="button" class="btn btn-ghost btn-icon btn-sm js-remove-budget-row">
                                                <i data-lucide="trash-2"></i>
                                            </button>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="calc-dynamic-row">
                                        <input type="text" name="incomeCategory" class="form-input" placeholder="e.g. Salary" list="incomeCategories" required>
                                        <div class="form-input-group form-input-group--prefix-calc">
                                            <span class="input-prefix-calc">€</span>
                                            <input type="text" name="incomeAmount" class="form-input" placeholder="0.00" pattern="^[0-9]+([.,][0-9]+)?$" title="Please enter a valid amount (e.g. 100 or 100.50)" required>
                                        </div>
                                        <button type="button" class="btn btn-ghost btn-icon btn-sm js-remove-budget-row">
                                            <i data-lucide="trash-2"></i>
                                        </button>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="flex-col mt-2">
                            <button type="button" class="btn btn-ghost btn-sm btn-block" data-add-budget-row="income">
                                <i data-lucide="plus"></i> Add Income
                            </button>
                        </div>
                    </div>

                    <div class="form-field mb-6">
                        <label class="form-label flex-row gap-2">
                            <i data-lucide="target" class="text-primary"></i> Savings goal
                        </label>
                        <div class="form-input-group form-input-group--prefix-calc">
                            <span class="input-prefix-calc">€</span>
                            <c:set var="savingsGoalDisplay" value=""/>
                            <c:if test="${savingsGoal > 0}"><c:set var="savingsGoalDisplay"><fmt:formatNumber value="${savingsGoal}" maxFractionDigits="2" minFractionDigits="2" groupingUsed="false"/></c:set></c:if>
                            <input type="text" name="savingsGoal" class="form-input" value="<c:out value='${savingsGoalDisplay}'/>" placeholder="0.00" pattern="^[0-9]+([.,][0-9]+)?$" title="Please enter a valid amount (e.g. 100 or 100.50)">
                        </div>
                        <span class="form-help">Your target savings for the month</span>
                    </div>

                    <div class="form-field mb-6">
                        <h3 class="text-sm font-semibold mb-3 flex-row gap-2">
                            <i data-lucide="receipt" class="text-primary"></i> Expenses
                        </h3>
                        <div id="expense-rows">
                            <c:choose>
                                <c:when test="${hasExpenseRows}">
                                    <c:forEach var="cat" items="${expenseCategories}" varStatus="s">
                                        <c:set var="amt" value="${expenseAmounts[s.index]}"/>
                                        <div class="calc-dynamic-row">
                                            <input type="text" name="expenseCategory" class="form-input" placeholder="e.g. Rent, Groceries" list="expenseCategories" value="<c:out value='${cat}'/>" required>
                                            <div class="form-input-group form-input-group--prefix-calc">
                                                <span class="input-prefix-calc">€</span>
                                                <input type="text" name="expenseAmount" class="form-input" placeholder="0.00" pattern="^[0-9]+([.,][0-9]+)?$" title="Please enter a valid amount (e.g. 100 or 100.50)" value="<c:out value='${amt}'/>" required>
                                            </div>
                                            <button type="button" class="btn btn-ghost btn-icon btn-sm js-remove-budget-row">
                                                <i data-lucide="trash-2"></i>
                                            </button>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="calc-dynamic-row">
                                        <input type="text" name="expenseCategory" class="form-input" placeholder="e.g. Rent, Groceries" list="expenseCategories" required>
                                        <div class="form-input-group form-input-group--prefix-calc">
                                            <span class="input-prefix-calc">€</span>
                                            <input type="text" name="expenseAmount" class="form-input" placeholder="0.00" pattern="^[0-9]+([.,][0-9]+)?$" title="Please enter a valid amount (e.g. 100 or 100.50)" required>
                                        </div>
                                        <button type="button" class="btn btn-ghost btn-icon btn-sm js-remove-budget-row">
                                            <i data-lucide="trash-2"></i>
                                        </button>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="flex-col mt-2">
                            <button type="button" class="btn btn-ghost btn-sm btn-block" data-add-budget-row="expense">
                                <i data-lucide="plus"></i> Add Expense
                            </button>
                        </div>
                    </div>

                    <div class="form-field mt-6">
                        <button type="submit" class="btn btn-primary btn-block">
                            <i data-lucide="calculator"></i> Calculate Budget
                        </button>
                    </div>
                </form>
            </article>

            <c:if test="${hasData}">
                <article class="card flex align-start gap-3">
                    <div class="advice-icon">
                        <i data-lucide="info"></i>
                    </div>
                    <div>
                        <h4 class="text-sm font-semibold m-0 mb-1">Budget health</h4>
                        <p class="text-sm muted m-0">
                            <c:choose>
                                <c:when test="${savingsGoal > 0 and netBalance >= savingsGoal}">
                                    Your savings goal is realistic and fully met! You have a healthy surplus of €<fmt:formatNumber value="${netBalance - savingsGoal}" maxFractionDigits="0"/> this month.
                                </c:when>
                                <c:when test="${savingsGoal > 0 and netBalance > 0}">
                                    Your savings goal is not fully met. Try reducing your largest expense categories to save €<fmt:formatNumber value="${savingsGoal - netBalance}" maxFractionDigits="0"/> more.
                                </c:when>
                                <c:otherwise>
                                    You are in a budget deficit! Your expenses exceed your monthly income by €<fmt:formatNumber value="${-netBalance}" maxFractionDigits="0"/>. You need to reduce spending immediately.
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </article>
            </c:if>
        </aside>

        <c:if test="${hasData}">
            <div class="flex-col gap-6">

                <div class="grid grid--3">
                    <article class="card">
                        <div class="flex-row gap-2 mb-3 muted text-sm">
                            <div class="badge badge-income badge-icon">
                                <i data-lucide="arrow-up-right"></i>
                            </div>
                            <span>Remaining income</span>
                        </div>
                        <h3 class="text-3xl text-primary font-normal mb-0">
                            €<fmt:formatNumber value="${netBalance}" maxFractionDigits="0"/>
                        </h3>
                        <p class="text-xs muted m-0">After expenses</p>
                    </article>

                    <article class="card">
                        <div class="flex-row gap-2 mb-3 muted text-sm">
                            <div class="badge badge-income badge-icon">
                                <i data-lucide="percent"></i>
                            </div>
                            <span>Savings rate</span>
                        </div>
                        <h3 class="text-3xl font-normal mb-0">
                            <fmt:formatNumber value="${savingsRate}" maxFractionDigits="1" minFractionDigits="1"/>%
                        </h3>
                        <p class="text-xs muted m-0">Of monthly income</p>
                    </article>

                    <article class="card">
                        <div class="flex-row gap-2 mb-3 muted text-sm">
                            <div class="badge badge-wallet-cash badge-icon badge-warning">
                                <i data-lucide="activity"></i>
                            </div>
                            <span>Budget health</span>
                        </div>
                        <h3 class="text-3xl font-normal mb-0">
                            <fmt:formatNumber value="${budgetHealth}" maxFractionDigits="0"/>%
                        </h3>
                        <p class="text-xs m-0 font-medium ${healthStatusClass}">${healthStatus}</p>
                    </article>
                </div>

                <article class="card">
                    <div class="mb-6">
                        <h3 class="card-title m-0">Budget breakdown</h3>
                        <p class="card-subtitle m-0">How your monthly income is distributed.</p>
                    </div>
                    <div class="grid grid--2 align-center">
                        <div class="flex-col gap-6">
                            <div>
                                <div class="flex-row mb-1 justify-between">
                                    <span class="text-sm font-semibold">Expenses</span>
                                    <span class="text-sm font-semibold">€<fmt:formatNumber value="${totalExpenses}" minFractionDigits="2" maxFractionDigits="2"/></span>
                                </div>
                                <div class="progress-track">
                                    <div class="progress-bar progress-bar--negative calc-progress-bar js-progress-bar" data-pct="${expensePct}"></div>
                                </div>
                                <span class="text-xs muted"><fmt:formatNumber value="${totalIncome > 0 ? (totalExpenses / totalIncome) * 100 : 0}" maxFractionDigits="1" minFractionDigits="1"/>% of income</span>
                            </div>

                            <div>
                                <div class="flex-row mb-1 justify-between">
                                    <span class="text-sm font-semibold">Savings / Net balance</span>
                                    <span class="text-sm font-semibold">€<fmt:formatNumber value="${netBalance}" minFractionDigits="2" maxFractionDigits="2"/></span>
                                </div>
                                <div class="progress-track">
                                    <div class="progress-bar progress-bar--warning calc-progress-bar js-progress-bar" data-pct="${savingsPct}"></div>
                                </div>
                                <span class="text-xs muted"><fmt:formatNumber value="${totalIncome > 0 ? (netBalance / totalIncome) * 100 : 0}" maxFractionDigits="1" minFractionDigits="1"/>% of income</span>
                            </div>

                            <c:if test="${savingsGoal > 0}">
                                <div>
                                    <div class="flex-row mb-1 justify-between">
                                        <span class="text-sm font-semibold">Savings goal</span>
                                        <span class="text-sm font-semibold">€<fmt:formatNumber value="${savingsGoal}" minFractionDigits="2" maxFractionDigits="2"/></span>
                                    </div>
                                    <div class="progress-track">
                                        <div class="progress-bar progress-bar--primary calc-progress-bar js-progress-bar" data-pct="${goalPct}"></div>
                                    </div>
                                    <span class="text-xs muted"><fmt:formatNumber value="${totalIncome > 0 ? (savingsGoal / totalIncome) * 100 : 0}" maxFractionDigits="1" minFractionDigits="1"/>% of income</span>
                                </div>
                            </c:if>
                        </div>

                        <div class="center donut-wrapper">
                            <div class="donut-chart-container">
                                <canvas id="budgetDonutChart" width="200" height="200" data-expenses="${totalExpenses}" data-savings="${actualSavings}"></canvas>
                                <div class="donut-chart-center-text">
                                    <span class="donut-val"><fmt:formatNumber value="${donutFreePct}" maxFractionDigits="0"/>%</span>
                                    <span class="donut-lbl">free</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </article>

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
                                <c:choose>
                                    <c:when test="${empty expensesMap}">
                                        <tr>
                                            <td colspan="3" class="center muted mt-4 mb-4 table-empty-cell">No expenses recorded.</td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="entry" items="${expensesMap}">
                                            <c:set var="pct" value="${totalExpenses > 0 ? (entry.value / totalExpenses) * 100 : 0}"/>
                                            <tr>
                                                <td class="pl-6 font-semibold"><c:out value="${entry.key}"/></td>
                                                <td class="num">€<fmt:formatNumber value="${entry.value}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                                <td class="pr-6 num"><fmt:formatNumber value="${pct}" maxFractionDigits="1" minFractionDigits="1"/>%</td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </article>
            </div>
        </c:if>

        <c:if test="${not hasData}">
            <article class="card empty-state flex-col align-center justify-center calc-empty-fill">
                <div class="empty-state__icon">
                    <i data-lucide="bar-chart-3"></i>
                </div>
                <h3 class="empty-state__title">No projection data</h3>
                <p class="empty-state__desc">
                    Enter your financial details in the form on the left and click <strong>Calculate Budget</strong> to see your compound growth projection and year-by-year breakdown.
                </p>
            </article>
        </c:if>

    </div>
</main>

<footer class="site-footer mt-10">
    <p>FinTrack &middot; University Project &middot; 2026</p>
</footer>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
<script src="${ctx}/static/js/icons.js"></script>
<script src="${ctx}/static/js/budget-report.js"></script>

</body>
</html>
