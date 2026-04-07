<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Dashboard</title>
    </head>
    <body>
        <h1>Dashboard</h1>

        <p>You are logged in.</p>

        <form action="<%= request.getContextPath() %>/auth/logout" method="post">
            <button type="submit">Logout</button>
        </form>
    </body>
</html>