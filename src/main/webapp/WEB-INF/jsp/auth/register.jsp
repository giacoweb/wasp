<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/common/_favicon.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create account &middot; FinTrack</title>

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

<main class="auth-page">

    <div class="card auth-card auth-card--register">

        <header class="auth-header">
            <h1 class="auth-title">Create your account</h1>
            <p class="muted auth-subtitle">Start tracking in under a minute.</p>
        </header>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error auth-alert" role="alert">
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <form id="register-form" class="form"
              action="${ctx}/auth/register" method="post" novalidate>

            <div class="form-grid-2">
                <div class="form-field">
                    <label class="form-label form-required" for="register-name">Name</label>
                    <input class="form-input" id="register-name" type="text" name="name"
                           required maxlength="100"
                           value="<c:out value='${name}'/>"
                           autocomplete="given-name">
                    <span class="form-error"></span>
                </div>
                <div class="form-field">
                    <label class="form-label form-required" for="register-surname">Surname</label>
                    <input class="form-input" id="register-surname" type="text" name="surname"
                           required maxlength="100"
                           value="<c:out value='${surname}'/>"
                           autocomplete="family-name">
                    <span class="form-error"></span>
                </div>
            </div>

            <div class="form-field">
                <label class="form-label form-required" for="register-email">Email</label>
                <input class="form-input" id="register-email" type="email" name="email"
                       required maxlength="255"
                       value="<c:out value='${email}'/>"
                       placeholder="you@email.com"
                       autocomplete="email">
                <span class="form-error"></span>
            </div>

            <div class="form-field">
                <label class="form-label form-required" for="register-password">Password</label>
                <input class="form-input" id="register-password" type="password" name="password"
                       required minlength="8"
                       autocomplete="new-password">
                <span class="form-help">
                    At least 8 characters, including one digit and one special character.
                </span>
                <span class="form-error"></span>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Create account</button>
        </form>

        <p class="muted auth-switch">
            Already have an account?
            <a href="${ctx}/login">Log in</a>
        </p>
    </div>

</main>

<script src="${ctx}/static/js/auth.js"></script>

</body>
</html>
