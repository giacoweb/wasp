<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.model.PacPlan" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>PAC Plans</title>
</head>
<body>
<h1>Your PAC Plans</h1>

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    List<PacPlan> pacPlans = (List<PacPlan>) request.getAttribute("pacPlans");
%>

<% if (errorMessage != null) { %>
<p style="color: red;"><%= HtmlUtil.escape(errorMessage) %></p>
<% } %>

<% if (successMessage != null) { %>
<p style="color: green;"><%= HtmlUtil.escape(successMessage) %></p>
<% } %>

<h2>Create PAC Plan</h2>
<form action="<%= request.getContextPath() %>/pac/create" method="post">
    <label>Asset Name:</label><br>
    <input type="text" name="assetName" required><br><br>

    <label>Monthly Amount:</label><br>
    <input type="number" step="0.01" min="0.01" name="monthlyAmount" required><br><br>

    <label>Target Amount:</label><br>
    <input type="number" step="0.01" min="0.01" name="targetAmount" required><br><br>

    <label>Start Date:</label><br>
    <input type="date" name="startDate" required><br><br>

    <button type="submit">Create Plan</button>
</form>

<h2>PAC Plan List</h2>
<% if (pacPlans == null || pacPlans.isEmpty()) { %>
<p>No PAC plans available yet.</p>
<% } else { %>
<table border="1" cellpadding="8">
    <thead>
    <tr>
        <th>Asset</th>
        <th>Monthly Amount</th>
        <th>Target Amount</th>
        <th>Start Date</th>
        <th>Active</th>
        <th>Created At</th>
        <th>Edit</th>
        <th>Delete</th>
    </tr>
    </thead>
    <tbody>
    <% for (PacPlan plan : pacPlans) { %>
    <tr>
        <td><%= HtmlUtil.escape(plan.getAssetName()) %></td>
        <td><%= plan.getMonthlyAmount() %></td>
        <td><%= plan.getTargetAmount() %></td>
        <td><%= plan.getStartDate() %></td>
        <td><%= plan.isActive() ? "Yes" : "No" %></td>
        <td><%= plan.getCreatedAt() %></td>
        <td>
            <form action="<%= request.getContextPath() %>/pac/edit" method="post">
                <input type="hidden" name="planId" value="<%= plan.getId() %>">

                <input type="text" name="assetName" value="<%= HtmlUtil.escape(plan.getAssetName()) %>" required><br><br>

                <input type="number" step="0.01" min="0.01" name="monthlyAmount"
                       value="<%= plan.getMonthlyAmount() %>" required><br><br>

                <input type="number" step="0.01" min="0.01" name="targetAmount"
                       value="<%= plan.getTargetAmount() %>" required><br><br>

                <input type="date" name="startDate" value="<%= plan.getStartDate() %>" required><br><br>

                <select name="active" required>
                    <option value="true" <%= plan.isActive() ? "selected" : "" %>>Active</option>
                    <option value="false" <%= !plan.isActive() ? "selected" : "" %>>Inactive</option>
                </select><br><br>

                <button type="submit">Save</button>
            </form>
        </td>
        <td>
            <form action="<%= request.getContextPath() %>/pac/delete" method="post">
                <input type="hidden" name="planId" value="<%= plan.getId() %>">
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
