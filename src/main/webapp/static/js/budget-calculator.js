(function () {
    "use strict";

    var SVG_NS = "http://www.w3.org/2000/svg";

    document.addEventListener("DOMContentLoaded", function () {
        document.getElementById("btn-analyze").addEventListener("click", onAnalyze);
    });

    /* ── Validation ───────────────────────────────────────── */

    function getNum(id) { return parseFloat(document.getElementById(id).value); }

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
        var ok     = true;
        var income = getNum("monthlyIncome");
        var fixed  = getNum("fixedExpenses");
        var varExp = getNum("variableExpenses");
        var savings = getNum("savingsGoal");

        if (!income || income <= 0) {
            setError("field-income","err-income","Monthly income must be greater than 0."); ok = false;
        } else { setError("field-income","err-income",""); }

        if (isNaN(fixed) || fixed < 0) {
            setError("field-fixed","err-fixed","Fixed expenses must be 0 or more."); ok = false;
        } else { setError("field-fixed","err-fixed",""); }

        if (isNaN(varExp) || varExp < 0) {
            setError("field-variable","err-variable","Variable expenses must be 0 or more."); ok = false;
        } else { setError("field-variable","err-variable",""); }

        if (isNaN(savings) || savings < 0) {
            setError("field-savings","err-savings","Savings goal must be 0 or more."); ok = false;
        } else { setError("field-savings","err-savings",""); }

        if (ok && (fixed + varExp) >= income) {
            setError("field-variable","err-variable","Total expenses exceed or equal your income."); ok = false;
        }

        return ok;
    }

    /* ── Analysis ─────────────────────────────────────────── */

    function analyzeBudget(income, fixed, varExp, savings) {
        var remaining    = round2(income - fixed - varExp);
        var savingsRate  = round2(savings / income * 100);
        var fixedPct     = round2(fixed   / income * 100);
        var varPct       = round2(varExp  / income * 100);
        var savingsPct   = round2(savings / income * 100);
        var freePct      = round2(remaining / income * 100);

        // Budget health: floor((remaining / income) × 250), capped at 100
        var health = Math.min(100, Math.floor((remaining / income) * 250));
        var healthLabel;
        if      (health >= 80) healthLabel = "Excellent";
        else if (health >= 60) healthLabel = "Good, needs attention";
        else if (health >= 40) healthLabel = "Moderate";
        else                   healthLabel = "Over budget";

        // Dynamic tip
        var tip;
        if (varPct > 35) {
            tip = "Your variable spending is high relative to your income. Consider reducing discretionary expenses.";
        } else if (fixedPct > 55) {
            tip = "Your fixed costs take up a large share of income. Look for ways to reduce bills or housing costs.";
        } else if (savingsRate < 10) {
            tip = "Your savings rate is below 10%. Even small increases can compound significantly over time.";
        } else {
            tip = "Your savings goal is realistic. Keep an eye on variable spending to stay on track.";
        }

        return {
            remaining: remaining, savingsRate: savingsRate,
            health: health, healthLabel: healthLabel, tip: tip,
            fixedPct: fixedPct, varPct: varPct, savingsPct: savingsPct, freePct: freePct
        };
    }

    function round2(n) { return Math.round(n * 100) / 100; }

    function fmt(n) {
        return "€" + Math.abs(n).toLocaleString("en-US", { minimumFractionDigits: 0, maximumFractionDigits: 0 });
    }
    function fmtDec(n) {
        return n.toLocaleString("en-US", { minimumFractionDigits: 1, maximumFractionDigits: 1 }) + "%";
    }

    /* ── Main render ──────────────────────────────────────── */

    function onAnalyze() {
        if (!validate()) return;

        var income  = getNum("monthlyIncome");
        var fixed   = getNum("fixedExpenses");
        var varExp  = getNum("variableExpenses");
        var savings = getNum("savingsGoal");

        var res = analyzeBudget(income, fixed, varExp, savings);

        // KPI strip
        document.getElementById("kpi-remaining").textContent     = fmt(res.remaining);
        document.getElementById("kpi-savings-rate").textContent  = fmtDec(res.savingsRate);
        document.getElementById("kpi-health").textContent        = res.health + "%";
        document.getElementById("kpi-health-label").textContent  = res.healthLabel;
        document.getElementById("kpi-strip").hidden = false;

        // Info box
        document.getElementById("info-box-text").textContent = res.tip;
        document.getElementById("info-box").hidden = false;

        // Breakdown card
        renderBreakdown(fixed, varExp, savings, res, document.getElementById("breakdown-panel"));

        // Category review table
        renderReviewTable(income, fixed, varExp, savings, res);
        document.getElementById("review-section").hidden = false;

        if (window.lucide) window.lucide.createIcons();
    }

    /* ── Breakdown card (progress bars + donut) ───────────── */

    function renderBreakdown(fixed, varExp, savings, res, container) {
        var html =
            '<div class="card card--padded">' +
              '<div class="card-title">Budget breakdown</div>' +
              '<div class="card-subtitle">How your monthly income is distributed.</div>' +
              '<div class="breakdown-inner">' +

                // Progress bars — use existing .progress / .progress__bar with --pct CSS var
                '<div class="progress-rows">' +
                  progressRow("Fixed expenses",    fmt(fixed),   res.fixedPct,   "red") +
                  progressRow("Variable expenses", fmt(varExp),  res.varPct,     "amber") +
                  progressRow("Savings goal",      fmt(savings), res.savingsPct, "green") +
                '</div>' +

                // Donut
                '<div class="donut-wrap">' +
                  buildDonutSvg(res.fixedPct, res.varPct, res.savingsPct, res.freePct) +
                  '<div class="donut-wrap__label">' +
                    '<span class="donut-wrap__pct">' + Math.round(res.freePct) + '%</span>' +
                    '<span class="donut-wrap__free-label">free</span>' +
                  '</div>' +
                '</div>' +

              '</div>' +
            '</div>';

        container.innerHTML = html;
    }

    function progressRow(label, amount, pct, color) {
        var fillPct = Math.min(100, Math.max(0, pct));
        // Uses existing .progress / .progress__bar from components.css;
        // .progress__bar--red/amber/green overrides the bar colour in guest.css
        return '<div class="progress-row">' +
            '<div class="progress-row__header">' +
              '<span class="progress-row__label">' + label + '</span>' +
              '<span class="progress-row__amount">' + amount + '</span>' +
            '</div>' +
            '<div class="progress" style="margin-bottom:var(--space-1);">' +
              '<div class="progress__bar progress__bar--' + color + '" style="--pct:' + fillPct + '%;width:var(--pct)"></div>' +
            '</div>' +
            '<div class="progress-row__pct">' + Math.round(pct) + '%</div>' +
        '</div>';
    }

    /* ── Donut SVG ────────────────────────────────────────── */

    function buildDonutSvg(fixedPct, varPct, savingsPct, freePct) {
        var size   = 120;
        var cx     = size / 2;
        var cy     = size / 2;
        var r      = 44;
        var circ   = 2 * Math.PI * r;
        var stroke = 14;

        // Background ring
        var bg = '<circle cx="' + cx + '" cy="' + cy + '" r="' + r + '" fill="none" ' +
                 'stroke="#E2E8F0" stroke-width="' + stroke + '"/>';

        // Segments (fixed, variable, savings; free = remainder)
        var segments = [
            { pct: fixedPct,   color: "#DC2626" }, // red  — fixed
            { pct: varPct,     color: "#F59E0B" }, // amber — variable
            { pct: savingsPct, color: "#16A34A" }, // green — savings
        ];

        var offset = 0; // start at top (rotate -90 handled via transform)
        var arcs = segments.map(function (seg) {
            var dash   = (seg.pct / 100) * circ;
            var gap    = circ - dash;
            var arc = '<circle cx="' + cx + '" cy="' + cy + '" r="' + r + '" fill="none" ' +
                      'stroke="' + seg.color + '" stroke-width="' + stroke + '" ' +
                      'stroke-dasharray="' + dash + ' ' + gap + '" ' +
                      'stroke-dashoffset="' + (-offset / 100 * circ) + '" ' +
                      'transform="rotate(-90 ' + cx + ' ' + cy + ')"/>';
            offset += seg.pct;
            return arc;
        });

        return '<svg viewBox="0 0 ' + size + ' ' + size + '" xmlns="http://www.w3.org/2000/svg">' +
               bg + arcs.join("") + '</svg>';
    }

    /* ── Category review table ────────────────────────────── */

    function renderReviewTable(income, fixed, varExp, savings, res) {
        var remaining = round2(income - fixed - varExp);

        function status(pct, type) {
            if (type === "fixed") {
                return pct > 50 ? '<span class="status-high">High</span>'
                                : '<span class="status-on-track">On track</span>';
            }
            if (type === "variable") {
                return pct > 35 ? '<span class="status-high">High</span>'
                                : '<span class="status-on-track">On track</span>';
            }
            if (type === "savings") {
                if (pct >= 20) return '<span class="status-great">Great</span>';
                if (pct >= 10) return '<span class="status-on-track">On track</span>';
                return '<span class="status-low">Low</span>';
            }
            return '<span class="status-neutral">&mdash;</span>';
        }

        var rows = [
            { label: "Fixed expenses",    amount: fixed,     pct: res.fixedPct,    type: "fixed"    },
            { label: "Variable expenses", amount: varExp,    pct: res.varPct,      type: "variable" },
            { label: "Savings goal",      amount: savings,   pct: res.savingsPct,  type: "savings"  },
            { label: "Remaining",         amount: remaining, pct: res.freePct,     type: "other"    }
        ];

        var html = rows.map(function (row) {
            return '<tr>' +
                '<td>' + row.label + '</td>' +
                '<td class="num">' + fmt(row.amount) + '</td>' +
                '<td class="num">' + Math.round(row.pct) + '%</td>' +
                '<td>' + status(row.pct, row.type) + '</td>' +
            '</tr>';
        }).join("");

        document.getElementById("review-body").innerHTML = html;
    }

}());
