(function () {
    "use strict";

    function toNumberList(value) {
        if (!value) return [];
        return value.split(",").map(function (item) {
            return parseFloat(item);
        }).filter(function (item) {
            return !Number.isNaN(item);
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        var canvas = document.getElementById("compoundChart");
        if (!canvas || !window.Chart) return;

        var points = Array.prototype.slice.call(document.querySelectorAll(".js-compound-point"));
        var values = points.length
            ? points.map(function (point) { return parseFloat(point.dataset.value || "0"); })
            : toNumberList(canvas.dataset.values);
        var contributions = points.length
            ? points.map(function (point) { return parseFloat(point.dataset.contribution || "0"); })
            : toNumberList(canvas.dataset.contributions);
        var labels = values.map(function (_, index) {
            return "Year " + (index + 1);
        });

        new Chart(canvas, {
            type: "line",
            data: {
                labels: labels,
                datasets: [
                    {
                        label: "Contributions",
                        data: contributions,
                        borderColor: "#CBD5E1",
                        backgroundColor: "rgba(203, 213, 225, 0.5)",
                        borderWidth: 2,
                        tension: 0.3,
                        fill: true,
                        pointBackgroundColor: "#FFFFFF",
                        pointBorderColor: "#CBD5E1",
                        pointHoverBackgroundColor: "#CBD5E1",
                        pointHoverBorderColor: "#FFFFFF"
                    },
                    {
                        label: "Compound Value",
                        data: values,
                        borderColor: "#0F766E",
                        backgroundColor: "rgba(15, 118, 110, 0.15)",
                        borderWidth: 2,
                        tension: 0.3,
                        fill: "-1",
                        pointBackgroundColor: "#FFFFFF",
                        pointBorderColor: "#0F766E",
                        pointHoverBackgroundColor: "#0F766E",
                        pointHoverBorderColor: "#FFFFFF"
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    x: {
                        ticks: { font: { family: "'Inter', sans-serif" }, color: "#64748B" },
                        grid: { display: false },
                        border: { color: "#ECECEC" }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { font: { family: "'Inter', sans-serif" }, color: "#64748B" },
                        grid: { color: "#ECECEC", drawBorder: false },
                        border: { display: false }
                    }
                }
            }
        });
    });
}());
