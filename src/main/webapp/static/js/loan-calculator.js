(function () {
    "use strict";

    var MONTHS = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
    var lastSchedule = [];

    document.addEventListener("DOMContentLoaded", function () {
        var now = new Date();
        var startMonth = document.getElementById("startMonth");
        var startYear = document.getElementById("startYear");
        if (startMonth) startMonth.value = String(now.getMonth() + 1);
        if (startYear) startYear.value = String(now.getFullYear());
        document.getElementById("btn-calculate").addEventListener("click", onCalculate);
        document.getElementById("btn-reset").addEventListener("click", onReset);
        document.getElementById("btn-export-csv").addEventListener("click", onExportCsv);
    });

    /* ── Validation ───────────────────────────────────────── */

    function getNum(id) { return parseFloat(document.getElementById(id).value); }
    function getInt(id) { return parseInt(document.getElementById(id).value, 10); }

    function setError(fieldId, errId, msg) {
        var field = document.getElementById(fieldId);
        var err   = document.getElementById(errId);
        if (msg) {
            field.classList.add("is-invalid");
            err.textContent = msg;
        } else {
            field.classList.remove("is-invalid");
            err.textContent = "";
        }
    }

    function validate() {
        var ok = true;
        var principal = getNum("principal");
        var rate      = getNum("annualRate");
        var months    = getInt("months");

        if (!principal || principal <= 0) {
            setError("field-principal", "err-principal", "Enter a principal greater than 0."); ok = false;
        } else { setError("field-principal", "err-principal", ""); }

        if (isNaN(rate) || rate < 0 || rate > 100) {
            setError("field-rate", "err-rate", "Rate must be between 0 and 100."); ok = false;
        } else { setError("field-rate", "err-rate", ""); }

        if (!months || months <= 0) {
            setError("field-months", "err-months", "Term must be at least 1 month."); ok = false;
        } else { setError("field-months", "err-months", ""); }

        return ok;
    }

    /* ── Calculation ──────────────────────────────────────── */

    function calculateLoan(principal, annualRate, months) {
        var schedule = [];
        if (annualRate === 0) {
            var payment = round2(principal / months);
            var balance = principal;
            for (var i = 1; i <= months; i++) {
                balance = Math.max(0, round2(balance - payment));
                schedule.push({ month: i, payment: payment, principal: payment, interest: 0, balance: balance });
            }
        } else {
            var monthlyRate = annualRate / 12 / 100;
            var mp = principal * (monthlyRate * Math.pow(1 + monthlyRate, months)) /
                     (Math.pow(1 + monthlyRate, months) - 1);
            var bal = principal;
            for (var j = 1; j <= months; j++) {
                var interest  = round2(bal * monthlyRate);
                var principal_part = round2(mp - interest);
                bal = Math.max(0, round2(bal - principal_part));
                schedule.push({ month: j, payment: round2(mp), principal: principal_part,
                                 interest: interest, balance: bal });
            }
        }
        return schedule;
    }

    function round2(n) { return Math.round(n * 100) / 100; }

    function fmt(n) {
        return "€" + Math.abs(n).toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    /* ── Render ───────────────────────────────────────────── */

    function onCalculate() {
        if (!validate()) return;

        var principal = getNum("principal");
        var rate      = getNum("annualRate");
        var months    = getInt("months");
        var startMonth = getInt("startMonth");
        var startYear  = getInt("startYear");

        var schedule = calculateLoan(principal, rate, months);
        lastSchedule = schedule;

        var monthlyPayment = schedule[0].payment;
        var totalPaid      = round2(monthlyPayment * months);
        var totalInterest  = round2(totalPaid - principal);

        renderSummary(principal, rate, months, monthlyPayment, totalInterest, totalPaid);
        renderTable(schedule, startMonth, startYear);

        document.getElementById("schedule-section").hidden = false;

        // re-init lucide for any newly added SVG icons inside rendered HTML
        if (window.lucide) window.lucide.createIcons();
    }

    function renderSummary(principal, rate, months, monthly, totalInterest, totalPaid) {
        var caption = "Estimated from €" + principal.toLocaleString("en-US") +
                      " principal, " + rate + "% APR, and " + months + " months.";

        var what = months <= 24
            ? "With a short term, more of each payment goes to principal from the start."
            : "Most of the first payments go partly toward interest, then gradually shift " +
              "toward principal as the remaining balance decreases.";

        var html =
            '<div class="card card--padded">' +
              '<div class="card-title">Payment summary</div>' +
              '<div class="card-subtitle loan-summary-caption">' + escHtml(caption) + '</div>' +
              '<div class="summary-kpis">' +

                // Monthly payment
                '<div class="summary-kpi">' +
                  '<div class="summary-kpi__header">' +
                    '<div class="summary-kpi__icon summary-kpi__icon--teal">' +
                      '<i data-lucide="refresh-cw"></i>' +
                    '</div>' +
                    '<div class="summary-kpi__label">Monthly payment</div>' +
                  '</div>' +
                  '<div class="summary-kpi__value summary-kpi__value--teal">' + fmt(monthly) + '</div>' +
                  '<div class="summary-kpi__sub">Over ' + months + ' months</div>' +
                '</div>' +

                // Total interest
                '<div class="summary-kpi">' +
                  '<div class="summary-kpi__header">' +
                    '<div class="summary-kpi__icon summary-kpi__icon--amber">' +
                      '<i data-lucide="percent"></i>' +
                    '</div>' +
                    '<div class="summary-kpi__label">Total interest</div>' +
                  '</div>' +
                  '<div class="summary-kpi__value summary-kpi__value--amber">' + fmt(totalInterest) + '</div>' +
                  '<div class="summary-kpi__sub">' + rate + '% APR</div>' +
                '</div>' +

                // Total paid
                '<div class="summary-kpi">' +
                  '<div class="summary-kpi__header">' +
                    '<div class="summary-kpi__icon summary-kpi__icon--green">' +
                      '<i data-lucide="circle"></i>' +
                    '</div>' +
                    '<div class="summary-kpi__label">Total paid</div>' +
                  '</div>' +
                  '<div class="summary-kpi__value summary-kpi__value--green">' + fmt(totalPaid) + '</div>' +
                  '<div class="summary-kpi__sub">Principal + interest</div>' +
                '</div>' +

              '</div>' + // /.summary-kpis

              '<hr class="loan-summary-divider">' +
              '<h4 class="loan-summary-heading">What this means</h4>' +
              '<p class="muted loan-summary-copy">' + what + '</p>' +
            '</div>';

        document.getElementById("results-panel").innerHTML = html;
    }

    function renderTable(schedule, startMonth, startYear) {
        var tbody = document.getElementById("schedule-body");
        var rows = [];
        var m = startMonth - 1; // 0-indexed
        var y = startYear;

        for (var i = 0; i < schedule.length; i++) {
            var row = schedule[i];
            var label = MONTHS[m] + " " + y;
            m++;
            if (m === 12) { m = 0; y++; }

            rows.push(
                '<tr>' +
                  '<td>#' + pad2(row.month) + ' &nbsp;<span class="muted loan-schedule-date">' + label + '</span></td>' +
                  '<td class="num">' + fmt(row.payment) + '</td>' +
                  '<td class="num col-principal">' + fmt(row.principal) + '</td>' +
                  '<td class="num col-interest">'   + fmt(row.interest)  + '</td>' +
                  '<td class="num">' + fmt(row.balance) + '</td>' +
                '</tr>'
            );
        }
        tbody.innerHTML = rows.join("");
    }

    function pad2(n) { return n < 10 ? "0" + n : "" + n; }

    /* ── Reset ────────────────────────────────────────────── */

    function onReset() {
        document.getElementById("principal").value  = "25000";
        document.getElementById("annualRate").value = "6.5";
        document.getElementById("months").value     = "60";
        ["field-principal","field-rate","field-months"].forEach(function(id) {
            document.getElementById(id).classList.remove("is-invalid");
        });
        ["err-principal","err-rate","err-months"].forEach(function(id) {
            document.getElementById(id).textContent = "";
        });
        document.getElementById("results-panel").innerHTML =
            '<div class="card card--empty">' +
            '<div class="empty-state__icon"><i data-lucide="calculator"></i></div>' +
            '<p class="empty-state__desc">Fill in the loan details and click <strong>Calculate payment</strong> to see your summary.</p>' +
            '</div>';
        document.getElementById("schedule-section").hidden = true;
        lastSchedule = [];
        if (window.lucide) window.lucide.createIcons();
    }

    /* ── CSV Export ───────────────────────────────────────── */

    function onExportCsv() {
        if (!lastSchedule.length) return;
        var lines = ["Month,Payment (EUR),Principal (EUR),Interest (EUR),Balance (EUR)"];
        for (var i = 0; i < lastSchedule.length; i++) {
            var r = lastSchedule[i];
            lines.push([r.month, r.payment, r.principal, r.interest, r.balance].join(","));
        }
        var blob = new Blob([lines.join("\r\n")], { type: "text/csv" });
        var url  = URL.createObjectURL(blob);
        var a    = document.createElement("a");
        a.href     = url;
        a.download = "amortization-schedule.csv";
        a.click();
        URL.revokeObjectURL(url);
    }

    /* ── Helpers ──────────────────────────────────────────── */

    function escHtml(s) {
        return String(s).replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;");
    }

}());
