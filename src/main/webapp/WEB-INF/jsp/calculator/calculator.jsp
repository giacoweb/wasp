<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
  <head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <title>FinTrack</title>
    <link rel="stylesheet" href="${ctx}/static/css/tokens.css">
    <link rel="stylesheet" href="${ctx}/static/css/calculator.css">
  </head>
  <body>
    <h1>FinTrack - Calculator</h1>

    <p><strong>Loan Calculator</strong></p>

    <c:if test="${not empty errorMessage}">
        <div class="calc-error-legacy">
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <form action="${ctx}/report/loan" method="post">

        <div>
             <label>Principal (€):</label><br>
              <input type="number" step="0.01" min="0.01" required name="principal" value="<c:out value='${param.principal}'/>">

        </div>
        <div>
              <label>Annual Rate (TAN %):</label><br>
              <input type="number" step="0.1" min="0" max="100" required name="annualRate" value="<c:out value='${param.annualRate}'/>">

        </div>
        <div>
              <label>Duration (Months):</label><br>
              <input type="number" step="1" min="1" max="600" required name="months" value="<c:out value='${param.months}'/>">

        </div>

      <button type="submit">Calculate Loan</button>
    </form>

    <br>
    <p><strong>Compound Interest Calculator</strong></p>
    <form action="${ctx}/report/compound" method="post">

            <div>
                 <label>Initial Capital (€):</label><br>
                  <input type="number" step="0.01" min="0" required name="initialCapital" value="">
            </div>
            <div>
                  <label>Monthly Contribution (€):</label><br>
                  <input type="number" step="0.01" min="0" required name="monthlyContribution" value="">
            </div>
            <div>
                  <label>Annual Rate (%):</label><br>
                  <input type="number" step="0.1" min="0" max="100" required name="annualRate" value="">
            </div>
            <div>
                  <label>Years Duration (years):</label><br>
                  <input type="number" step="1" min="1" max="50" required name="years" value="">
            </div>
          <button type="submit">Calculate Compound</button>
        </form>

    <br>
    <br>
    <p><strong>Budget Calculator</strong></p>

    <form action="${ctx}/report/budget" method="post">

        <p>Income Items</p>
        <div id="income-rows">
            <div class="income-row">
                <input type="text"   name="incomeCategory" placeholder="Category (e.g. Salary)" required>
                <input type="number" name="incomeAmount"   placeholder="Amount (€)" step="0.01" min="0.01" required>
                <button type="button" class="js-calculator-remove">✕</button>
            </div>
        </div>
        <button type="button" data-calculator-add="income">+ Add Income</button>
        <br><br>

        <p>Expense Items</p>
        <div id="expense-rows">
            <div class="expense-row">
                <input type="text"   name="expenseCategory" placeholder="Category (e.g. Rent)" required>
                <input type="number" name="expenseAmount"   placeholder="Amount (€)" step="0.01" min="0.01" required>
                <button type="button" class="js-calculator-remove">✕</button>
            </div>
        </div>
        <button type="button" data-calculator-add="expense">+ Add Expense</button>
        <br><br>
        <button type="submit">Calculate Budget</button>
    </form>
    <script src="${ctx}/static/js/calculator.js"></script>


    <br>
    <br>
    <form action="${ctx}/" method="get">
      <button type="submit">Back to Homepage</button>
    </form>
    <c:if test="${not empty sessionScope.guestSessionToken and hasGuestReportData}">
        <a href="${ctx}/report/export?token=${sessionScope.guestSessionToken}">
            <button type="button">Export full PDF Result</button>
        </a>
    </c:if>
  </body>
</html>
