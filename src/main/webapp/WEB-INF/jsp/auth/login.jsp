<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Login</title>
  </head>
  <body>
    <h1>Login</h1>

    <%
      String errorMessage = (String) request.getAttribute("errorMessage");
      if (errorMessage != null) {
    %>
    <p style="color: red;"><%= HtmlUtil.escape(errorMessage) %></p>
    <%
      }
    %>

    <form action="<%= request.getContextPath() %>/auth/login" method="post">
      <label>Email:</label><br>
      <input type="email" name="email" value="<%= HtmlUtil.escape(request.getAttribute("email")) %>"><br><br>

      <label>Password:</label><br>
      <input type="password" name="password"><br><br>

      <button type="submit">Login</button>
    </form>

    <p>
      No account yet?
      <a href="<%= request.getContextPath() %>/register">Register</a>
    </p>
  </body>
</html>
