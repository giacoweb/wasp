<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack &middot; Calculators</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

    <link rel="stylesheet" href="${ctx}/static/css/tokens.css">
    <link rel="stylesheet" href="${ctx}/static/css/base.css">
    <link rel="stylesheet" href="${ctx}/static/css/layout.css">
    <link rel="stylesheet" href="${ctx}/static/css/components.css">
    <link rel="stylesheet" href="${ctx}/static/css/guest.css">
</head>
<body class="guest-page">

<header class="site-header">
    <a href="${ctx}/" class="brand">
        <i data-lucide="triangle"></i>
        FinTrack
    </a>
    <nav class="site-header__nav" aria-label="Primary">
        <a href="${ctx}/guest/report" class="guest-session-btn">
            <i data-lucide="alert-triangle"></i>
            Guest session
        </a>
        <a href="${ctx}/login"    class="btn btn-ghost">Log in</a>
        <a href="${ctx}/register" class="btn btn-primary">Create account</a>
    </nav>
</header>

<main class="section calc-hub-section">

    <h1 class="calc-hub__heading calc-hub__heading--spacious">Try our calculators &mdash; no account needed.</h1>
    <p class="calc-hub__sub calc-hub__sub--spacious muted">Three quick tools for instant financial clarity. Sign up later to save results.</p>

    <div class="calc-hub__grid">

        <a href="${ctx}/guest/loan" class="card calc-card">
            <div class="feature-icon">
                <i data-lucide="landmark"></i>
            </div>
            <h2 class="feature-title">Loan Amortization</h2>
            <p class="feature-desc">See your monthly payment and full amortization.</p>
            <hr class="calc-card__divider">
            <span class="calc-card__link">
                Open calculator
                <i data-lucide="arrow-right"></i>
            </span>
        </a>

        <a href="${ctx}/guest/compound" class="card calc-card">
            <div class="feature-icon">
                <i data-lucide="trending-up"></i>
            </div>
            <h2 class="feature-title">Compound Interest</h2>
            <p class="feature-desc">Project how contributions grow year by year.</p>
            <hr class="calc-card__divider">
            <span class="calc-card__link">
                Open calculator
                <i data-lucide="arrow-right"></i>
            </span>
        </a>

        <a href="${ctx}/guest/budget" class="card calc-card">
            <div class="feature-icon">
                <i data-lucide="minus-square"></i>
            </div>
            <h2 class="feature-title">Budget Analysis</h2>
            <p class="feature-desc">Add incomes and expenses, get an instant breakdown.</p>
            <hr class="calc-card__divider">
            <span class="calc-card__link">
                Open calculator
                <i data-lucide="arrow-right"></i>
            </span>
        </a>

    </div>

</main>

<div class="calc-hub__save-bar">
    <div class="calc-hub__save-bar__icon">
        <i data-lucide="check"></i>
    </div>
    <span><strong>Nothing is saved</strong> &nbsp;unless you create an account.</span>
</div>

<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
<script src="${ctx}/static/js/icons.js"></script>
</body>
</html>
