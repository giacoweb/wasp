<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Important: add this line to enable <c: tags --%>
<%@ page import="java.util.*" %>
<%@ page import="it.unipd.dei.webapp.model.AmortizationRow" %>
<%
    String ctxPath = request.getContextPath();
    List<AmortizationRow> result = (List<AmortizationRow>) request.getAttribute("amortizationList");
    double principal = (double) request.getAttribute("principal");
    double annualRate = (double) request.getAttribute("annualRate");

    if (result == null) {
        response.sendRedirect(request.getContextPath() + "/guest/report");
        return;
    }

    String strMonths = "[";
    String strRemainingBalance = "[";
    String strPrincipalPaid = "[";
    String strInterestPaid = "[";
    String strTotalPayment = "[";
    for (int i = 0; i < result.size(); i++) {
        AmortizationRow r = result.get(i);
        String comma = (i == result.size() - 1) ? "" : ",";
        strMonths           += "\"Month " + r.getMonth() + "\"" + comma;
        strRemainingBalance += r.getRemainingBalance() + comma;
        strPrincipalPaid    += r.getPrincipalPaid() + comma;
        strInterestPaid     += r.getInterestPaid() + comma;
        strTotalPayment     += r.getTotalPayment() + comma;
    }
    strMonths           += "]";
    strRemainingBalance += "]";
    strPrincipalPaid    += "]";
    strInterestPaid     += "]";
    strTotalPayment     += "]";

%>
<!DOCTYPE html>
<html>
  <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <title>FinTrack - Loan Report</title>

      <link rel="preconnect" href="https://fonts.googleapis.com">
      <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
      <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

      <link rel="stylesheet" href="<%= ctxPath %>/static/css/tokens.css">
      <link rel="stylesheet" href="<%= ctxPath %>/static/css/base.css">
      <link rel="stylesheet" href="<%= ctxPath %>/static/css/layout.css">
      <link rel="stylesheet" href="<%= ctxPath %>/static/css/components.css">
      <link rel="stylesheet" href="<%= ctxPath %>/static/css/tables.css">

  </head>
  <body>
  <header class="site-header">
      <a href="<%= ctxPath %>/" class="brand">
          <i data-lucide="triangle"></i>
          FinTrack
      </a>
      <nav class="site-header__nav" aria-label="Primary">
          <a href="<%= ctxPath %>/login" class="btn btn-ghost">Log in</a>
          <a href="<%= ctxPath %>/register" class="btn btn-primary">Get started</a>
      </nav>
  </header>
  <main class="container section">
      <header class="center mb-8">
          <h1>Your Loan Report</h1>
          <p class="hero__lead">Here is the summary of your financial projection.</p>
      </header>

      <div class="grid grid--auto-fit mb-10">

          <!-- Principal Capital -->
          <article class="card">
              <div class="feature-icon">
                  <i data-lucide="piggy-bank"></i>
              </div>
              <h3 class="card-report-title">Principal Capital</h3>
              <p class="card-report-subtitle"> <%= principal %>€</p>
          </article>

          <!-- Months Duration -->
          <article class="card">
              <div class="feature-icon">
                  <i data-lucide="calendar"></i>
              </div>
              <h3 class="card-report-title">Duration</h3>
              <p class="card-report-subtitle"> <%= result.size() %> months</p>
          </article>

          <!-- Annual Rate -->
          <article class="card">
              <div class="feature-icon">
                  <i data-lucide="percent"></i>
              </div>
              <h3 class="card-report-title">Annual Rate(TAN)</h3>
              <p class="card-report-subtitle"> <%= annualRate %>% </p>
          </article>
      </div>

      <div class="grid grid--auto-fit mb-10" >
          <article class="card">
              <h3 class="card-report-title mb-4">Payment Breakdown</h3>
              <canvas id="paymentChart"></canvas>
          </article>
          <article class="card">
              <h3 class="card-report-title mb-4" >Remaining Balance</h3>
              <canvas id="balanceChart"></canvas>
          </article>
      </div>

      <div class="table-wrapper mb-10">
          <table class="table table--striped">
              <thead>
              <tr>
                  <th>Month</th>
                  <th class="num">Total Payment</th>
                  <th class="num">Principal Paid</th>
                  <th class="num">Interest Paid</th>
                  <th class="num">Remaining Balance</th>
              </tr>
              </thead>
              <tbody id="amortBody">
              <% for (int i = 0; i < result.size(); i++) {
                  AmortizationRow row = result.get(i); %>
              <tr>
                  <td><%= row.getMonth() %></td>
                  <td class="num"><%= String.format("€%.2f", row.getTotalPayment()) %></td>
                  <td class="num amount-positive"><%= String.format("€%.2f", row.getPrincipalPaid()) %></td>
                  <td class="num amount-negative"><%= String.format("€%.2f", row.getInterestPaid()) %></td>
                  <td class="num"><%= String.format("€%.2f", row.getRemainingBalance()) %></td>
              </tr>
              <% } %>
              </tbody>
          </table>
          <div id="pagination" class="table-footer">
              <span id="pageInfo" class="muted"></span>
              <div id="pageButtons" class="table-footer--buttons"></div>
          </div>
      </div>



      <div class="center">
          <a class="btn btn-primary" href="<%= ctxPath %>/guest/report">Back to Calculator</a>
      </div>
  </main>


    <footer class="site-footer">
        <p>FinTrack &middot; University Project &middot; 2026</p>
    </footer>


  <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <script src="<%= ctxPath %>/static/js/table-control.js"></script>
  <script>
      lucide.createIcons();
      var months      = <%= strMonths %>;
      var principal   = <%= strPrincipalPaid %>;
      var interest    = <%= strInterestPaid %>;
      var balance     = <%= strRemainingBalance %>;
      new Chart(document.getElementById('paymentChart'), {
          type: 'bar',
          data: {
              labels: months,
              datasets: [
                  {
                      label: 'Principal Paid',
                      data: principal,
                      backgroundColor: 'rgba(15, 118, 110, 0.75)'
                  },
                  {
                      label: 'Interest Paid',
                      data: interest,
                      backgroundColor: 'rgba(220, 38, 38, 0.65)'
                  }
              ]
          },
          options: {
              responsive: true,
              scales: {
                  x: { stacked: true },
                  y: { stacked: true, beginAtZero: true }
              }
          }
      });
      new Chart(document.getElementById('balanceChart'), {
          type: 'line',
          data: {
              labels: months,
              datasets: [{
                  label: 'Remaining Balance',
                  data: balance,
                  borderColor: 'rgba(15, 118, 110, 1)',
                  backgroundColor: 'rgba(15, 118, 110, 0.1)',
                  fill: true,
                  tension: 0.3,
                  pointRadius: 0
              }]
          },
          options: {
              responsive: true,
              scales: {
                  y: { beginAtZero: true }
              }
          }
      });

  </script>

  </body>
</html>