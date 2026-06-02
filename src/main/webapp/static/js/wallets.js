(function () {
    "use strict";

    /* ── type toggle helper ──────────────────────────────────── */

    function initTypeToggle(containerSelector, hiddenInputId) {
        var container = document.querySelector(containerSelector);
        if (!container) return;
        var hidden = document.getElementById(hiddenInputId);
        container.querySelectorAll(".wallet-type-btn").forEach(function (btn) {
            btn.addEventListener("click", function () {
                container.querySelectorAll(".wallet-type-btn").forEach(function (b) {
                    b.classList.remove("active");
                    b.setAttribute("aria-pressed", "false");
                });
                btn.classList.add("active");
                btn.setAttribute("aria-pressed", "true");
                hidden.value = btn.dataset.value;
            });
        });
    }

    function setActiveType(containerSelector, hiddenInputId, value) {
        var container = document.querySelector(containerSelector);
        if (!container) return;
        var hidden = document.getElementById(hiddenInputId);
        container.querySelectorAll(".wallet-type-btn").forEach(function (btn) {
            var isActive = btn.dataset.value === value;
            btn.classList.toggle("active", isActive);
            btn.setAttribute("aria-pressed", isActive ? "true" : "false");
        });
        hidden.value = value;
    }

    /* ── modal helpers ───────────────────────────────────────── */

    function openOverlay(id) {
        var el = document.getElementById(id);
        if (el) el.hidden = false;
    }

    function closeOverlay(id) {
        var el = document.getElementById(id);
        if (el) el.hidden = true;
    }

    /* ── create modal ────────────────────────────────────────── */

    initTypeToggle("#create-modal-overlay .wallet-type-toggle", "create-type");

    document.getElementById("btn-add-wallet").addEventListener("click", function () {
        setActiveType("#create-modal-overlay .wallet-type-toggle", "create-type", "CASH");
        openOverlay("create-modal-overlay");
    });

    document.getElementById("create-modal-close").addEventListener("click", function () {
        closeOverlay("create-modal-overlay");
    });
    document.getElementById("create-modal-cancel").addEventListener("click", function () {
        closeOverlay("create-modal-overlay");
    });
    document.getElementById("create-modal-overlay").addEventListener("click", function (e) {
        if (e.target === this) closeOverlay("create-modal-overlay");
    });

    /* ── edit modal ──────────────────────────────────────────── */

    initTypeToggle("#edit-modal-overlay .wallet-type-toggle", "edit-type");

    document.getElementById("edit-modal-close").addEventListener("click", function () {
        closeOverlay("edit-modal-overlay");
    });
    document.getElementById("edit-modal-cancel").addEventListener("click", function () {
        closeOverlay("edit-modal-overlay");
    });
    document.getElementById("edit-modal-overlay").addEventListener("click", function (e) {
        if (e.target === this) closeOverlay("edit-modal-overlay");
    });

    function openEdit(id, name, type, currency) {
        document.getElementById("edit-wallet-id").value = id;
        document.getElementById("edit-name").value      = name;
        document.getElementById("edit-currency").value  = currency;
        setActiveType("#edit-modal-overlay .wallet-type-toggle", "edit-type", type);
        openOverlay("edit-modal-overlay");
    }

    document.addEventListener("click", function (e) {
        var btn = e.target.closest(".js-edit-wallet");
        if (!btn) return;
        openEdit(btn.dataset.id, btn.dataset.name, btn.dataset.type, btn.dataset.currency);
    });

    document.addEventListener("submit", function (e) {
        var form = e.target.closest(".js-delete-wallet");
        if (!form) return;
        if (!confirm("Delete wallet '" + form.dataset.name + "'? This cannot be undone.")) {
            e.preventDefault();
        }
    });

    /* ── search + filter ─────────────────────────────────────── */

    var searchInput  = document.getElementById("wallet-search");
    var filterTabs   = document.querySelectorAll(".filter-tab");
    var rows         = document.querySelectorAll("#wallets-body tr[data-type]");
    var activeFilter = "ALL";

    function applyFilter() {
        var query = searchInput ? searchInput.value.toLowerCase() : "";
        rows.forEach(function (row) {
            var matchType = activeFilter === "ALL" || row.dataset.type === activeFilter;
            var matchName = !query || row.dataset.name.includes(query);
            row.style.display = matchType && matchName ? "" : "none";
        });
    }

    filterTabs.forEach(function (tab) {
        tab.addEventListener("click", function () {
            filterTabs.forEach(function (t) {
                t.classList.remove("active");
                t.setAttribute("aria-pressed", "false");
            });
            tab.classList.add("active");
            tab.setAttribute("aria-pressed", "true");
            activeFilter = tab.dataset.filter;
            applyFilter();
        });
    });

    if (searchInput) searchInput.addEventListener("input", applyFilter);

})();
