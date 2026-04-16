<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Important: add this line to enable <c: tags --%>
<%@ page import="java.util.*" %>
<%@ page import="it.unipd.dei.webapp.model.CompoundResult" %>
<!DOCTYPE html>
<html>
  <head>
    <title>FinTrack - Compound Report</title>
  </head>
  <body>
    <h1>Compound Report</h1>

    <p>Number of years: ${compoundList.size()}</p>

    <table border="1">
        <thead>
                    <tr>
                        <th>Year</th>
                        <th>Total Value (€)</th>
                    </tr>
        </thead>
        <tbody>

            <%
                  List<CompoundResult> result= (List<CompoundResult>) request.getAttribute("compoundList");

              if(result != null)
              {
                    for(int i=0; i<result.size();i++)
                    {
                        CompoundResult row = result.get(i);

            %>
                        <tr>
                            <td><%= row.getYear() %> </td>
                            <td><%= row.getTotalValue() %> </td>
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