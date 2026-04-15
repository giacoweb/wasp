<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>FinTrack</title>
    </head>
    <body>
        <h1>Welcome to FinTrack</h1>

        <p>Choose how you want to continue:</p>

        <form action="<%= request.getContextPath() %>/login" method="get">
            <button type="submit">Log in</button>
        </form>

        <br>

        <form action="<%= request.getContextPath() %>/register" method="get">
            <button type="submit">Register</button>
        </form>

        <br>

        <form action="<%= request.getContextPath() %>/guest/report" method="get">
            <button type="submit">Continue as Guest</button>
        </form>
    </body>
</html>