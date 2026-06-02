(function () {
    "use strict";

    var SCENARIO_DEFAULT = "CONSERVATIVE";
    var SCENARIO_INFO = {
        OPTIMISTIC: "Income increased by 10%, expenses unchanged.",
        CONSERVATIVE: "Income unchanged, expenses with 3% monthly compounding inflation.",
        STRESS_TEST: "Income reduced by 20%, with a 3× expense shock at month 6."
    };
    var SVG_NS = "http://www.w3.org/2000/svg";

    var ctx = document.body.dataset.contextPath || "";
    var currentScenario = SCENARIO_DEFAULT;

    document.addEventListener("DOMContentLoaded", function () {
        bindScenarioPills();
        bindRecomputeButtons();
        loadScenario(currentScenario);
    });

    function bindScenarioPills() {
        document.querySelectorAll(".scenario-pill").forEach(function (pill) {
            pill.addEventListener("click", function () {
                var scenario = pill.dataset.scenario;
                if (!scenario || scenario === currentScenario) return;
                setActivePill(scenario);
                currentScenario = scenario;
                loadScenario(scenario);
            });
        });
    }

    function setActivePill(scenario) {
        document.querySelectorAll(".scenario-pill").forEach(function (pill) {
            var isActive = pill.dataset.scenario === scenario;
            pill.classList.toggle("is-active", isActive);
            pill.setAttribute("aria-checked", isActive ? "true" : "false");
        });
        var info = document.getElementById("scenario-info-text");
        if (info) info.textContent = SCENARIO_INFO[scenario] || "";
    }

    function bindRecomputeButtons() {
        var topBtn = document.getElementById("recompute-btn");
        if (topBtn) topBtn.addEventListener("click", function () { computeScenario(currentScenario); });
        var emptyBtn = document.getElementById("empty-recompute-btn");
        if (emptyBtn) emptyBtn.addEventListener("click", function () { computeScenario(currentScenario); });
    }

    function showState(state) {
        ["loading", "empty", "error", "ready"].forEach(function (s) {
            var el = document.getElementById("state-" + s);
            if (el) el.hidden = (s !== state);
        });
    }

    function showError(message) {
        var msg = document.getElementById("state-error-message");
        if (msg) msg.textContent = message;
        showState("error");
    }

    function loadScenario(scenario) {
        showState("loading");
        fetch(ctx + "/api/forecast?scenario=" + encodeURIComponent(scenario), {
            headers: { "Accept": "application/json" }
        }).then(handleResponse).catch(handleNetworkError);
    }

    function computeScenario(scenario) {
        showState("loading");
        fetch(ctx + "/api/forecast?scenario=" + encodeURIComponent(scenario), {
            method: "POST",
            headers: { "Accept": "application/json" }
        }).then(handleResponse).catch(handleNetworkError);
    }

    function handleResponse(res) {
        if (res.status === 401) {
            window.location.href = ctx + "/login";
            return;
        }
        if (res.status === 404) {
            showState("empty");
            return;
        }
        return res.text().then(function (text) {
            var data = {};
            if (text) {
                try { data = JSON.parse(text); } catch (e) { data = {}; }
            }
            if (!res.ok) {
                showError(data.error || ("Request failed with status " + res.status));
                return;
            }
            renderForecast(data);
            showState("ready");
        });
    }

    function handleNetworkError() {
        showError("Network error. Please try again.");
    }

    function renderForecast(data) {
        var projection = data.projection || [];
        if (projection.length === 0) {
            showState("empty");
            return;
        }
        var currency = projection[0].currency || "EUR";
        renderKpis(projection, currency);
        renderChart(projection);
        renderTable(projection, currency);
    }

    function currencySymbol(code) {
        switch (code) {
            case "EUR": return "€";
            case "USD": return "$";
            case "GBP": return "£";
            case "JPY": return "¥";
            default:    return code + " ";
        }
    }

    function formatMoney(n, currency) {
        var sign = n < 0 ? "-" : "";
        var abs = Math.abs(n);
        return sign + currencySymbol(currency) + abs.toLocaleString("en-US", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
    }

    function setText(id, text) {
        var el = document.getElementById(id);
        if (el) el.textContent = text;
    }

    function renderKpis(projection, currency) {
        var last = projection[projection.length - 1];
        var totalIncome = 0;
        var totalExpense = 0;
        var totalPac = 0;
        for (var i = 0; i < projection.length; i++) {
            totalIncome += Number(projection[i].income);
            totalExpense += Number(projection[i].expense);
            totalPac += Number(projection[i].pacContribution);
        }
        setText("kpi-end-balance", formatMoney(Number(last.balance), currency));
        setText("kpi-total-income", formatMoney(totalIncome, currency));
        setText("kpi-total-expenses", formatMoney(totalExpense, currency));
        setText("kpi-total-pac", formatMoney(totalPac, currency));
    }

    function svgEl(tag, attrs) {
        var el = document.createElementNS(SVG_NS, tag);
        if (attrs) {
            for (var k in attrs) {
                if (Object.prototype.hasOwnProperty.call(attrs, k)) {
                    el.setAttribute(k, attrs[k]);
                }
            }
        }
        return el;
    }

    function renderChart(projection) {
        var chart = document.getElementById("forecast-chart");
        if (!chart) return;
        while (chart.firstChild) chart.removeChild(chart.firstChild);

        var W = 800, H = 320;
        var pad = { top: 20, right: 20, bottom: 30, left: 60 };
        var chartW = W - pad.left - pad.right;
        var chartH = H - pad.top - pad.bottom;
        var n = projection.length;

        var incomes = projection.map(function (p) { return Number(p.income); });
        var expenses = projection.map(function (p) { return Number(p.expense) + Number(p.pacContribution); });
        var balances = projection.map(function (p) { return Number(p.balance); });

        var allValues = incomes.concat(expenses).concat(balances);
        var minY = Math.min(0, Math.min.apply(null, allValues));
        var maxY = Math.max.apply(null, allValues);
        var range = maxY - minY;
        if (range === 0) range = 1;
        minY -= range * 0.05;
        maxY += range * 0.05;

        function xAt(i) { return pad.left + (i / Math.max(1, n - 1)) * chartW; }
        function yAt(v) { return pad.top + chartH - ((v - minY) / (maxY - minY)) * chartH; }

        var grid = svgEl("g", { "class": "forecast-chart__grid" });
        var gridLines = 5;
        for (var i = 0; i <= gridLines; i++) {
            var v = minY + (i / gridLines) * (maxY - minY);
            var y = yAt(v);
            grid.appendChild(svgEl("line", { x1: pad.left, y1: y, x2: W - pad.right, y2: y }));
            var label = svgEl("text", { x: pad.left - 8, y: y + 4, "text-anchor": "end" });
            label.textContent = formatTick(v);
            grid.appendChild(label);
        }
        chart.appendChild(grid);

        var areaPath = "M " + xAt(0) + " " + yAt(0);
        for (var j = 0; j < n; j++) {
            areaPath += " L " + xAt(j) + " " + yAt(balances[j]);
        }
        areaPath += " L " + xAt(n - 1) + " " + yAt(0) + " Z";
        chart.appendChild(svgEl("path", { d: areaPath, "class": "forecast-chart__balance-area" }));

        chart.appendChild(linePath(balances, xAt, yAt, "forecast-chart__balance-line"));
        chart.appendChild(linePath(incomes,  xAt, yAt, "forecast-chart__income-line"));
        chart.appendChild(linePath(expenses, xAt, yAt, "forecast-chart__expense-line"));

        var xLabels = svgEl("g", { "class": "forecast-chart__x-labels" });
        [0, 5, 11, 17, 23].forEach(function (idx) {
            if (idx >= n) return;
            var text = svgEl("text", { x: xAt(idx), y: H - 10, "text-anchor": "middle" });
            text.textContent = "M" + (idx + 1);
            xLabels.appendChild(text);
        });
        chart.appendChild(xLabels);
    }

    function linePath(values, xAt, yAt, cls) {
        var d = "";
        for (var i = 0; i < values.length; i++) {
            d += (i === 0 ? "M " : " L ") + xAt(i) + " " + yAt(values[i]);
        }
        return svgEl("path", { d: d, "class": cls });
    }

    function formatTick(v) {
        var abs = Math.abs(v);
        if (abs >= 1000000) return (v / 1000000).toFixed(1) + "M";
        if (abs >= 1000)    return (v / 1000).toFixed(1) + "k";
        return v.toFixed(0);
    }

    function renderTable(projection, currency) {
        var tbody = document.getElementById("forecast-tbody");
        if (!tbody) return;
        while (tbody.firstChild) tbody.removeChild(tbody.firstChild);

        projection.forEach(function (p) {
            var tr = document.createElement("tr");
            appendCell(tr, "Month " + p.month);
            appendCell(tr, formatMoney(Number(p.income), currency), "num amount-positive");
            appendCell(tr, formatMoney(-Number(p.expense), currency), "num amount-negative");
            appendCell(tr, formatMoney(-Number(p.pacContribution), currency), "num amount-negative");
            var net = Number(p.netFlow);
            appendCell(tr, formatMoney(net, currency), "num " + (net >= 0 ? "amount-positive" : "amount-negative"));
            appendCell(tr, formatMoney(Number(p.balance), currency), "num tabular");
            tbody.appendChild(tr);
        });
    }

    function appendCell(tr, text, cls) {
        var td = document.createElement("td");
        if (cls) td.className = cls;
        td.textContent = text;
        tr.appendChild(td);
    }
})();
