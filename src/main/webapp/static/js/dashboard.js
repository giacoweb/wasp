(function () {
    "use strict";

    const ctx     = window.CTX_PATH  || "";
    const wallets = window.WALLET_IDS || [];
    const tbody   = document.getElementById("recent-tx-body");
    const LIMIT   = 5;

    if (!tbody || !wallets.length) {
        if (tbody) tbody.innerHTML = '<tr><td colspan="4" class="table-empty">No transactions yet.</td></tr>';
        return;
    }

    function esc(str) {
        return String(str)
            .replace(/&/g, "&amp;").replace(/</g, "&lt;")
            .replace(/>/g, "&gt;").replace(/"/g, "&quot;");
    }

    function fmt(amount) {
        return parseFloat(amount).toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    const walletMap = {};
    wallets.forEach(function (w) { walletMap[w.id] = w.name; });

    Promise.all(wallets.map(function (w) {
        return fetch(ctx + "/api/transactions?walletId=" + w.id, { credentials: "same-origin" })
            .then(function (r) { return r.json(); })
            .then(function (data) { return Array.isArray(data) ? data : []; })
            .catch(function () { return []; });
    }))
    .then(function (results) {
        const all = [].concat.apply([], results);
        all.sort(function (a, b) { return b.date.localeCompare(a.date); });
        const recent = all.slice(0, LIMIT);

        if (!recent.length) {
            tbody.innerHTML = '<tr><td colspan="4" class="table-empty">No transactions yet.</td></tr>';
            return;
        }

        tbody.innerHTML = recent.map(function (t) {
            const isIncome   = t.type === "INCOME";
            const amountHtml = isIncome
                ? '<span style="color:var(--color-positive);font-weight:500;">+' + fmt(t.amount) + '</span>'
                : '<span style="color:var(--color-negative);font-weight:500;">−' + fmt(t.amount) + '</span>';
            const walletName = walletMap[t.walletId] ? esc(walletMap[t.walletId]) : "—";
            return "<tr>" +
                "<td>" + esc(t.date) + "</td>" +
                "<td>" + esc(t.category) + "</td>" +
                "<td>" + walletName + "</td>" +
                "<td class='num'>" + amountHtml + "</td>" +
            "</tr>";
        }).join("");
    })
    .catch(function () {
        tbody.innerHTML = '<tr><td colspan="4" class="table-empty">Failed to load transactions.</td></tr>';
    });
})();
