<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
    <head>
        <title>FinTrack</title>
    </head>
    <body>
        <h1>Welcome to FinTrack</h1>

        <p>Choose how you want to continue:</p>

        <form action="${ctx}/login" method="get">
            <button type="submit">Log in</button>
        </form>

        <br>

        <form action="${ctx}/register" method="get">
            <button type="submit">Register</button>
        </form>

        <br>

        <form action="${ctx}/guest/report" method="get">
            <button type="submit">Continue as Guest</button>
        </form>
    </body>
</html>
