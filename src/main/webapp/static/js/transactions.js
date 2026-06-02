(function () {
    "use strict";

    const ctx     = window.CTX_PATH   || "";
    const wallets = window.WALLET_IDS || [];

    const tbody      = document.getElementById("transactions-body");
    const btnAdd     = document.getElementById("btn-add");
    const searchInput = document.getElementById("search-input");
    const filterTabs = document.querySelectorAll(".filter-tab");
    const modalOverlay = document.getElementById("modal-overlay");
    const form         = document.getElementById("transaction-form");
    const formId       = document.getElementById("form-id");
    const formType     = document.getElementById("form-type");
    const formAmount   = document.getElementById("form-amount");
    const formDate     = document.getElementById("form-date");
    const formCategory = document.getElementById("form-category");
    const formWallet   = document.getElementById("form-wallet");
    const formNote     = document.getElementById("form-note");
    const formError    = document.getElementById("form-error");
    const formSubmit   = document.getElementById("form-submit");

    if (!tbody || !wallets.length) return;

    const walletMap = {};
    const walletCurrencyMap = {};
    wallets.forEach(function (w) {
        walletMap[w.id] = w.name;
        walletCurrencyMap[w.id] = w.currency || "";
    });

    let allTransactions = [];
    let activeFilter    = "ALL";

    /* ── helpers ─────────────────────────────────────────── */

    function esc(str) {
        return String(str)
            .replace(/&/g, "&amp;").replace(/</g, "&lt;")
            .replace(/>/g, "&gt;").replace(/"/g, "&quot;");
    }

    function fmt(amount) {
        return parseFloat(amount).toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    /* ── KPI update (one group of cards per currency) ────────── */

    var SVG_INCOME  = "<svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><line x1='7' y1='17' x2='17' y2='7'/><polyline points='7 7 17 7 17 17'/></svg>";
    var SVG_EXPENSE = "<svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><line x1='7' y1='7' x2='17' y2='17'/><polyline points='17 7 17 17 7 17'/></svg>";
    var SVG_FLOW    = "<svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><polyline points='22 7 13.5 15.5 8.5 10.5 2 17'/><polyline points='16 7 22 7 22 13'/></svg>";

    function updateKpis(transactions) {
        var byCurrency = {};
        wallets.forEach(function (w) {
            var cur = w.currency || "?";
            if (!byCurrency[cur]) byCurrency[cur] = { income: 0, expenses: 0 };
        });
        transactions.forEach(function (t) {
            var cur = walletCurrencyMap[t.walletId] || "?";
            if (!byCurrency[cur]) byCurrency[cur] = { income: 0, expenses: 0 };
            var v = parseFloat(t.amount);
            if (t.type === "INCOME") byCurrency[cur].income += v;
            else byCurrency[cur].expenses += v;
        });

        var currencies = Object.keys(byCurrency).sort();
        var isMulti = currencies.length > 1;
        var section = document.getElementById("kpi-section");

        section.innerHTML = currencies.map(function (cur, idx) {
            var d = byCurrency[cur];
            var flow = d.income - d.expenses;
            var flowClass = "kpi-amount " + (flow >= 0 ? "kpi-amount--flow-pos" : "kpi-amount--flow-neg");
            var flowSign  = flow >= 0 ? "+" : "−";
            var gapStyle  = isMulti && idx < currencies.length - 1 ? "margin-bottom:var(--space-4);" : "";
            var header    = isMulti
                ? "<div style='font-weight:600;font-size:var(--font-size-sm);margin-bottom:var(--space-2);'>" + esc(cur) + "</div>"
                : "";
            return header +
                "<div class='grid grid--3' style='" + gapStyle + "'>" +
                    "<div class='card'>" +
                        "<div class='kpi-icon kpi-icon--income'>" + SVG_INCOME + "</div>" +
                        "<div class='card-subtitle'>Total Income</div>" +
                        "<div class='kpi-amount kpi-amount--income'>+" + fmt(d.income) + " " + esc(cur) + "</div>" +
                        "<p class='muted' style='margin:0;font-size:var(--font-size-xs);'>Across all wallets</p>" +
                    "</div>" +
                    "<div class='card'>" +
                        "<div class='kpi-icon kpi-icon--expense'>" + SVG_EXPENSE + "</div>" +
                        "<div class='card-subtitle'>Total Expenses</div>" +
                        "<div class='kpi-amount kpi-amount--expense'>−" + fmt(d.expenses) + " " + esc(cur) + "</div>" +
                        "<p class='muted' style='margin:0;font-size:var(--font-size-xs);'>Across all wallets</p>" +
                    "</div>" +
                    "<div class='card'>" +
                        "<div class='kpi-icon kpi-icon--flow'>" + SVG_FLOW + "</div>" +
                        "<div class='card-subtitle'>Net Flow</div>" +
                        "<div class='" + flowClass + "'>" + flowSign + fmt(Math.abs(flow)) + " " + esc(cur) + "</div>" +
                        "<p class='muted' style='margin:0;font-size:var(--font-size-xs);'>" + (flow >= 0 ? "Healthy positive balance" : "Negative balance") + "</p>" +
                    "</div>" +
                "</div>";
        }).join("");
    }

    /* ── table render ─────────────────────────────────────── */

    function visibleTransactions() {
        const query = searchInput ? searchInput.value.toLowerCase() : "";
        return allTransactions.filter(function (t) {
            if (activeFilter !== "ALL" && t.type !== activeFilter) return false;
            if (query && !t.category.toLowerCase().includes(query)) return false;
            return true;
        });
    }

    function renderRows() {
        const rows = visibleTransactions();
        if (!rows.length) {
            tbody.innerHTML = '<tr><td colspan="6" class="table-empty">No transactions found.</td></tr>';
            return;
        }
        tbody.innerHTML = rows.map(function (t) {
            const isIncome   = t.type === "INCOME";
            const amountHtml = isIncome
                ? '<span style="color:var(--color-positive);font-weight:500;">+' + fmt(t.amount) + '</span>'
                : '<span style="color:var(--color-negative);font-weight:500;">−' + fmt(t.amount) + '</span>';
            const badge = isIncome
                ? '<span class="badge badge-income">Income</span>'
                : '<span class="badge badge-expense">Expense</span>';
            const walletName = walletMap[t.walletId] ? esc(walletMap[t.walletId]) : "—";
            const safeT = JSON.stringify(t)
                .replace(/</g, "\\u003c").replace(/>/g, "\\u003e").replace(/&/g, "\\u0026");
            return "<tr>" +
                "<td>" + esc(t.date) + "</td>" +
                "<td>" + esc(t.category) + "</td>" +
                "<td>" + badge + "</td>" +
                "<td>" + walletName + "</td>" +
                "<td class='num'>" + amountHtml + "</td>" +
                "<td class='actions'>" +
                    "<button class='btn btn-icon btn-ghost btn-sm' title='Edit' onclick='Transactions.edit(" + safeT + ")'>" +
                        "<svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><path d='M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7'/><path d='M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z'/></svg>" +
                    "</button>" +
                    "<button class='btn btn-icon btn-ghost btn-sm' title='Delete' onclick='Transactions.delete(\"" + esc(t.id) + "\")'>" +
                        "<svg viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' style='color:var(--color-negative)'><polyline points='3 6 5 6 21 6'/><path d='M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6'/><path d='M10 11v6'/><path d='M14 11v6'/><path d='M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2'/></svg>" +
                    "</button>" +
                "</td>" +
            "</tr>";
        }).join("");
    }

    /* ── load ─────────────────────────────────────────────── */

    function loadTransactions() {
        tbody.innerHTML = '<tr><td colspan="6" class="table-empty">Loading...</td></tr>';
        Promise.all(wallets.map(function (w) {
            return fetch(ctx + "/api/transactions?walletId=" + w.id, { credentials: "same-origin" })
                .then(function (r) { return r.json(); })
                .then(function (data) { return Array.isArray(data) ? data : []; })
                .catch(function () { return []; });
        }))
        .then(function (results) {
            allTransactions = [].concat.apply([], results);
            allTransactions.sort(function (a, b) { return b.date.localeCompare(a.date); });
            updateKpis(allTransactions);
            renderRows();
        });
    }

    /* ── modal ────────────────────────────────────────────── */

    function setType(value) {
        formType.value = value;
        document.querySelectorAll(".type-toggle__btn").forEach(function (btn) {
            var isActive = btn.dataset.value === value;
            btn.classList.toggle("active", isActive);
            btn.setAttribute("aria-pressed", isActive ? "true" : "false");
        });
    }

    function openModal(transaction) {
        formError.hidden = true;
        form.reset();
        if (transaction) {
            document.getElementById("modal-title").textContent = "Edit transaction";
            formSubmit.textContent = "Save changes";
            formId.value       = transaction.id;
            formAmount.value   = transaction.amount;
            formDate.value     = transaction.date;
            formCategory.value = transaction.category;
            formNote.value     = transaction.note || "";
            if (formWallet) formWallet.value = transaction.walletId;
            setType(transaction.type);
        } else {
            document.getElementById("modal-title").textContent = "Create transaction";
            formSubmit.textContent = "Create transaction";
            formId.value   = "";
            formDate.value = new Date().toISOString().slice(0, 10);
            setType("INCOME");
        }
        modalOverlay.hidden = false;
    }

    function closeModal() { modalOverlay.hidden = true; }

    /* ── public API ───────────────────────────────────────── */

    window.Transactions = {
        edit: function (t) { openModal(t); },
        delete: function (id) {
            if (!confirm("Delete this transaction?")) return;
            fetch(ctx + "/api/transactions/" + id, { method: "DELETE", credentials: "same-origin" })
                .then(function (r) {
                    if (r.status === 204) { loadTransactions(); }
                    else { r.json().then(function (d) { alert(d.error || "Failed to delete."); }); }
                })
                .catch(function () { alert("Failed to delete transaction."); });
        }
    };

    /* ── events ───────────────────────────────────────────── */

    document.querySelectorAll(".type-toggle__btn").forEach(function (btn) {
        btn.addEventListener("click", function () { setType(btn.dataset.value); });
    });

    filterTabs.forEach(function (tab) {
        tab.addEventListener("click", function () {
            filterTabs.forEach(function (t) {
                t.classList.remove("active");
                t.setAttribute("aria-pressed", "false");
            });
            tab.classList.add("active");
            tab.setAttribute("aria-pressed", "true");
            activeFilter = tab.dataset.filter;
            renderRows();
        });
    });

    if (searchInput) searchInput.addEventListener("input", renderRows);

    form.addEventListener("submit", function (e) {
        e.preventDefault();
        formError.hidden = true;

        const id        = formId.value;
        const isExpense = formType.value === "EXPENSE";
        const signed    = (isExpense ? -1 : 1) * Math.abs(parseFloat(formAmount.value));
        const walletId  = formWallet ? formWallet.value : (wallets[0] ? wallets[0].id : "");

        const body = {
            walletId: walletId,
            amount:   signed.toFixed(2),
            category: formCategory.value.trim(),
            date:     formDate.value,
            note:     formNote.value.trim() || null
        };

        fetch(id ? ctx + "/api/transactions/" + id : ctx + "/api/transactions", {
            method:      id ? "PUT" : "POST",
            credentials: "same-origin",
            headers:     { "Content-Type": "application/json" },
            body:        JSON.stringify(body)
        })
        .then(function (r) { return r.json(); })
        .then(function (data) {
            if (data.error) {
                formError.textContent   = data.error;
                formError.hidden = false;
                return;
            }
            closeModal();
            loadTransactions();
        })
        .catch(function () {
            formError.textContent = "An unexpected error occurred.";
            formError.hidden = false;
        });
    });

    if (btnAdd) btnAdd.addEventListener("click", function () { openModal(null); });
    document.getElementById("modal-close").addEventListener("click", closeModal);
    document.getElementById("modal-cancel").addEventListener("click", closeModal);
    modalOverlay.addEventListener("click", function (e) { if (e.target === modalOverlay) closeModal(); });

    loadTransactions();
})();
