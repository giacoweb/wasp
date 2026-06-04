<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="nav" value="${param.navActive}"/>
<c:set var="me"  value="${sessionScope.authenticatedUser}"/>
<div class="mobile-header">
    <button class="hamburger" id="hamburger" type="button" title="Menu">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
    </button>
    <a href="${ctx}/dashboard" class="brand">
        <span class="brand-logo">▲</span> FinTrack
    </a>
</div>
<div class="sidebar-backdrop" id="sidebar-backdrop"></div>
<aside class="app-sidebar">
    <div class="sidebar-brand">
        <a href="${ctx}/dashboard" class="brand">
            <span class="brand-logo">▲</span><span class="brand-label"> FinTrack</span>
        </a>
        <button class="sidebar-toggle" id="sidebar-toggle" type="button" title="Toggle sidebar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>
        </button>
    </div>
    <nav style="margin-top: var(--space-8);">
        <ul class="nav-menu">
            <li>
                <a href="${ctx}/dashboard" class="nav-item${nav eq 'dashboard' ? ' active' : ''}" title="Dashboard">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="7" height="9" x="3" y="3" rx="1"/><rect width="7" height="5" x="14" y="3" rx="1"/><rect width="7" height="9" x="14" y="12" rx="1"/><rect width="7" height="5" x="3" y="16" rx="1"/></svg>
                    <span class="nav-label">Dashboard</span>
                </a>
            </li>
            <li>
                <a href="${ctx}/wallets" class="nav-item${nav eq 'wallets' ? ' active' : ''}" title="Wallets">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/><path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/><path d="M18 12a2 2 0 0 0 0 4h4v-4Z"/></svg>
                    <span class="nav-label">Wallets</span>
                </a>
            </li>
            <li>
                <a href="${ctx}/transactions" class="nav-item${nav eq 'transactions' ? ' active' : ''}" title="Transactions">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m21 16-4 4-4-4"/><path d="M17 20V4"/><path d="m3 8 4-4 4 4"/><path d="M7 4v16"/></svg>
                    <span class="nav-label">Transactions</span>
                </a>
            </li>
            <li>
                <a href="${ctx}/pac" class="nav-item${nav eq 'pac' ? ' active' : ''}" title="PAC Plans">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/><polyline points="16 7 22 7 22 13"/></svg>
                    <span class="nav-label">PAC Plans</span>
                </a>
            </li>
            <li>
                <a href="${ctx}/expenses" class="nav-item${nav eq 'expenses' ? ' active' : ''}" title="Recurring Expenses">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/></svg>
                    <span class="nav-label">Recurring Expenses</span>
                </a>
            </li>
            <li>
                <a href="${ctx}/forecast" class="nav-item${nav eq 'forecast' ? ' active' : ''}" title="Forecast">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21.21 15.89A10 10 0 1 1 8 2.83"/><path d="M22 12A10 10 0 0 0 12 2v10z"/></svg>
                    <span class="nav-label">Forecast</span>
                </a>
            </li>
        </ul>
    </nav>
    <div class="user-profile">
        <div class="user-avatar">
            <c:choose>
                <c:when test="${not empty me.name}">${fn:toUpperCase(fn:substring(me.name, 0, 1))}</c:when>
                <c:otherwise>U</c:otherwise>
            </c:choose>
        </div>
        <div class="user-info">
            <span class="user-name"><c:out value="${me.name}"/> <c:out value="${me.surname}"/></span>
            <span class="user-email"><c:out value="${me.email}"/></span>
        </div>
        <form action="${ctx}/auth/logout" method="post" style="margin-left: auto;">
            <button type="submit" class="btn btn-icon btn-ghost" title="Logout" style="width:24px;height:24px;">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"/></svg>
            </button>
        </form>
    </div>
</aside>
<script src="${ctx}/static/js/sidebar.js" defer></script>
