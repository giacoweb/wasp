<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="hasData" value="${not empty amortizationList}"/>

<c:if test="${hasData}">
    <c:set var="sumInterest" value="0"/>
    <c:set var="sumTotal" value="0"/>
    <c:forEach var="row" items="${amortizationList}">
        <c:set var="sumInterest" value="${sumInterest + row.interestPaid}"/>
        <c:set var="sumTotal"    value="${sumTotal + row.totalPayment}"/>
    </c:forEach>
    <c:set var="monthlyPayment" value="${amortizationList[0].totalPayment}"/>
</c:if>

<!DOCTYPE html>
<html>
  <head>
      <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <title>FinTrack - Loan Amortization</title>

      <link rel="preconnect" href="https://fonts.googleapis.com">
      <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
      <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

      <link rel="stylesheet" href="${ctx}/static/css/tokens.css">
      <link rel="stylesheet" href="${ctx}/static/css/base.css">
      <link rel="stylesheet" href="${ctx}/static/css/layout.css">
      <link rel="stylesheet" href="${ctx}/static/css/components.css">
      <link rel="stylesheet" href="${ctx}/static/css/tables.css">
      <link rel="stylesheet" href="${ctx}/static/css/forms.css">
      <link rel="stylesheet" href="${ctx}/static/css/calculator.css">

  </head>
  <body>
  <header class="site-header">
      <a href="${ctx}/" class="brand">
          <i data-lucide="triangle"></i> FinTrack
      </a>
      <nav class="site-header__nav flex-row" aria-label="Primary">
          <button class="btn btn-ghost btn-guest-session">
              <i data-lucide="beaker"></i> Guest session
          </button>
          <div class="nav-separator"></div>
          <a href="${ctx}/login" class="btn btn-ghost">Log in</a>
          <a href="${ctx}/register" class="btn btn-primary">Create account</a>
      </nav>
  </header>

  <main class="container container--wide section">

      <nav class="breadcrumb">
          <a href="${ctx}/guest/report">Calculators</a> / <span>Loan amortization</span>
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

                  <c:if test="${not empty errorMessage}">
                      <div class="alert alert-error mb-4">
                          <i data-lucide="alert-circle"></i>
                          <span><c:out value="${errorMessage}"/></span>
                      </div>
                  </c:if>

                  <form class="form" action="${ctx}/report/loan" method="POST">
                      <div class="form-field">
                          <label class="form-label">Loan amount</label>
                          <div class="form-input-group form-input-group--prefix-calc">
                              <span class="input-prefix-calc">€</span>
                              <input type="number" name="principal" class="form-input" step="0.01" min="0.01" required value="<c:out value='${param.principal}'/>" />
                          </div>
                          <span class="form-help">Total amount borrowed</span>
                      </div>

                      <div class="form-field">
                          <label class="form-label">Annual interest rate</label>
                          <div class="form-input-group form-input-group--suffix-calc">
                              <input type="number" name="annualRate" class="form-input" step="0.1" min="0" max="100" required value="<c:out value='${param.annualRate}'/>" />
                              <span class="input-suffix-calc">%</span>
                          </div>
                          <span class="form-help">Yearly interest percentage</span>
                      </div>

                      <div class="form-field">
                          <label class="form-label">Loan term</label>
                          <input type="number" name="months" class="form-input" step="1" min="1" max="600" required value="<c:out value='${param.months}'/>" />
                          <span class="form-help">Duration in months</span>
                      </div>

                      <div class="form-field">
                          <label class="form-label">Start month</label>
                          <select name="startMonth" class="form-input">
                              <c:forEach var="m" items="${['January','February','March','April','May','June','July','August','September','October','November','December']}">
                                  <option value="${m}" ${param.startMonth eq m ? 'selected' : ''}>${m}</option>
                              </c:forEach>
                          </select>
                          <span class="form-help">First month of payment</span>
                      </div>

                      <button type="submit" class="btn btn-primary btn-block mt-4">
                          <i data-lucide="calculator"></i> Calculate
                      </button>
                  </form>
              </article>

              <article class="card mt-4 flex align-start gap-3">
                  <div class="advice-icon">
                      <i data-lucide="info"></i>
                  </div>
                  <div>
                      <h4 class="text-sm m-0 mb-1">How amortization works</h4>
                      <p class="text-sm muted m-0">
                          Early payments cover mostly interest. Over time, more goes toward principal as the remaining balance shrinks.
                      </p>
                  </div>
              </article>
          </aside>

          <div class="flex-col gap-6 calc-column-fill">

              <c:choose>
                  <c:when test="${hasData}">

                      <div class="grid grid--3">
                          <article class="card">
                              <div class="flex-row gap-2 mb-3 muted text-sm">
                                  <div class="badge badge-primary badge-icon">
                                      <i data-lucide="rotate-cw"></i>
                                  </div>
                                  <span>Monthly payment</span>
                              </div>
                              <h3 class="text-3xl text-primary font-normal mb-0">
                                  €<fmt:formatNumber value="${monthlyPayment}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                              </h3>
                              <p class="text-xs muted m-0">Over <c:out value="${param.months}"/> months</p>
                          </article>

                          <article class="card">
                              <div class="flex-row gap-2 mb-3 muted text-sm">
                                  <div class="badge badge-neutral badge-icon">
                                      <i data-lucide="percent"></i>
                                  </div>
                                  <span>Total interest</span>
                              </div>
                              <h3 class="text-3xl font-normal mb-0">
                                  €<fmt:formatNumber value="${sumInterest}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                              </h3>
                              <p class="text-xs muted m-0">${annualRate}% APR</p>
                          </article>

                          <article class="card">
                              <div class="flex-row gap-2 mb-3 muted text-sm">
                                  <div class="badge badge-neutral badge-icon">
                                      <i data-lucide="landmark"></i>
                                  </div>
                                  <span>Total paid</span>
                              </div>
                              <h3 class="text-3xl font-normal mb-0">
                                  €<fmt:formatNumber value="${sumTotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                              </h3>
                              <p class="text-xs muted m-0">Principal + interest</p>
                          </article>
                      </div>

                      <article class="card card--table card--table-fill table-paginated-container" data-per-page="10" data-item-name="months">
                          <div class="card-header-border">
                              <h3 class="card-title m-0">Amortization schedule</h3>
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
                                      <c:forEach var="row" items="${amortizationList}">
                                          <tr>
                                              <td class="pl-6 muted">#<fmt:formatNumber value="${row.month}" minIntegerDigits="2"/></td>
                                              <td class="font-semibold">€<fmt:formatNumber value="${row.totalPayment}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
                                              <td class="text-primary font-medium">€<fmt:formatNumber value="${row.principalPaid}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
                                              <td class="text-warning font-medium">€<fmt:formatNumber value="${row.interestPaid}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
                                              <td class="pr-6 font-semibold">€<fmt:formatNumber value="${row.remainingBalance}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
                                          </tr>
                                      </c:forEach>
                                  </tbody>
                              </table>
                          </div>
                          <div class="table-footer">
                              <span class="text-sm muted table-pagination-info"></span>
                              <div class="table-footer--buttons table-pagination-buttons"></div>
                          </div>
                      </article>

                  </c:when>
                  <c:otherwise>
                      <article class="card empty-state flex-col align-center justify-center calc-empty-fill">
                          <div class="empty-state__icon">
                              <i data-lucide="bar-chart-3"></i>
                          </div>
                          <h3 class="empty-state__title">No projection data</h3>
                          <p class="empty-state__desc">
                              Enter your loan details in the form on the left and click <strong>Calculate</strong> to see your payment summary and amortization schedule.
                          </p>
                      </article>
                  </c:otherwise>
              </c:choose>

          </div>
      </div>

  </main>

  <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
  <script src="${ctx}/static/js/table-control.js"></script>
  <script src="${ctx}/static/js/icons.js"></script>

  </body>
</html>
