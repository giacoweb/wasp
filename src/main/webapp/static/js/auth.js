/**
 * Client-side validation for the login and register forms.
 * Uses the HTML5 constraint API for built-in checks (required, type=email,
 * minlength) and adds a custom password-strength rule on register.
 *
 * Server-side validation is still authoritative.
 */
(function () {
    "use strict";

    function field(input) {
        return input.closest(".form-field");
    }

    function setFieldError(input, message) {
        const f = field(input);
        if (!f) return;
        f.classList.add("is-invalid");
        const err = f.querySelector(".form-error");
        if (err) err.textContent = message;
    }

    function clearFieldError(input) {
        const f = field(input);
        if (!f) return;
        f.classList.remove("is-invalid");
        const err = f.querySelector(".form-error");
        if (err) err.textContent = "";
    }

    /**
     * Validates every constraint-API-aware input on `form`. Optionally runs
     * `customCheck(form)`, which may return `{ input, message }` on failure.
     */
    function validateForm(form, customCheck) {
        let firstInvalid = null;

        Array.from(form.elements).forEach((el) => {
            if (!el.willValidate) return;
            clearFieldError(el);
            if (!el.checkValidity()) {
                setFieldError(el, el.validationMessage);
                if (!firstInvalid) firstInvalid = el;
            }
        });

        if (customCheck) {
            const result = customCheck(form);
            if (result) {
                setFieldError(result.input, result.message);
                if (!firstInvalid) firstInvalid = result.input;
            }
        }

        if (firstInvalid) firstInvalid.focus();
        return firstInvalid === null;
    }

    // Live: clear an inline error as soon as the user starts fixing the field.
    document.addEventListener("input", function (e) {
        if (e.target.matches(".form-input")) clearFieldError(e.target);
    });

    document.addEventListener("DOMContentLoaded", function () {

        const loginForm = document.getElementById("login-form");
        if (loginForm) {
            loginForm.addEventListener("submit", function (e) {
                if (!validateForm(loginForm)) e.preventDefault();
            });
        }

        const registerForm = document.getElementById("register-form");
        if (registerForm) {
            registerForm.addEventListener("submit", function (e) {
                const valid = validateForm(registerForm, function (form) {
                    const password = form.password.value;
                    if (password.length < 8) {
                        return { input: form.password, message: "Password must be at least 8 characters." };
                    }
                    if (!/\d/.test(password)) {
                        return { input: form.password, message: "Password must contain at least one digit." };
                    }
                    if (!/[^A-Za-z0-9]/.test(password)) {
                        return { input: form.password, message: "Password must contain at least one special character." };
                    }
                    return null;
                });
                if (!valid) e.preventDefault();
            });
        }
    });
})();
