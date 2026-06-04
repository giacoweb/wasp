(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {
        var page = document.getElementById("expenses-page");
        if (!page) return;

        var ctx = page.dataset.contextPath || "";
        var walletSelect = document.getElementById("expense-wallet");
        var wallets = readWallets(walletSelect);
        var walletMap = {};
        wallets.forEach(function (wallet) {
            walletMap[wallet.id] = wallet;
        });

        var tbody = document.getElementById("expenses-body");
        var createBtn = document.getElementById("expense-create-btn");
        var searchInput = document.getElementById("expense-search");
        var filterTabs = document.querySelectorAll(".filter-tab");
        var alertBox = document.getElementById("expense-alert");
        var modal = document.getElementById("expense-modal");
        var form = document.getElementById("expense-form");
        var formError = document.getElementById("expense-form-error");
        var submitBtn = document.getElementById("expense-submit");
        var modalTitle = document.getElementById("expense-modal-title");

        if (!tbody || !wallets.length) return;

        var fields = {
            id: document.getElementById("expense-id"),
            label: document.getElementById("expense-label"),
            amount: document.getElementById("expense-amount"),
            nextDue: document.getElementById("expense-next-due"),
            periodicity: document.getElementById("expense-periodicity"),
            wallet: walletSelect
        };

        var allExpenses = [];
        var activeFilter = "ALL";

        function readWallets(select) {
            if (!select) return [];
            return Array.prototype.map.call(select.options, function (option) {
                return {
                    id: option.value,
                    name: option.dataset.walletName || option.textContent.trim(),
                    currency: option.dataset.walletCurrency || ""
                };
            });
        }

        function esc(value) {
            return String(value == null ? "" : value)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#39;");
        }

        function formatMoney(amount, currency) {
            var value = Number(amount || 0);
            return value.toLocaleString("en-US", {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }) + (currency ? " " + currency : "");
        }

        function formatDate(value) {
            if (!value) return "-";
            var parts = value.split("-");
            if (parts.length !== 3) return value;
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        }

        function periodicityLabel(value) {
            if (value === "MONTHLY") return "Monthly";
            if (value === "QUARTERLY") return "Quarterly";
            if (value === "YEARLY") return "Yearly";
            return value || "-";
        }

        function monthlyEquivalent(expense) {
            var amount = Number(expense.amount || 0);
            if (expense.periodicity === "QUARTERLY") return amount / 3;
            if (expense.periodicity === "YEARLY") return amount / 12;
            return amount;
        }

        function isDueSoon(expense) {
            if (!expense.nextDue || !expense.active) return false;
            var due = new Date(expense.nextDue + "T00:00:00");
            var today = new Date();
            today.setHours(0, 0, 0, 0);
            var soon = new Date(today);
            soon.setDate(soon.getDate() + 7);
            return due <= soon;
        }

        function showPageError(message) {
            if (!alertBox) return;
            alertBox.textContent = message;
            alertBox.hidden = false;
        }

        function clearPageError() {
            if (!alertBox) return;
            alertBox.textContent = "";
            alertBox.hidden = true;
        }

        function updateKpis() {
            var active = 0;
            var monthly = 0;
            var dueSoon = 0;

            allExpenses.forEach(function (expense) {
                if (!expense.active) return;
                active++;
                monthly += monthlyEquivalent(expense);
                if (isDueSoon(expense)) dueSoon++;
            });

            var firstCurrency = wallets[0] ? wallets[0].currency : "";
            setText("kpi-active-count", active);
            setText("kpi-monthly-total", formatMoney(monthly, firstCurrency));
            setText("kpi-due-count", dueSoon);
        }

        function setText(id, value) {
            var el = document.getElementById(id);
            if (el) el.textContent = value;
        }

        function visibleExpenses() {
            var query = searchInput ? searchInput.value.trim().toLowerCase() : "";
            return allExpenses.filter(function (expense) {
                if (activeFilter === "ACTIVE" && !expense.active) return false;
                if (activeFilter === "PAUSED" && expense.active) return false;
                if (!query) return true;
                var wallet = walletMap[expense.walletId];
                return expense.label.toLowerCase().indexOf(query) !== -1 ||
                    (wallet && wallet.name.toLowerCase().indexOf(query) !== -1);
            });
        }

        function renderRows() {
            var rows = visibleExpenses();
            if (!rows.length) {
                tbody.innerHTML = '<tr><td colspan="7" class="table-empty">No recurring expenses found.</td></tr>';
                return;
            }

            tbody.innerHTML = rows.map(function (expense) {
                var wallet = walletMap[expense.walletId] || {};
                var currency = wallet.currency || "";
                var statusClass = expense.active ? "badge-primary" : "badge-muted";
                var statusText = expense.active ? "Active" : "Paused";
                var dueClass = isDueSoon(expense) ? "expense-due-soon" : "";

                return "<tr>" +
                    "<td><strong>" + esc(expense.label) + "</strong></td>" +
                    "<td>" + esc(wallet.name || "-") + "</td>" +
                    "<td>" + esc(periodicityLabel(expense.periodicity)) + "</td>" +
                    "<td class='" + dueClass + "'>" + esc(formatDate(expense.nextDue)) + "</td>" +
                    "<td><span class='badge " + statusClass + "'>" + statusText + "</span></td>" +
                    "<td class='num amount-negative'>-" + esc(formatMoney(expense.amount, currency)) + "</td>" +
                    "<td class='actions'>" +
                        "<button type='button' class='btn btn-icon btn-ghost btn-sm js-edit-expense' title='Edit' data-id='" + esc(expense.id) + "'>" + editIcon() + "</button>" +
                        "<button type='button' class='btn btn-icon btn-ghost btn-sm js-toggle-expense' title='" + (expense.active ? "Pause" : "Activate") + "' data-id='" + esc(expense.id) + "'>" + toggleIcon(expense.active) + "</button>" +
                        "<button type='button' class='btn btn-icon btn-ghost btn-sm js-delete-expense' title='Delete' data-id='" + esc(expense.id) + "'>" + deleteIcon() + "</button>" +
                    "</td>" +
                "</tr>";
            }).join("");
        }

        function editIcon() {
            return "<svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' aria-hidden='true'><path d='M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7'/><path d='M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z'/></svg>";
        }

        function toggleIcon(active) {
            if (active) {
                return "<svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' aria-hidden='true'><rect x='6' y='4' width='4' height='16'/><rect x='14' y='4' width='4' height='16'/></svg>";
            }
            return "<svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' aria-hidden='true'><polygon points='5 3 19 12 5 21 5 3'/></svg>";
        }

        function deleteIcon() {
            return "<svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' class='icon--danger' aria-hidden='true'><polyline points='3 6 5 6 21 6'/><path d='M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6'/><path d='M10 11v6'/><path d='M14 11v6'/><path d='M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2'/></svg>";
        }

        function loadExpenses() {
            clearPageError();
            tbody.innerHTML = '<tr><td colspan="7" class="table-empty">Loading...</td></tr>';
            fetch(ctx + "/api/expenses", { credentials: "same-origin", headers: { "Accept": "application/json" } })
                .then(function (response) { return response.json(); })
                .then(function (data) {
                    if (!Array.isArray(data)) {
                        throw new Error(data.error || "Unable to load recurring expenses.");
                    }
                    allExpenses = data;
                    allExpenses.sort(function (a, b) {
                        return String(a.nextDue).localeCompare(String(b.nextDue));
                    });
                    updateKpis();
                    renderRows();
                })
                .catch(function (error) {
                    tbody.innerHTML = '<tr><td colspan="7" class="table-empty">Unable to load recurring expenses.</td></tr>';
                    showPageError(error.message || "Unable to load recurring expenses.");
                });
        }

        function openModal(expense) {
            clearFormErrors();
            form.reset();
            formError.hidden = true;
            fields.id.value = "";
            if (wallets[0]) fields.wallet.value = wallets[0].id;
            fields.periodicity.value = "MONTHLY";
            fields.nextDue.value = new Date().toISOString().slice(0, 10);

            if (expense) {
                modalTitle.textContent = "Edit recurring expense";
                submitBtn.textContent = "Save changes";
                fields.id.value = expense.id;
                fields.label.value = expense.label;
                fields.amount.value = expense.amount;
                fields.nextDue.value = expense.nextDue;
                fields.periodicity.value = expense.periodicity;
                fields.wallet.value = expense.walletId;
                fields.wallet.disabled = true;
            } else {
                modalTitle.textContent = "Create recurring expense";
                submitBtn.textContent = "Create expense";
                fields.wallet.disabled = false;
            }

            modal.hidden = false;
            fields.label.focus();
        }

        function closeModal() {
            modal.hidden = true;
            fields.wallet.disabled = false;
        }

        function clearFormErrors() {
            Array.prototype.forEach.call(form.querySelectorAll(".form-field"), function (field) {
                field.classList.remove("is-invalid");
                var error = field.querySelector(".form-error");
                if (error) error.textContent = "";
            });
        }

        function setFieldError(input, message) {
            var field = input.closest(".form-field");
            if (!field) return;
            field.classList.add("is-invalid");
            var error = field.querySelector(".form-error");
            if (error) error.textContent = message;
        }

        function validateForm() {
            clearFormErrors();
            var firstInvalid = null;
            Array.prototype.forEach.call(form.elements, function (input) {
                if (!input.willValidate || input.disabled) return;
                if (!input.checkValidity()) {
                    setFieldError(input, input.validationMessage);
                    if (!firstInvalid) firstInvalid = input;
                }
            });
            if (firstInvalid) firstInvalid.focus();
            return firstInvalid === null;
        }

        function submitForm(event) {
            event.preventDefault();
            formError.hidden = true;
            if (!validateForm()) return;

            var id = fields.id.value;
            var body = {
                label: fields.label.value.trim(),
                amount: fields.amount.value,
                periodicity: fields.periodicity.value,
                nextDue: fields.nextDue.value
            };
            if (!id) body.walletId = fields.wallet.value;

            fetch(id ? ctx + "/api/expenses/" + id : ctx + "/api/expenses", {
                method: id ? "PUT" : "POST",
                credentials: "same-origin",
                headers: { "Content-Type": "application/json", "Accept": "application/json" },
                body: JSON.stringify(body)
            })
                .then(function (response) { return response.json(); })
                .then(function (data) {
                    if (data.error) {
                        formError.textContent = data.error;
                        formError.hidden = false;
                        return;
                    }
                    closeModal();
                    loadExpenses();
                })
                .catch(function () {
                    formError.textContent = "An unexpected error occurred.";
                    formError.hidden = false;
                });
        }

        function findExpense(id) {
            return allExpenses.find(function (expense) {
                return expense.id === id;
            });
        }

        function toggleExpense(id) {
            fetch(ctx + "/api/expenses/" + id + "/toggle", {
                method: "POST",
                credentials: "same-origin",
                headers: { "Accept": "application/json" }
            })
                .then(function (response) { return response.json(); })
                .then(function (data) {
                    if (data.error) {
                        showPageError(data.error);
                        return;
                    }
                    loadExpenses();
                })
                .catch(function () {
                    showPageError("Unable to update recurring expense.");
                });
        }

        function deleteExpense(id) {
            var expense = findExpense(id);
            var label = expense ? expense.label : "this recurring expense";
            if (!window.confirm("Delete \"" + label + "\"? This cannot be undone.")) return;

            fetch(ctx + "/api/expenses/" + id, {
                method: "DELETE",
                credentials: "same-origin",
                headers: { "Accept": "application/json" }
            })
                .then(function (response) {
                    if (response.status === 204) {
                        loadExpenses();
                        return;
                    }
                    return response.json().then(function (data) {
                        showPageError(data.error || "Unable to delete recurring expense.");
                    });
                })
                .catch(function () {
                    showPageError("Unable to delete recurring expense.");
                });
        }

        if (createBtn) {
            createBtn.addEventListener("click", function () {
                openModal(null);
            });
        }

        filterTabs.forEach(function (tab) {
            tab.addEventListener("click", function () {
                filterTabs.forEach(function (item) {
                    item.classList.remove("active");
                    item.setAttribute("aria-pressed", "false");
                });
                tab.classList.add("active");
                tab.setAttribute("aria-pressed", "true");
                activeFilter = tab.dataset.filter || "ALL";
                renderRows();
            });
        });

        if (searchInput) searchInput.addEventListener("input", renderRows);
        form.addEventListener("submit", submitForm);

        document.getElementById("expense-modal-close").addEventListener("click", closeModal);
        document.getElementById("expense-modal-cancel").addEventListener("click", closeModal);
        modal.addEventListener("click", function (event) {
            if (event.target === modal) closeModal();
        });

        document.addEventListener("keydown", function (event) {
            if (event.key === "Escape" && !modal.hidden) closeModal();
        });

        document.addEventListener("click", function (event) {
            var editBtn = event.target.closest(".js-edit-expense");
            if (editBtn) {
                var expense = findExpense(editBtn.dataset.id);
                if (expense) openModal(expense);
                return;
            }

            var toggleBtn = event.target.closest(".js-toggle-expense");
            if (toggleBtn) {
                toggleExpense(toggleBtn.dataset.id);
                return;
            }

            var deleteBtn = event.target.closest(".js-delete-expense");
            if (deleteBtn) {
                deleteExpense(deleteBtn.dataset.id);
            }
        });

        document.addEventListener("input", function (event) {
            if (event.target.matches && event.target.matches(".form-input, .form-select")) {
                var field = event.target.closest(".form-field");
                if (field) {
                    field.classList.remove("is-invalid");
                    var error = field.querySelector(".form-error");
                    if (error) error.textContent = "";
                }
            }
        });

        loadExpenses();
    });
})();
