<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
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

    <link rel="stylesheet" href="${ctx}/static/css/tokens.css">
    <link rel="stylesheet" href="${ctx}/static/css/base.css">
    <link rel="stylesheet" href="${ctx}/static/css/layout.css">
    <link rel="stylesheet" href="${ctx}/static/css/components.css">
    <link rel="stylesheet" href="${ctx}/static/css/forms.css">
    <link rel="stylesheet" href="${ctx}/static/css/dashboard.css">
    <link rel="stylesheet" href="${ctx}/static/css/pac.css">
</head>
<body>

<div class="app-shell">

    <jsp:include page="/WEB-INF/jsp/common/_sidebar.jsp">
        <jsp:param name="navActive" value="pac"/>
    </jsp:include>

    <main class="app-main">

        <nav class="breadcrumb" aria-label="Breadcrumb">
            <a href="${ctx}/dashboard">FinTrack</a> / <span>PAC Plans</span>
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

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error" role="alert">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success" role="status">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
                </svg>
                <c:out value="${successMessage}"/>
            </div>
        </c:if>

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
                    <p class="pac-kpi__value tabular">${activeCount}</p>
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
                    <p class="pac-kpi__value tabular">&euro;${monthlyCommitmentFormatted}</p>
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
                    <p class="pac-kpi__value tabular">&euro;${combinedTargetFormatted}</p>
                </div>
            </article>
        </section>

        <c:choose>
            <c:when test="${empty planViews}">

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

            </c:when>
            <c:otherwise>

                <section class="pac-grid" aria-label="Your plans">
                    <c:forEach var="view" items="${planViews}">
                        <article class="card pac-card ${view.plan.active ? '' : 'is-inactive'}">

                            <div class="pac-card__head">
                                <h3 class="pac-card__title"><c:out value="${view.plan.assetName}"/></h3>
                                <span class="badge ${view.plan.active ? 'badge-primary' : ''}">
                                    ${view.plan.active ? 'Active' : 'Paused'}
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
                                    Started ${view.startedLabel}
                                </span>
                                <span class="pac-chip">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                                        <polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/>
                                        <polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/>
                                    </svg>
                                    &euro;${view.monthlyFormatted} / month
                                </span>
                            </div>

                            <div class="pac-card__progress">
                                <div class="pac-card__progress-head">
                                    <span>Progress to target</span>
                                    <span class="tabular">${view.pctDisplay}%</span>
                                </div>
                                <div class="progress" role="progressbar"
                                     aria-valuemin="0" aria-valuemax="100"
                                     aria-valuenow="${view.progressPct}">
                                    <div class="progress__bar js-pac-progress" data-pct="${view.progressPct}"></div>
                                </div>
                                <div class="pac-card__progress-foot">
                                    <span class="pac-card__progress-amount"
                                          title="Estimated from monthly amount &times; months since start date. Does not account for pauses or past changes.">
                                        &euro;${view.savedFormatted}
                                        <span class="muted">(est.)</span>
                                    </span>
                                    <span class="muted">of &euro;${view.targetFormatted}</span>
                                </div>
                            </div>

                            <div class="pac-card__footer">
                                <button type="button" class="btn btn-ghost btn-sm js-edit-plan"
                                        data-id="${view.plan.id}"
                                        data-asset-name="<c:out value='${view.plan.assetName}'/>"
                                        data-monthly-amount="${view.plan.monthlyAmount}"
                                        data-target-amount="${view.plan.targetAmount}"
                                        data-start-date="${view.plan.startDate}"
                                        data-active="${view.plan.active}"
                                        aria-label="Edit plan ${view.plan.assetName}">
                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                         stroke="currentColor" stroke-width="2" aria-hidden="true">
                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                                    </svg>
                                    Edit
                                </button>
                                <form class="js-delete-form"
                                      action="${ctx}/pac/delete" method="post"
                                      data-asset-name="<c:out value='${view.plan.assetName}'/>">
                                    <input type="hidden" name="planId" value="${view.plan.id}">
                                    <button type="submit" class="btn btn-ghost btn-sm"
                                            aria-label="Delete plan ${view.plan.assetName}">
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
                    </c:forEach>
                </section>

            </c:otherwise>
        </c:choose>

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

        <form action="${ctx}/pac/create" method="post" class="form js-validate" novalidate>

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

        <form action="${ctx}/pac/edit" method="post" class="form js-validate" novalidate>
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

<script src="${ctx}/static/js/pac.js"></script>

</body>
</html>
