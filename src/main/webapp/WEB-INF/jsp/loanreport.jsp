<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Important: add this line to enable <c: tags --%>
<%@ page import="java.util.*" %>
<%@ page import="it.unipd.dei.webapp.model.AmortizationRow" %>
<!DOCTYPE html>
<html>
  <head>
    <title>FinTrack - Loan Report</title>
  </head>
  <body>
    <h1>Loan Report</h1>

    <p>Total number of payments: ${amortizationList.size()}</p>

    <table border="1">
        <thead>
                    <tr>
                        <th>Month</th>
                        <th>Total Payment (€)</th>
                        <th>Principal Portion (€)</th>
                        <th>Interest Portion (€)</th>
                        <th>Remaining Balance (€)</th>
                    </tr>
        </thead>
        <tbody>

            <%
                  List<AmortizationRow> result= (List<AmortizationRow>) request.getAttribute("amortizationList");

              if(result != null)
              {
                    for(int i=0; i<result.size();i++)
                    {
                        AmortizationRow row = result.get(i);

            %>
                        <tr>
                            <td><%= row.getMonth() %> </td>
                            <td><%= row.getTotalPayment() %> </td>
                            <td><%= row.getPrincipalPaid() %> </td>
                            <td><%= row.getInterestPaid() %> </td>
                            <td><%= row.getRemainingBalance() %> </td>

                        </tr>

            <%
                   }

               }
            %>


        </tbody>

    </table>
    <br>
        <a href="<%= request.getContextPath() %>/guest/report">Back to Calculator</a>
  </body>
</html>