<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.model.PacPlan" %>
<%@ page import="it.unipd.dei.webapp.model.User" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.temporal.ChronoUnit" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("authenticatedUser");
    List<PacPlan> plans   = (List<PacPlan>) request.getAttribute("pacPlans");
    String errorMessage   = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    String ctxPath = request.getContextPath();

    DateTimeFormatter monthYearFmt = DateTimeFormatter.ofPattern("MMM yyyy");
    LocalDate today = LocalDate.now();

    int activeCount = 0;
    BigDecimal monthlyCommitment = BigDecimal.ZERO;
    BigDecimal combinedTarget    = BigDecimal.ZERO;
    if (plans != null) {
        for (PacPlan p : plans) {
            if (p.isActive()) {
                activeCount++;
                if (p.getMonthlyAmount() != null) {
                    monthlyCommitment = monthlyCommitment.add(p.getMonthlyAmount());
                }
            }
            if (p.getTargetAmount() != null) {
                combinedTarget = combinedTarget.add(p.getTargetAmount());
            }
        }
    }
    boolean hasPlans = plans != null && !plans.isEmpty();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PAC Plans &middot; FinTrack</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

    <link rel="stylesheet" href="<%= ctxPath %>/static/css/tokens.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/base.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/layout.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/components.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/forms.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/dashboard.css">
    <link rel="stylesheet" href="<%= ctxPath %>/static/css/pac.css">
</head>
<body>

<div class="app-shell">

    <jsp:include page="/WEB-INF/jsp/common/_sidebar.jsp">
        <jsp:param name="navActive" value="pac"/>
    </jsp:include>

    <main class="app-main">

        <nav class="breadcrumb" aria-label="Breadcrumb">
            <a href="<%= ctxPath %>/dashboard">FinTrack</a> / <span>PAC Plans</span>
        </nav>

        <header class="page-header">
            <div>
                <h1>PAC Plans</h1>
                <p class="muted">Dollar-cost averaging toward your goals.</p>
            </div>
            <button class="btn btn-primary" id="pac-create-btn" type="button">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                New plan
            </button>
        </header>

        <% if (errorMessage != null) { %>
            <div class="alert alert-error" role="alert">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                <%= HtmlUtil.escape(errorMessage) %>
            </div>
        <% } %>
        <% if (successMessage != null) { %>
            <div class="alert alert-success" role="status">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
                </svg>
                <%= HtmlUtil.escape(successMessage) %>
            </div>
        <% } %>

        <section class="pac-kpi-strip" aria-label="Summary">

            <article class="card pac-kpi">
                <div class="pac-kpi__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M19 5c-1.5 0-2.8 1.4-3 2-3.5-1.5-11-.3-11 5 0 1.8 0 3 2 4.5V20h4v-2h3v2h4v-4c1-.5 1.7-1 2-2h2v-4h-2c0-1-.5-1.5-1-2h0V5z"/>
                        <path d="M2 9v1c0 1.1.9 2 2 2h1"/><path d="M16 11h0"/>
                    </svg>
                </div>
                <div>
                    <p class="pac-kpi__label">Active plans</p>
                    <p class="pac-kpi__value tabular"><%= activeCount %></p>
                </div>
            </article>

            <article class="card pac-kpi">
                <div class="pac-kpi__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/>
                        <polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/>
                    </svg>
                </div>
                <div>
                    <p class="pac-kpi__label">Monthly commitment</p>
                    <p class="pac-kpi__value tabular">&euro;<%= String.format("%,.2f", monthlyCommitment) %></p>
                </div>
            </article>

            <article class="card pac-kpi">
                <div class="pac-kpi__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/>
                    </svg>
                </div>
                <div>
                    <p class="pac-kpi__label">Combined target</p>
                    <p class="pac-kpi__value tabular">&euro;<%= String.format("%,.2f", combinedTarget) %></p>
                </div>
            </article>
        </section>

        <% if (!hasPlans) { %>

            <section class="card">
                <div class="empty-state">
                    <div class="empty-state__icon" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M19 5c-1.5 0-2.8 1.4-3 2-3.5-1.5-11-.3-11 5 0 1.8 0 3 2 4.5V20h4v-2h3v2h4v-4c1-.5 1.7-1 2-2h2v-4h-2c0-1-.5-1.5-1-2h0V5z"/>
                        </svg>
                    </div>
                    <h2 class="empty-state__title">No plans yet</h2>
                    <p class="empty-state__desc">Start your first PAC plan to grow steadily.</p>
                    <button class="btn btn-primary" id="pac-empty-create-btn" type="button">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                             stroke="currentColor" stroke-width="2" aria-hidden="true">
                            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                        </svg>
                        New plan
                    </button>
                </div>
            </section>

        <% } else { %>

            <section class="pac-grid" aria-label="Your plans">
            <% for (PacPlan plan : plans) {
                long monthsSince = plan.getStartDate() != null
                        ? Math.max(0, ChronoUnit.MONTHS.between(plan.getStartDate(), today))
                        : 0;
                BigDecimal saved = plan.getMonthlyAmount() != null
                        ? plan.getMonthlyAmount().multiply(BigDecimal.valueOf(monthsSince))
                        : BigDecimal.ZERO;
                BigDecimal target = plan.getTargetAmount() != null
                        ? plan.getTargetAmount() : BigDecimal.ZERO;
                BigDecimal progressPct = BigDecimal.ZERO;
                if (target.signum() > 0) {
                    progressPct = saved.multiply(BigDecimal.valueOf(100))
                            .divide(target, 1, RoundingMode.HALF_UP);
                    if (progressPct.compareTo(BigDecimal.valueOf(100)) > 0) {
                        progressPct = BigDecimal.valueOf(100);
                    }
                    if (saved.compareTo(target) > 0) saved = target;
                }
                String startedLabel = plan.getStartDate() != null
                        ? plan.getStartDate().format(monthYearFmt) : "—";
                String safeAssetName = HtmlUtil.escape(plan.getAssetName());
                String monthlyStr = plan.getMonthlyAmount() != null
                        ? String.format("%,.2f", plan.getMonthlyAmount()) : "0.00";
                String targetStr = String.format("%,.2f", target);
                String savedStr  = String.format("%,.2f", saved);
                String pctDisplay = progressPct.stripTrailingZeros().toPlainString();
                if (pctDisplay.startsWith(".")) pctDisplay = "0" + pctDisplay;
            %>
                <article class="card pac-card <%= plan.isActive() ? "" : "is-inactive" %>">

                    <div class="pac-card__head">
                        <h3 class="pac-card__title"><%= safeAssetName %></h3>
                        <span class="badge <%= plan.isActive() ? "badge-primary" : "" %>">
                            <%= plan.isActive() ? "Active" : "Paused" %>
                        </span>
                    </div>

                    <div class="pac-card__chips">
                        <span class="pac-chip">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                                <rect width="18" height="18" x="3" y="4" rx="2"/>
                                <line x1="16" y1="2" x2="16" y2="6"/>
                                <line x1="8" y1="2" x2="8" y2="6"/>
                                <line x1="3" y1="10" x2="21" y2="10"/>
                            </svg>
                            Started <%= startedLabel %>
                        </span>
                        <span class="pac-chip">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                                <polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/>
                                <polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/>
                            </svg>
                            &euro;<%= monthlyStr %> / month
                        </span>
                    </div>

                    <div class="pac-card__progress">
                        <div class="pac-card__progress-head">
                            <span>Progress to target</span>
                            <span class="tabular"><%= pctDisplay %>%</span>
                        </div>
                        <div class="progress" role="progressbar"
                             aria-valuemin="0" aria-valuemax="100"
                             aria-valuenow="<%= progressPct.intValue() %>">
                            <div class="progress__bar" style="--pct: <%= progressPct %>%;"></div>
                        </div>
                        <div class="pac-card__progress-foot">
                            <span class="pac-card__progress-amount"
                                  title="Estimated from monthly amount &times; months since start date. Does not account for pauses or past changes.">
                                &euro;<%= savedStr %>
                                <span class="muted">(est.)</span>
                            </span>
                            <span class="muted">of &euro;<%= targetStr %></span>
                        </div>
                    </div>

                    <div class="pac-card__footer">
                        <button type="button" class="btn btn-ghost btn-sm js-edit-plan"
                                data-id="<%= plan.getId() %>"
                                data-asset-name="<%= safeAssetName %>"
                                data-monthly-amount="<%= plan.getMonthlyAmount() %>"
                                data-target-amount="<%= plan.getTargetAmount() %>"
                                data-start-date="<%= plan.getStartDate() %>"
                                data-active="<%= plan.isActive() %>"
                                aria-label="Edit plan <%= safeAssetName %>">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                 stroke="currentColor" stroke-width="2" aria-hidden="true">
                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                            </svg>
                            Edit
                        </button>
                        <form class="js-delete-form"
                              action="<%= ctxPath %>/pac/delete" method="post"
                              data-asset-name="<%= safeAssetName %>">
                            <input type="hidden" name="planId" value="<%= plan.getId() %>">
                            <button type="submit" class="btn btn-ghost btn-sm"
                                    aria-label="Delete plan <%= safeAssetName %>">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                     stroke="currentColor" stroke-width="2" aria-hidden="true">
                                    <polyline points="3 6 5 6 21 6"/>
                                    <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
                                    <path d="M10 11v6"/><path d="M14 11v6"/>
                                    <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/>
                                </svg>
                                Delete
                            </button>
                        </form>
                    </div>
                </article>
            <% } %>
            </section>
        <% } %>

    </main>
</div>

<div class="modal-overlay" id="pac-create-modal"
     role="dialog" aria-modal="true" aria-labelledby="pac-create-title" hidden>
    <div class="card modal-card">
        <div class="card-header">
            <h2 class="card-title" id="pac-create-title">Create PAC plan</h2>
            <button type="button" class="btn btn-icon btn-ghost js-modal-close" aria-label="Close">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
            </button>
        </div>

        <form action="<%= ctxPath %>/pac/create" method="post" class="form js-validate" novalidate>

            <div class="form-field">
                <label class="form-label form-required" for="create-asset-name">Asset name</label>
                <input class="form-input" id="create-asset-name" name="assetName"
                       type="text" maxlength="120" required placeholder="e.g. S&amp;P 500 ETF">
                <span class="form-error"></span>
            </div>

            <div class="form-grid-2">
                <div class="form-field">
                    <label class="form-label form-required" for="create-monthly-amount">Monthly (&euro;)</label>
                    <input class="form-input" id="create-monthly-amount" name="monthlyAmount"
                           type="number" step="0.01" min="0.01" required placeholder="200.00">
                    <span class="form-error"></span>
                </div>
                <div class="form-field">
                    <label class="form-label form-required" for="create-target-amount">Target (&euro;)</label>
                    <input class="form-input" id="create-target-amount" name="targetAmount"
                           type="number" step="0.01" min="0.01" required placeholder="10000.00">
                    <span class="form-error"></span>
                </div>
            </div>

            <div class="form-field">
                <label class="form-label form-required" for="create-start-date">Start date</label>
                <input class="form-input" id="create-start-date" name="startDate"
                       type="date" required>
                <span class="form-error"></span>
            </div>

            <div class="modal__footer">
                <button type="button" class="btn btn-ghost js-modal-close">Cancel</button>
                <button type="submit" class="btn btn-primary">Create plan</button>
            </div>
        </form>
    </div>
</div>

<div class="modal-overlay" id="pac-edit-modal"
     role="dialog" aria-modal="true" aria-labelledby="pac-edit-title" hidden>
    <div class="card modal-card">
        <div class="card-header">
            <h2 class="card-title" id="pac-edit-title">Edit PAC plan</h2>
            <button type="button" class="btn btn-icon btn-ghost js-modal-close" aria-label="Close">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
            </button>
        </div>

        <form action="<%= ctxPath %>/pac/edit" method="post" class="form js-validate" novalidate>
            <input type="hidden" name="planId">

            <div class="form-field">
                <label class="form-label form-required" for="edit-asset-name">Asset name</label>
                <input class="form-input" id="edit-asset-name" name="assetName"
                       type="text" maxlength="120" required>
                <span class="form-error"></span>
            </div>

            <div class="form-grid-2">
                <div class="form-field">
                    <label class="form-label form-required" for="edit-monthly-amount">Monthly (&euro;)</label>
                    <input class="form-input" id="edit-monthly-amount" name="monthlyAmount"
                           type="number" step="0.01" min="0.01" required>
                    <span class="form-error"></span>
                </div>
                <div class="form-field">
                    <label class="form-label form-required" for="edit-target-amount">Target (&euro;)</label>
                    <input class="form-input" id="edit-target-amount" name="targetAmount"
                           type="number" step="0.01" min="0.01" required>
                    <span class="form-error"></span>
                </div>
            </div>

            <div class="form-field">
                <label class="form-label form-required" for="edit-start-date">Start date</label>
                <input class="form-input" id="edit-start-date" name="startDate"
                       type="date" required>
                <span class="form-error"></span>
            </div>

            <div class="form-field">
                <label class="pac-active-row" for="edit-active">
                    <span class="switch">
                        <input type="checkbox" id="edit-active" class="switch__input"
                               role="switch" aria-checked="true">
                        <span class="switch__slider" aria-hidden="true"></span>
                    </span>
                    <span>Active</span>
                </label>
                <input type="hidden" id="edit-active-value" name="active" value="true">
            </div>

            <div class="modal__footer">
                <button type="button" class="btn btn-ghost js-modal-close">Cancel</button>
                <button type="submit" class="btn btn-primary">Save changes</button>
            </div>
        </form>
    </div>
</div>

<script src="<%= ctxPath %>/static/js/pac.js"></script>

</body>
</html>
