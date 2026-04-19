<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Important: add this line to enable <c: tags --%>
<%@ page import="java.util.*" %>
<%@ page import="it.unipd.dei.webapp.model.BudgetResult" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html>
  <head>
    <title>FinTrack - Budget Report</title>
  </head>
  <body>
    <h1>Budget Report</h1>

    <%
          BudgetResult result = (BudgetResult) request.getAttribute("budgetResult");
          if (result == null) {
                response.sendRedirect(request.getContextPath() + "/guest/report");
                return;
          }
    %>

    <h2>Summary</h2>
    <table border="1">
            <tr><th>Total Income (€)</th><td><%= String.format("%.2f", result.getTotalIncome()) %></td></tr>
            <tr><th>Total Expenses (€)</th><td><%= String.format("%.2f", result.getTotalExpenses()) %></td></tr>
            <tr><th>Net Balance (€)</th><td><%= String.format("%.2f", result.getNetBalance()) %></td></tr>
    </table>

    <h2>Income by Category</h2>
     <table border="1">
            <thead><tr><th>Category</th><th>Amount (€)</th></tr></thead>
            <tbody>
            <% for (Map.Entry<String, BigDecimal> entry : result.getIncomeByCategory().entrySet()) { %>
                <tr>
                    <td><%= entry.getKey() %></td>
                    <td><%= String.format("%.2f", entry.getValue()) %></td>
                </tr>
            <% } %>
            </tbody>
     </table>

    <h2>Expenses by Category</h2>
    <table border="1">
            <thead><tr><th>Category</th><th>Amount (€)</th></tr></thead>
            <tbody>
            <% for (Map.Entry<String, BigDecimal> entry : result.getExpensesByCategory().entrySet()) { %>
                <tr>
                    <td><%= entry.getKey() %></td>
                    <td><%= String.format("%.2f", entry.getValue()) %></td>
                </tr>
            <% } %>
            </tbody>
        </table>

    <br>
        <a href="<%= request.getContextPath() %>/guest/report">Back to Calculator</a>
  </body>
</html>