(function () {
    "use strict";

    function createRow(type) {
        var row = document.createElement("div");
        row.className = "calc-dynamic-row";

        var config = type === "income"
            ? { catName: "incomeCategory", amtName: "incomeAmount", placeholder: "e.g. Salary", list: "incomeCategories" }
            : { catName: "expenseCategory", amtName: "expenseAmount", placeholder: "e.g. Rent, Groceries", list: "expenseCategories" };

        row.innerHTML =
            '<input type="text" name="' + config.catName + '" class="form-input" placeholder="' + config.placeholder + '" list="' + config.list + '" required>' +
            '<div class="form-input-group form-input-group--prefix-calc">' +
                '<span class="input-prefix-calc">€</span>' +
                '<input type="text" name="' + config.amtName + '" class="form-input" placeholder="0.00" pattern="^[0-9]+([.,][0-9]+)?$" title="Please enter a valid amount (e.g. 100 or 100.50)" required>' +
            '</div>' +
            '<button type="button" class="btn btn-ghost btn-icon btn-sm js-remove-budget-row">' +
                '<i data-lucide="trash-2"></i>' +
            '</button>';

        return row;
    }

    function removeRow(button) {
        var row = button.closest(".calc-dynamic-row");
        var container = row.parentElement;
        if (container.querySelectorAll(".calc-dynamic-row").length > 1) {
            row.remove();
        } else {
            row.querySelectorAll('input[type="text"]').forEach(function (input) {
                input.value = "";
            });
        }
    }

    function renderChart() {
        var canvas = document.getElementById("budgetDonutChart");
        if (!canvas || !window.Chart) return;

        var expenseVal = parseFloat(canvas.dataset.expenses || "0");
        var savingsVal = parseFloat(canvas.dataset.savings || "0");
        var dataVals = expenseVal === 0 && savingsVal === 0 ? [0, 1] : [expenseVal, savingsVal];

        new Chart(canvas.getContext("2d"), {
            type: "doughnut",
            data: {
                labels: ["Expenses", "Savings"],
                datasets: [{
                    data: dataVals,
                    backgroundColor: ["#ccfbf1", "#FDE047"],
                    borderWidth: 0,
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: false,
                cutout: "75%",
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        enabled: expenseVal > 0 || savingsVal > 0,
                        callbacks: {
                            label: function (context) {
                                return " " + context.label + ": €" + context.raw.toFixed(2);
                            }
                        }
                    }
                }
            }
        });
    }

    function applyProgressBars() {
        document.querySelectorAll(".js-progress-bar").forEach(function (bar) {
            bar.style.setProperty("--pct", (bar.dataset.pct || "0") + "%");
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll("[data-add-budget-row]").forEach(function (button) {
            button.addEventListener("click", function () {
                var type = button.dataset.addBudgetRow;
                var container = document.getElementById(type + "-rows");
                if (!container) return;
                container.appendChild(createRow(type));
                if (window.lucide) window.lucide.createIcons();
            });
        });

        document.addEventListener("click", function (event) {
            var button = event.target.closest(".js-remove-budget-row");
            if (button) removeRow(button);
        });

        var form = document.querySelector("form.form");
        if (form) {
            form.addEventListener("submit", function () {
                form.querySelectorAll('input[name="incomeAmount"], input[name="expenseAmount"], input[name="savingsGoal"]').forEach(function (input) {
                    if (input.value) input.value = input.value.replace(/,/g, ".").trim();
                });
            });
        }

        applyProgressBars();
        renderChart();
    });
}());
