<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.model.DashboardSummary" %>
<%@ page import="it.unipd.dei.webapp.model.WalletSummary" %>
<%@ page import="it.unipd.dei.webapp.model.User" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
</head>
<body>

<%
    User user = (User) session.getAttribute("authenticatedUser");
%>
<h1>Welcome, <%= HtmlUtil.escape(user.getName()) %></h1>

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage != null) {
%>
<p style="color: red;"><%= HtmlUtil.escape(errorMessage) %></p>
<%
    }
%>

<%
    DashboardSummary summary = (DashboardSummary) request.getAttribute("summary");
    if (summary != null) {
%>

<h2>Financial Summary</h2>
<table border="1" cellpadding="8">
    <tr>
        <th>Total Equity</th>
        <td><%= summary.getTotalEquity() %></td>
    </tr>
    <tr>
        <th>YTD Expenses</th>
        <td><%= summary.getYtdExpenses() %></td>
    </tr>
    <tr>
        <th>YTD Savings</th>
        <td><%= summary.getYtdSavings() %></td>
    </tr>
</table>

<h2>Wallets</h2>
<%
    List<WalletSummary> wallets = summary.getWallets();
    if (wallets == null || wallets.isEmpty()) {
%>
<p>No wallets yet. <a href="<%= request.getContextPath() %>/wallets">Create your first wallet.</a></p>
<%
    } else {
%>
<table border="1" cellpadding="8">
    <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Currency</th>
            <th>Balance</th>
        </tr>
    </thead>
    <tbody>
    <%
        for (WalletSummary wallet : wallets) {
    %>
        <tr>
            <td><%= HtmlUtil.escape(wallet.getName()) %></td>
            <td><%= wallet.getType() %></td>
            <td><%= HtmlUtil.escape(wallet.getCurrency()) %></td>
            <td><%= wallet.getBalance() %></td>
        </tr>
    <%
        }
    %>
    </tbody>
</table>
<%
    }
}
%>

<br>
<form action="<%= request.getContextPath() %>/auth/logout" method="post">
    <button type="submit">Logout</button>
</form>

</body>
</html>
