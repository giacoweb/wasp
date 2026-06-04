<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>FinTrack &middot; See your money. Plan your future.</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

    <link rel="stylesheet" href="${ctx}/static/css/tokens.css">
    <link rel="stylesheet" href="${ctx}/static/css/base.css">
    <link rel="stylesheet" href="${ctx}/static/css/layout.css">
    <link rel="stylesheet" href="${ctx}/static/css/components.css">
</head>
<body>

<header class="site-header">
    <a href="${ctx}/" class="brand">
        <i data-lucide="triangle"></i>
        FinTrack
    </a>
    <nav class="site-header__nav" aria-label="Primary">
        <a href="${ctx}/login" class="btn btn-ghost">Log in</a>
        <a href="${ctx}/register" class="btn btn-primary">Get started</a>
    </nav>
</header>

<main>

    <section class="container container--narrow hero">

        <span class="eyebrow">
            <i data-lucide="sparkles"></i>
            Personal finance, reimagined
        </span>

        <h1>See your money.<br>Plan your future.</h1>

        <p class="hero__lead">
            Track wallets, project 24 months ahead, and grow with PAC investment plans
            &mdash; all in one calm dashboard.
        </p>

        <div class="hero__actions">
            <a href="${ctx}/register" class="btn btn-primary btn-lg">
                Create free account
                <i data-lucide="arrow-right"></i>
            </a>
            <a href="${ctx}/guest/report" class="btn btn-ghost btn-lg">
                <i data-lucide="calculator"></i>
                Continue as guest
            </a>
        </div>
    </section>

    <section class="container section" aria-label="Features">
        <div class="grid grid--auto-fit">

            <article class="card">
                <div class="feature-icon">
                    <i data-lucide="wallet"></i>
                </div>
                <h3 class="feature-title">Unified wallets</h3>
                <p class="feature-desc">All accounts, one balance.</p>
            </article>

            <article class="card">
                <div class="feature-icon">
                    <i data-lucide="trending-up"></i>
                </div>
                <h3 class="feature-title">24-month forecast</h3>
                <p class="feature-desc">Optimistic, conservative, stress test.</p>
            </article>

            <article class="card">
                <div class="feature-icon">
                    <i data-lucide="piggy-bank"></i>
                </div>
                <h3 class="feature-title">PAC plans</h3>
                <p class="feature-desc">Dollar-cost averaging toward goals.</p>
            </article>
        </div>
    </section>

</main>

<footer class="site-footer">
    <p>FinTrack &middot; University Project &middot; 2026</p>
</footer>

<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        if (window.lucide) window.lucide.createIcons();
    });
</script>

</body>
</html>
