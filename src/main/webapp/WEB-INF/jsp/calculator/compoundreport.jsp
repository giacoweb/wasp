<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String ctxPath = request.getContextPath();

    List<CompoundResult> result= (List<CompoundResult>) request.getAttribute("compoundList");
    double initialCapital = (double) request.getAttribute("initialCapital");
    double monthlyContribution = (double) request.getAttribute("monthlyContribution");
    double annualRate = (double) request.getAttribute("annualRate");

    if (result == null) {
        response.sendRedirect(request.getContextPath() + "/guest/report");
        return;
    }

    String stringResult = "[";
    String stringYear = "[";
    for( int i = 0; i < result.size(); i++)
    {
        if (i== result.size()-1)
        {
            stringResult += result.get(i).getTotalValue();
            stringYear += "\"Year " + (i+1)+"\"";
        }
        else
        {
            stringResult += result.get(i).getTotalValue()+",";
            stringYear += "\"Year " + (i+1) + "\",";

        }

    }
    stringResult+="]";
    stringYear+="]";
%>
<%@ page import="java.util.*" %>
<%@ page import="it.unipd.dei.webapp.model.CompoundResult" %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack - Compound Report</title>

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
          <h1>Your Compound Report</h1>
          <p class="hero__lead">Here is the summary of your financial projection.</p>
      </header>

      <div class="grid grid--auto-fit mb-10">

          <!-- initial Capital -->
          <article class="card">
              <div class="feature-icon">
                  <i data-lucide="piggy-bank"></i>
              </div>
              <h3 class="card-report-title">Initial Capital</h3>
              <p class="card-report-subtitle"> <%= initialCapital %>€ </p>
          </article>

          <!-- years -->
          <article class="card">
              <div class="feature-icon">
                  <i data-lucide="calendar"></i>
              </div>
              <h3 class="card-report-title">Years</h3>
              <p class="card-report-subtitle" > <%= result.size() %> years </p>
          </article>

          <!-- Annual Rate -->
          <article class="card">
              <div class="feature-icon">
                  <i data-lucide="percent"></i>
              </div>
              <h3 class="card-report-title">Annual Interest</h3>
              <p class="card-report-subtitle"> <%= annualRate %> % </p>
          </article>

          <!-- Monthly contribution -->
          <article class="card">
              <div class="feature-icon">
                  <i data-lucide="percent"></i>
              </div>
              <h3 class="card-report-title">Monthly Contribution</h3>
              <p class="card-report-subtitle" > <%= monthlyContribution %> € </p>
          </article>

      </div>

      <canvas class="mb-12" id="compoundChart"></canvas>

      <div class="table-wrapper mb-10">
          <table class="table table--striped">
              <thead>
              <tr>
                  <th>Year</th>
                  <th class="num">Total Value</th>
              </tr>
              </thead>
              <tbody id="amortBody">
              <% for (int i = 0; i < result.size(); i++) {
                  CompoundResult row = result.get(i); %>
              <tr>
                  <td><%= row.getYear() %></td>
                  <td class="num"><%= String.format("€%.2f", row.getTotalValue()) %></td>
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


  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
  <script src="<%= ctxPath %>/static/js/table-control.js"></script>

  <script>
      const ctx = document.getElementById('compoundChart');
      const DataObject = JSON.parse('<%= stringResult %>');
      const LabelObject = JSON.parse('<%= stringYear %>');


      new Chart(ctx, {
          type: 'line',
          data: {
              labels: LabelObject,
              datasets: [{
                  label: 'Compound Value',
                  data: DataObject,
                  borderColor: '#0F766E',
                  backgroundColor: 'rgba(15, 118, 110, 0.15)',
                  borderWidth: 2,
                  tension: 0.3,
                  fill: true,
                  pointBackgroundColor: '#FFFFFF',
                  pointBorderColor: '#0F766E',
                  pointHoverBackgroundColor: '#0F766E',
                  pointHoverBorderColor: '#FFFFFF'
              }]
          },
          options: {
              responsive: true,
              scales: {
                  x: {
                      ticks: {
                          font: { family: "'Inter', sans-serif" },
                          color: '#64748B'
                      },
                      grid: {
                          display: false
                      },
                      border: {
                          color: '#ECECEC'
                      }
                  },
                  y: {
                      beginAtZero: true,
                      ticks: {
                          font: { family: "'Inter', sans-serif" },
                          color: '#64748B'
                      },
                      grid: {
                          color: '#ECECEC',
                          drawBorder: false
                      },
                      border: {
                          display: false
                      }
                  }
              }
          }
      });
      lucide.createIcons();
  </script>


  </body>
</html>