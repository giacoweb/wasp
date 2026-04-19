<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
  <head>
    <title>FinTrack</title>
  </head>
  <body>
    <h1>FinTrack - Calculator</h1>

    <p></strong>Loan Calculator</strong></p>

    <c:if test="${not empty errorMessage}">
        <div style="color: red; font-weight: bold; margin-bottom: 10px;">
            ${errorMessage}
        </div>
    </c:if>

    <form action="<%= request.getContextPath() %>/report/loan" method="post">

        <div>
             <label>Principal (€):</label><br>
              <input type="number" step="0.01" name="principal" value="${param.principal}">

        </div>
        <div>
              <label>Annual Rate (TAN %):</label><br>
              <input type="number" step="0.1" name="annualRate" value="${param.annualRate}">

        </div>
        <div>
              <label>Duration (Months):</label><br>
              <input type="number" name="months" value="${param.termMonths}">

        </div>

      <button type="submit">Calculate Loan</button>
    </form>

    <br>
    <p><strong>Compound Interest Calculator</strong></p>
    <form action="<%= request.getContextPath() %>/report/compound" method="post">

            <div>
                 <label>initial Capital (€):</label><br>
                  <input type="number" step="100" name="initialCapital" value="">
            </div>
            <div>
                  <label>Monthly Contribution (€):</label><br>
                  <input type="number" step="0.1" name="monthlyContribution" value="">
            </div>
            <div>
                  <label>Annual Rate (%):</label><br>
                  <input type="number" name="annualRate" value="">
            </div>
            <div>
                  <label>Years Duration (years):</label><br>
                  <input type="number" name="years" value="">
            </div>
          <button type="submit">Calculate Compound</button>
        </form>

    <br>
    <br>
    <p><strong>Budget Calculator</strong></p>

    <form action="<%= request.getContextPath()%>/report/budget" method="post">

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
    <form action="<%= request.getContextPath() %>" method="get">
      <button type="submit">Back to Homepage</button>

    </form>
  </body>
</html>