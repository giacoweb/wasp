<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
  <head>
    <title>FinTrack</title>
  </head>
  <body>
    <h1>Welcome to FinTrack</h1>

    <p>Guest Calculator</p>


    <form action="<%= request.getContextPath() %>/report/loan" method="post">

        <div>
             <label>Principal (€):</label><br>
              <input type="number" step="0.01" name="principal" value="${param.principal}">
              <c:if test="${not empty errors.principal}">
                    <span style="color: red;">${errors.principal}</span>
               </c:if>
        </div>
        <div>
              <label>Annual Rate (TAN %):</label><br>
              <input type="number" step="0.1" name="annualRate" value="${param.annualRate}">
              <c:if test="${not empty errors.annualRate}">
                  <span style="color: red;">${errors.annualRate}</span>
              </c:if>
        </div>
        <div>
              <label>Duration (Months):</label><br>
              <input type="number" name="termMonths" value="${param.termMonths}">
              <c:if test="${not empty errors.termMonths}">
                   <span style="color: red;">${errors.termMonths}</span>
              </c:if>
        </div>



      <button type="submit">Calculate</button>
    </form>

    <br>

    <form action="<%= request.getContextPath() %>/guest/report" method="get">
      <button type="submit">Continue as Guest</button>
    </form>
  </body>
</html>