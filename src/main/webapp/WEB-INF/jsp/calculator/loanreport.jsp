<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="it.unipd.dei.webapp.model.AmortizationRow" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<%
    String ctxPath = request.getContextPath();
    List<AmortizationRow> result = (List<AmortizationRow>) request.getAttribute("amortizationList");
    Double principal = (Double) request.getAttribute("principal");
    Double annualRate = (Double) request.getAttribute("annualRate");
    
    String principalStr = request.getParameter("principal") != null ? HtmlUtil.escape(request.getParameter("principal")) : "";
    String annualRateStr = request.getParameter("annualRate") != null ? HtmlUtil.escape(request.getParameter("annualRate")) : "";
    String monthsStr = request.getParameter("months") != null ? HtmlUtil.escape(request.getParameter("months")) : "";
    String startMonthStr = request.getParameter("startMonth") != null ? HtmlUtil.escape(request.getParameter("startMonth")) : "";
    
    boolean hasData = result != null && !result.isEmpty();

    double sumInterest = 0;
    double sumTotal = 0;
    double monthlyPayment = 0;
    if (hasData) {
        monthlyPayment = result.get(0).getTotalPayment().doubleValue();
        for (AmortizationRow r : result) {
            sumInterest += r.getInterestPaid().doubleValue();
            sumTotal += r.getTotalPayment().doubleValue();
        }
    }
%>
<!DOCTYPE html>
<html>
  <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <title>FinTrack - Loan Amortization</title>

      <link rel="preconnect" href="https://fonts.googleapis.com">
      <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
      <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

      <link rel="stylesheet" href="<%= ctxPath %>/static/css/tokens.css">
      <link rel="stylesheet" href="<%= ctxPath %>/static/css/base.css">
      <link rel="stylesheet" href="<%= ctxPath %>/static/css/layout.css">
      <link rel="stylesheet" href="<%= ctxPath %>/static/css/components.css">
      <link rel="stylesheet" href="<%= ctxPath %>/static/css/tables.css">
      <link rel="stylesheet" href="<%= ctxPath %>/static/css/forms.css">
      <link rel="stylesheet" href="<%= ctxPath %>/static/css/calculator.css">

  </head>
  <body>
  <header class="site-header">
      <a href="<%= ctxPath %>/" class="brand">
          <i data-lucide="triangle"></i> FinTrack
      </a>
      <nav class="site-header__nav flex-row" aria-label="Primary">
          <button class="btn btn-ghost btn-guest-session">
              <i data-lucide="beaker"></i> Guest session
          </button>
          <div class="nav-separator"></div>
          <a href="<%= ctxPath %>/login" class="btn btn-ghost">Log in</a>
          <a href="<%= ctxPath %>/register" class="btn btn-primary">Create account</a>
      </nav>
  </header>

  <main class="container container--wide section">

      <nav class="flex-row gap-2 mb-6 text-sm">
          <a href="<%= ctxPath %>/guest/report" class="muted breadcrumb-link">Calculators</a>
          <span class="muted">&rsaquo;</span>
          <strong>Loan amortization</strong>
      </nav>

      <header class="mb-10">
          <h1 class="text-3xl mb-2">Loan Amortization</h1>
          <p class="text-lg muted m-0">Estimate monthly payments, total interest, and the first months of your repayment schedule.</p>
      </header>

      <div class="calc-layout mb-10">
          
          <aside>
              <article class="card">
                  <div class="flex-row gap-3 mb-6">
                      <div class="form-header-icon">
                          <i data-lucide="grid-3x3"></i>
                      </div>
                      <h2 class="text-2xl font-semibold mb-0">Loan details</h2>
                  </div>

                  <% if (request.getAttribute("errorMessage") != null) { %>
                      <div class="alert alert-error mb-4">
                          <i data-lucide="alert-circle"></i>
                          <span><%= HtmlUtil.escape((String) request.getAttribute("errorMessage")) %></span>
                      </div>
                  <% } %>

                  <form class="form" action="<%= ctxPath %>/report/loan" method="POST">
                      <div class="form-field">
                          <label class="form-label">Loan amount</label>
                          <div class="form-input-group form-input-group--prefix-calc">
                              <span class="input-prefix-calc">€</span>
                              <input type="text" name="principal" class="form-input" value="<%= principalStr %>" />
                          </div>
                          <span class="form-help">Total amount borrowed</span>
                      </div>

                      <div class="form-field">
                          <label class="form-label">Annual interest rate</label>
                          <div class="form-input-group form-input-group--suffix-calc">
                              <input type="text" name="annualRate" class="form-input" value="<%= annualRateStr %>" />
                              <span class="input-suffix-calc">%</span>
                          </div>
                          <span class="form-help">Yearly interest percentage</span>
                      </div>

                      <div class="form-field">
                          <label class="form-label">Loan term</label>
                          <input type="text" name="months" class="form-input" value="<%= monthsStr %>" />
                          <span class="form-help">Duration in months</span>
                      </div>

                      <div class="form-field">
                          <label class="form-label">Start month</label>
                          <input type="text" name="startMonth" class="form-input" value="<%= startMonthStr %>" />
                          <span class="form-help">First month of payment</span>
                      </div>

                      <button type="submit" class="btn btn-primary btn-block mt-4">
                          <i data-lucide="rotate-cw"></i> Calculate payment
                      </button>
                  </form>
              </article>
          </aside>

          <% if (hasData) { %>
          <article class="card">
              <h2 class="text-2xl font-semibold mb-2">Payment summary</h2>
              <p class="muted text-sm mb-8">Estimated from €<%= principal != null ? String.format("%,.0f", principal) : principalStr %> principal, <%= annualRate %>% APR, and <%= monthsStr %> months.</p>

              <div class="grid grid--3 mb-8">
                  <article class="card">
                      <div class="flex-row gap-2 mb-3 muted text-sm">
                          <div class="badge badge-neutral badge-icon">
                              <i data-lucide="rotate-cw" class="text-primary"></i>
                          </div>
                          <span>Monthly payment</span>
                      </div>
                      <h3 class="text-3xl text-primary font-normal mb-0">
                          <%= String.format("€%,.2f", monthlyPayment) %>
                      </h3>
                      <p class="text-xs muted m-0">Over <%= monthsStr %> months</p>
                  </article>

                  <article class="card">
                      <div class="flex-row gap-2 mb-3 muted text-sm">
                          <div class="badge badge-wallet-cash badge-icon badge-warning">
                              <i data-lucide="percent" class="text-warning"></i>
                          </div>
                          <span>Total interest</span>
                      </div>
                      <h3 class="text-3xl font-normal mb-0">
                          <%= String.format("€%,.2f", sumInterest) %>
                      </h3>
                      <p class="text-xs muted m-0"><%= annualRate %>% APR</p>
                  </article>

                  <article class="card">
                      <div class="flex-row gap-2 mb-3 muted text-sm">
                          <div class="badge badge-neutral badge-icon">
                              <div class="legend-dot legend-dot--interest"></div>
                          </div>
                          <span>Total paid</span>
                      </div>
                      <h3 class="text-3xl font-normal mb-0">
                          <%= String.format("€%,.2f", sumTotal) %>
                      </h3>
                      <p class="text-xs muted m-0">Principal + interest</p>
                  </article>
              </div>

              <h4 class="font-semibold mb-2">What this means</h4>
              <p class="muted text-sm m-0">Most of the first payments go partly toward interest, then gradually shift toward principal as the remaining balance decreases.</p>
          </article>
          <% } else { %>
          <!-- EMPTY STATE -->
          <article class="card calc-empty-state">
              <div class="calc-empty-icon">
                  <i data-lucide="bar-chart-3"></i>
              </div>
              <h3 class="calc-empty-title">No projection data</h3>
              <p class="calc-empty-text">
                  Enter your loan details in the form on the left and click <strong>Calculate payment</strong> to see your payment summary and amortization schedule.
              </p>
          </article>
          <% } %>
      </div>

      <% if (hasData) { %>
      <article class="card card--table table-paginated-container mb-10" data-per-page="12" data-item-name="months">
          <div class="card-header-border flex-row justify-between">
              <h3 class="card-title m-0 text-xl font-semibold">Amortization schedule</h3>
              <button class="btn btn-ghost btn-sm">
                  <i data-lucide="download"></i> Export CSV
              </button>
          </div>
          <div class="table-wrapper">
              <table class="table">
                  <thead>
                  <tr>
                      <th class="pl-6">MONTH</th>
                      <th>PAYMENT</th>
                      <th>PRINCIPAL</th>
                      <th>INTEREST</th>
                      <th class="pr-6">REMAINING BALANCE</th>
                  </tr>
                  </thead>
                  <tbody class="table-paginate-body">
                  <% for (int i = 0; i < result.size(); i++) {
                      AmortizationRow row = result.get(i); %>
                  <tr>
                      <td class="pl-6 muted">#<%= String.format("%02d", row.getMonth()) %></td>
                      <td class="font-semibold"><%= String.format("€%,.2f", row.getTotalPayment()) %></td>
                      <td class="text-primary font-medium"><%= String.format("€%,.2f", row.getPrincipalPaid()) %></td>
                      <td class="text-warning font-medium"><%= String.format("€%,.2f", row.getInterestPaid()) %></td>
                      <td class="pr-6 font-semibold"><%= String.format("€%,.2f", row.getRemainingBalance()) %></td>
                  </tr>
                  <% } %>
                  </tbody>
              </table>
          </div>
          <div class="table-footer">
              <span class="text-sm muted table-pagination-info"></span>
              <div class="table-footer--buttons table-pagination-buttons"></div>
          </div>
      </article>
      <% } %>

  </main>

  <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
  <script src="<%= ctxPath %>/static/js/table-control.js"></script>
  <script>
      lucide.createIcons();
  </script>

  </body>
</html>