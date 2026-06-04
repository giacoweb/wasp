<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Log in &middot; FinTrack</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap">

    <link rel="stylesheet" href="${ctx}/static/css/tokens.css">
    <link rel="stylesheet" href="${ctx}/static/css/base.css">
    <link rel="stylesheet" href="${ctx}/static/css/components.css">
    <link rel="stylesheet" href="${ctx}/static/css/forms.css">
</head>
<body>

<main style="min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 2rem;">

    <div class="card" style="width: 100%; max-width: 420px;">

        <header style="margin-bottom: 1.5rem;">
            <h1 style="font-size: 2rem; margin: 0 0 0.5rem;">Log in</h1>
            <p class="muted" style="margin: 0;">Welcome back to FinTrack.</p>
        </header>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error" role="alert" style="margin-bottom: 1rem;">
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <form id="login-form" class="form"
              action="${ctx}/auth/login" method="post" novalidate>

            <div class="form-field">
                <label class="form-label form-required" for="login-email">Email</label>
                <input class="form-input" id="login-email" type="email" name="email"
                       required
                       value="<c:out value='${email}'/>"
                       placeholder="you@email.com"
                       autocomplete="email">
                <span class="form-error"></span>
            </div>

            <div class="form-field">
                <label class="form-label form-required" for="login-password">Password</label>
                <input class="form-input" id="login-password" type="password" name="password"
                       required
                       autocomplete="current-password">
                <span class="form-error"></span>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Log in</button>
        </form>

        <p class="muted" style="text-align: center; margin: 1.5rem 0 0;">
            No account yet?
            <a href="${ctx}/register">Create one</a>
        </p>
    </div>

</main>

<script src="${ctx}/static/js/auth.js"></script>

</body>
</html>
