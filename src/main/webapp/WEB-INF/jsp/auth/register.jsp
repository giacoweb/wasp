<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Register</title>
    </head>
    <body>
        <h1>Register</h1>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
        <p style="color: red;"><%= HtmlUtil.escape(errorMessage) %></p>
        <%
            }
        %>

        <form action="<%= request.getContextPath() %>/auth/register" method="post">
            <label>Name:</label><br>
            <input type="text" name="name" value="<%= HtmlUtil.escape(request.getAttribute("name")) %>"><br><br>

            <label>Surname:</label><br>
            <input type="text" name="surname" value="<%= HtmlUtil.escape(request.getAttribute("surname")) %>"><br><br>

            <label>Email:</label><br>
            <input type="email" name="email" value="<%= HtmlUtil.escape(request.getAttribute("email")) %>"><br><br>

            <label>Password:</label><br>
            <input type="password" name="password"><br><br>
            <small>Password must contain at least 8 characters, one digit, and one special character.</small><br><br>

            <button type="submit">Register</button>
        </form>

        <p>
            Already have an account?
            <a href="<%= request.getContextPath() %>/login">Login</a>
        </p>
    </body>
</html>
