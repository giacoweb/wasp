<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
        <p style="color: red;"><%= errorMessage %></p>
        <%
            }
        %>

        <form action="${pageContext.request.contextPath}/auth/register" method="post">
            <label>Name:</label><br>
            <input type="text" name="name" value="<%= request.getAttribute("name") != null ? request.getAttribute("name") : "" %>"><br><br>

            <label>Surname:</label><br>
            <input type="text" name="surname" value="<%= request.getAttribute("surname") != null ? request.getAttribute("surname") : "" %>"><br><br>

            <label>Email:</label><br>
            <input type="email" name="email" value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"><br><br>

            <label>Password:</label><br>
            <input type="password" name="password"><br><br>

            <button type="submit">Register</button>
        </form>

        <p>
            Already have an account?
            <a href="${pageContext.request.contextPath}/login">Login</a>
        </p>
    </body>
</html>