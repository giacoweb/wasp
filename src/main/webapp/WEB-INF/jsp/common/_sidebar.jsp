<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unipd.dei.webapp.model.User" %>
<%@ page import="it.unipd.dei.webapp.util.HtmlUtil" %>
<%
    User _u = (User) session.getAttribute("authenticatedUser");
    String _ctx = request.getContextPath();
    String _nav = request.getParameter("navActive");
    if (_nav == null) _nav = "";
%>
<aside class="app-sidebar">
    <a href="<%= _ctx %>/dashboard" class="brand">
        <span class="brand-logo">▲</span> FinTrack
    </a>
    <nav style="margin-top: var(--space-8);">
        <ul class="nav-menu">
            <li>
                <a href="<%= _ctx %>/dashboard" class="nav-item<%= "dashboard".equals(_nav) ? " active" : "" %>">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="7" height="9" x="3" y="3" rx="1"/><rect width="7" height="5" x="14" y="3" rx="1"/><rect width="7" height="9" x="14" y="12" rx="1"/><rect width="7" height="5" x="3" y="16" rx="1"/></svg>
                    Dashboard
                </a>
            </li>
            <li>
                <a href="<%= _ctx %>/wallets" class="nav-item<%= "wallets".equals(_nav) ? " active" : "" %>">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/><path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/><path d="M18 12a2 2 0 0 0 0 4h4v-4Z"/></svg>
                    Wallets
                </a>
            </li>
            <li>
                <a href="<%= _ctx %>/transactions" class="nav-item<%= "transactions".equals(_nav) ? " active" : "" %>">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m21 16-4 4-4-4"/><path d="M17 20V4"/><path d="m3 8 4-4 4 4"/><path d="M7 4v16"/></svg>
                    Transactions
                </a>
            </li>
            <li>
                <a href="<%= _ctx %>/pac" class="nav-item<%= "pac".equals(_nav) ? " active" : "" %>">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/><polyline points="16 7 22 7 22 13"/></svg>
                    PAC Plans
                </a>
            </li>
            <li>
                <a href="<%= _ctx %>/expenses" class="nav-item<%= "expenses".equals(_nav) ? " active" : "" %>">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/><path d="M3 3v5h5"/></svg>
                    Recurring Expenses
                </a>
            </li>
            <li>
                <a href="<%= _ctx %>/forecast" class="nav-item<%= "forecast".equals(_nav) ? " active" : "" %>">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21.21 15.89A10 10 0 1 1 8 2.83"/><path d="M22 12A10 10 0 0 0 12 2v10z"/></svg>
                    Forecast
                </a>
            </li>
        </ul>
    </nav>
    <div class="user-profile">
        <div class="user-avatar"><%= _u.getName() != null && !_u.getName().isEmpty() ? _u.getName().substring(0, 1).toUpperCase() : "U" %></div>
        <div class="user-info">
            <span class="user-name"><%= HtmlUtil.escape(_u.getName()) %> <%= HtmlUtil.escape(_u.getSurname()) %></span>
            <span class="user-email"><%= HtmlUtil.escape(_u.getEmail()) %></span>
        </div>
        <form action="<%= _ctx %>/auth/logout" method="post" style="margin-left: auto;">
            <button type="submit" class="btn btn-icon btn-ghost" title="Logout" style="width:24px;height:24px;">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"/></svg>
            </button>
        </form>
    </div>
</aside>
