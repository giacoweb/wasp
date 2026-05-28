(function () {
    "use strict";

    var SVG_NS = "http://www.w3.org/2000/svg";

    document.addEventListener("DOMContentLoaded", function () {
        document.getElementById("btn-calculate").addEventListener("click", onCalculate);
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
        var ok = true;
        var initial  = getNum("initialAmount");
        var monthly  = getNum("monthlyContrib");
        var rate     = getNum("annualReturn");
        var years    = getNum("durationYears");

        if (!initial || initial <= 0) {
            setError("field-initial","err-initial","Initial amount must be greater than 0."); ok = false;
        } else { setError("field-initial","err-initial",""); }

        if (isNaN(monthly) || monthly < 0) {
            setError("field-monthly","err-monthly","Monthly contribution must be 0 or more."); ok = false;
        } else { setError("field-monthly","err-monthly",""); }

        if (isNaN(rate) || rate < 0 || rate > 100) {
            setError("field-rate","err-rate","Annual return must be between 0 and 100."); ok = false;
        } else { setError("field-rate","err-rate",""); }

        if (!years || years < 1 || years > 50) {
            setError("field-years","err-years","Duration must be between 1 and 50 years."); ok = false;
        } else { setError("field-years","err-years",""); }

        return ok;
    }

    /* ── Calculation ──────────────────────────────────────── */

    function calculateCompound(initial, monthly, annualRate, years) {
        var monthlyRate = annualRate / 12 / 100;
        var balance = initial;
        var results = [];

        for (var y = 1; y <= years; y++) {
            for (var m = 1; m <= 12; m++) {
                balance = (balance + monthly) * (1 + monthlyRate);
            }
            var contributed    = round2(initial + monthly * 12 * y);
            var roundedBalance = round2(balance);
            var interestEarned = round2(roundedBalance - contributed);

            results.push({
                year:          y,
                balance:       roundedBalance,
                contributed:   contributed,
                interestEarned: interestEarned
            });
        }
        return results;
    }

    function round2(n) { return Math.round(n * 100) / 100; }

    function fmt(n) {
        var negative = n < 0;
        var abs = Math.abs(n);
        var s = "€" + abs.toLocaleString("en-US", { minimumFractionDigits: 0, maximumFractionDigits: 0 });
        return negative ? "-" + s : s;
    }

    /* ── Render ───────────────────────────────────────────── */

    function onCalculate() {
        if (!validate()) return;

        var initial = getNum("initialAmount");
        var monthly = getNum("monthlyContrib");
        var rate    = getNum("annualReturn");
        var years   = parseInt(document.getElementById("durationYears").value, 10);

        var results = calculateCompound(initial, monthly, rate, years);
        var last    = results[results.length - 1];

        // KPI strip
        document.getElementById("kpi-future-value").textContent = fmt(last.balance);
        document.getElementById("kpi-future-caption").textContent = "After " + years + " year" + (years !== 1 ? "s" : "");
        document.getElementById("kpi-contributed").textContent  = fmt(last.contributed);
        document.getElementById("kpi-interest").textContent     = fmt(last.interestEarned);
        document.getElementById("kpi-strip").hidden = false;

        // Chart
        renderChart(results, document.getElementById("chart-panel"));

        // Table
        renderTable(results);
        document.getElementById("projection-section").hidden = false;

        if (window.lucide) window.lucide.createIcons();
    }

    /* ── SVG Chart ────────────────────────────────────────── */

    function svgEl(tag, attrs) {
        var el = document.createElementNS(SVG_NS, tag);
        if (attrs) {
            Object.keys(attrs).forEach(function (k) { el.setAttribute(k, attrs[k]); });
        }
        return el;
    }

    function renderChart(results, container) {
        var W = 560, H = 260;
        var pad = { top: 20, right: 20, bottom: 36, left: 58 };
        var cW = W - pad.left - pad.right;
        var cH = H - pad.top  - pad.bottom;
        var n  = results.length;

        // Y scale
        var allVals = [];
        results.forEach(function (r) { allVals.push(r.balance, r.contributed); });
        var yMin = 0;
        var yMax = Math.max.apply(null, allVals) * 1.08;

        function xOf(idx) { return pad.left + (idx / (n - 1)) * cW; }
        function yOf(v)   { return pad.top  + (1 - (v - yMin) / (yMax - yMin)) * cH; }

        // Clear previous
        container.innerHTML = "";

        var wrapper = document.createElement("div");
        wrapper.className = "chart-wrap";

        // Legend
        var legendHtml =
            '<div class="chart-wrap__header">' +
              '<span class="chart-wrap__title">Growth projection</span>' +
              '<ul class="chart-legend">' +
                '<li><span class="chart-legend__dot chart-legend__dot--grey"></span> Contributions</li>' +
                '<li><span class="chart-legend__dot chart-legend__dot--teal"></span> Interest</li>' +
              '</ul>' +
            '</div>';
        wrapper.innerHTML = legendHtml;

        var svgWrap = document.createElement("div");
        svgWrap.className = "chart-svg-wrap";

        var svg = svgEl("svg", { viewBox: "0 0 " + W + " " + H, preserveAspectRatio: "xMidYMid meet" });

        // Y-axis grid lines (5 lines)
        var ySteps = 5;
        for (var g = 0; g <= ySteps; g++) {
            var yVal = yMin + (yMax - yMin) * (g / ySteps);
            var yPos = yOf(yVal);

            // grid line
            svg.appendChild(svgEl("line", {
                x1: pad.left, y1: yPos, x2: pad.left + cW, y2: yPos,
                stroke: "#ECECEC", "stroke-width": "1"
            }));

            // y label
            var labelText = svgEl("text", {
                x: pad.left - 6, y: yPos + 4,
                "text-anchor": "end",
                "font-size": "11",
                fill: "#94A3B8",
                "font-family": "Inter, sans-serif"
            });
            labelText.textContent = fmtK(yVal);
            svg.appendChild(labelText);
        }

        // X-axis labels (every year, or select Y1, Y2, Y4, Y6, Y8, Y10)
        var xLabelIdxs;
        if (n <= 10) {
            xLabelIdxs = results.map(function (_, i) { return i; });
        } else {
            xLabelIdxs = [0, 1, 3, 5, 7, 9]; // Y1,Y2,Y4,Y6,Y8,Y10
            xLabelIdxs = xLabelIdxs.filter(function (i) { return i < n; });
        }

        xLabelIdxs.forEach(function (idx) {
            var xPos = xOf(idx);
            var xText = svgEl("text", {
                x: xPos, y: H - 6,
                "text-anchor": "middle",
                "font-size": "11",
                fill: "#94A3B8",
                "font-family": "Inter, sans-serif"
            });
            xText.textContent = "Y" + results[idx].year;
            svg.appendChild(xText);
        });

        // Polyline: contributions (grey)
        var contribPoints = results.map(function (r, i) {
            return xOf(i) + "," + yOf(r.contributed);
        }).join(" ");
        svg.appendChild(svgEl("polyline", {
            points: contribPoints,
            fill: "none",
            stroke: "#CBD5E1",
            "stroke-width": "2",
            "stroke-linejoin": "round",
            "stroke-linecap": "round"
        }));

        // Polyline: balance (teal)
        var balancePoints = results.map(function (r, i) {
            return xOf(i) + "," + yOf(r.balance);
        }).join(" ");
        svg.appendChild(svgEl("polyline", {
            points: balancePoints,
            fill: "none",
            stroke: "#0F766E",
            "stroke-width": "2.5",
            "stroke-linejoin": "round",
            "stroke-linecap": "round"
        }));

        // Dots on balance line
        results.forEach(function (r, i) {
            svg.appendChild(svgEl("circle", {
                cx: xOf(i), cy: yOf(r.balance),
                r: "3", fill: "#0F766E"
            }));
        });

        svgWrap.appendChild(svg);
        wrapper.appendChild(svgWrap);
        container.appendChild(wrapper);
    }

    function fmtK(v) {
        if (v >= 1000) return "€" + Math.round(v / 1000) + "k";
        return "€" + Math.round(v);
    }

    /* ── Table ────────────────────────────────────────────── */

    function renderTable(results) {
        var rows = results.map(function (r) {
            return '<tr>' +
                '<td>Year ' + r.year + '</td>' +
                '<td class="num">' + fmt(r.contributed)    + '</td>' +
                '<td class="num amount-positive">' + fmt(r.interestEarned) + '</td>' +
                '<td class="num">' + fmt(r.balance)        + '</td>' +
            '</tr>';
        });
        document.getElementById("projection-body").innerHTML = rows.join("");
    }

}());
