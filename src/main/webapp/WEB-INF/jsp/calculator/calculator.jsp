<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
  <head>
    <title>FinTrack</title>
  </head>
  <body>
    <h1>FinTrack - Calculator</h1>

    <p><strong>Loan Calculator</strong></p>

    <c:if test="${not empty errorMessage}">
        <div style="color: red; font-weight: bold; margin-bottom: 10px;">
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
                <button type="button" onclick="removeRow(this)">✕</button>
            </div>
        </div>
        <button type="button" onclick="addRow('income')">+ Add Income</button>
        <br><br>

        <p>Expense Items</p>
        <div id="expense-rows">
            <div class="expense-row">
                <input type="text"   name="expenseCategory" placeholder="Category (e.g. Rent)" required>
                <input type="number" name="expenseAmount"   placeholder="Amount (€)" step="0.01" min="0.01" required>
                <button type="button" onclick="removeRow(this)">✕</button>
            </div>
        </div>
        <button type="button" onclick="addRow('expense')">+ Add Expense</button>
        <br><br>
        <button type="submit">Calculate Budget</button>
    </form>
    <script>
        function addRow(type) {
            const container = document.getElementById(type + '-rows');
            const div = document.createElement('div');
            div.className = type + '-row';
            const catInput = document.createElement('input');
            catInput.type = 'text';
            catInput.name = type + 'Category';
            catInput.placeholder = 'Category';
            catInput.required = true;
            const amtInput = document.createElement('input');
            amtInput.type = 'number';
            amtInput.name = type + 'Amount';
            amtInput.placeholder = 'Amount (€)';
            amtInput.step = '0.01';
            amtInput.min = '0.01';
            amtInput.required = true;
            const removeBtn = document.createElement('button');
            removeBtn.type = 'button';
            removeBtn.textContent = '✕';
            removeBtn.onclick = function() { removeRow(this); };
            div.appendChild(catInput);
            div.appendChild(amtInput);
            div.appendChild(removeBtn);
            container.appendChild(div);
        }
        function removeRow(btn) {
            const row = btn.parentElement;
            const container = row.parentElement;
            if (container.children.length > 1) {
                container.removeChild(row);
            }
        }
    </script>


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
