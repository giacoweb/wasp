<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
  <head>
    <title>FinTrack</title>
  </head>
  <body>
    <h1>FinTrack - Calculator</h1>

    <p>Loan Calculator</p>

    <c:if test="${not empty errorMessage}">
        <div style="color: red; font-weight: bold; margin-bottom: 10px;">
            ${errorMessage}
        </div>
    </c:if>

    <form action="<%= request.getContextPath() %>/report/compound" method="post">

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
    <p>Compound Interest Calculator</p>
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

    <form action="<%= request.getContextPath() %>/guest/report" method="get">
      <button type="submit">Continue as Guest</button>

    </form>
  </body>
</html>