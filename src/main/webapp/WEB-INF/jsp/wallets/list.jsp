<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.model.Wallet" %>
<%@ page import="it.unipd.dei.webapp.model.WalletType" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Wallets</title>
</head>
<body>
<h1>Your Wallets</h1>

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    String csrfToken = (String) request.getAttribute("csrfToken");
    List<Wallet> wallets = (List<Wallet>) request.getAttribute("wallets");
%>

<% if (errorMessage != null) { %>
<p style="color: red;"><%= HtmlUtil.escape(errorMessage) %></p>
<% } %>

<% if (successMessage != null) { %>
<p style="color: green;"><%= HtmlUtil.escape(successMessage) %></p>
<% } %>

<h2>Create Wallet</h2>
<form action="<%= request.getContextPath() %>/wallets/create" method="post">
    <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>">

    <label>Name:</label><br>
    <input type="text" name="name" required><br><br>

    <label>Type:</label><br>
    <select name="type" required>
        <% for (WalletType type : WalletType.values()) { %>
        <option value="<%= type.name() %>"><%= HtmlUtil.escape(type.name()) %></option>
        <% } %>
    </select><br><br>

    <label>Currency:</label><br>
    <input type="text" name="currency" maxlength="3" placeholder="EUR" required><br><br>

    <button type="submit">Create Wallet</button>
</form>

<h2>Wallet List</h2>
<% if (wallets == null || wallets.isEmpty()) { %>
<p>No wallets available yet.</p>
<% } else { %>
<table border="1" cellpadding="8">
    <thead>
    <tr>
        <th>Name</th>
        <th>Type</th>
        <th>Currency</th>
        <th>Created At</th>
        <th>Edit</th>
        <th>Delete</th>
    </tr>
    </thead>
    <tbody>
    <% for (Wallet wallet : wallets) { %>
    <tr>
        <td><%= HtmlUtil.escape(wallet.getName()) %></td>
        <td><%= HtmlUtil.escape(wallet.getType().name()) %></td>
        <td><%= HtmlUtil.escape(wallet.getCurrency()) %></td>
        <td><%= wallet.getCreatedAt() %></td>
        <td>
            <form action="<%= request.getContextPath() %>/wallets/edit" method="post">
                <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>">
                <input type="hidden" name="walletId" value="<%= wallet.getId() %>">

                <input type="text" name="name" value="<%= HtmlUtil.escape(wallet.getName()) %>" required><br><br>

                <select name="type" required>
                    <% for (WalletType type : WalletType.values()) { %>
                    <option value="<%= type.name() %>" <%= type == wallet.getType() ? "selected" : "" %>>
                        <%= HtmlUtil.escape(type.name()) %>
                    </option>
                    <% } %>
                </select><br><br>

                <input type="text" name="currency" maxlength="3"
                       value="<%= HtmlUtil.escape(wallet.getCurrency()) %>" required><br><br>

                <button type="submit">Save</button>
            </form>
        </td>
        <td>
            <form action="<%= request.getContextPath() %>/wallets/delete" method="post">
                <input type="hidden" name="csrfToken" value="<%= HtmlUtil.escape(csrfToken) %>">
                <input type="hidden" name="walletId" value="<%= wallet.getId() %>">
                <button type="submit">Delete</button>
            </form>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>
<% } %>

<br>
<a href="<%= request.getContextPath() %>/dashboard">Back to Dashboard</a>
</body>
</html>
