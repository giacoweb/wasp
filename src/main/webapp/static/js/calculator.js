(function () {
    "use strict";

    function addRow(type) {
        var container = document.getElementById(type + "-rows");
        var div = document.createElement("div");
        div.className = type + "-row calculator-row";

        var catInput = document.createElement("input");
        catInput.type = "text";
        catInput.name = type + "Category";
        catInput.placeholder = "Category";
        catInput.required = true;

        var amtInput = document.createElement("input");
        amtInput.type = "number";
        amtInput.name = type + "Amount";
        amtInput.placeholder = "Amount (€)";
        amtInput.step = "0.01";
        amtInput.min = "0.01";
        amtInput.required = true;

        var removeBtn = document.createElement("button");
        removeBtn.type = "button";
        removeBtn.textContent = "✕";
        removeBtn.className = "js-calculator-remove";

        div.appendChild(catInput);
        div.appendChild(amtInput);
        div.appendChild(removeBtn);
        container.appendChild(div);
    }

    function removeRow(button) {
        var row = button.parentElement;
        var container = row.parentElement;
        if (container.children.length > 1) {
            container.removeChild(row);
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll("[data-calculator-add]").forEach(function (button) {
            button.addEventListener("click", function () {
                addRow(button.dataset.calculatorAdd);
            });
        });

        document.addEventListener("click", function (event) {
            var removeButton = event.target.closest(".js-calculator-remove");
            if (removeButton) removeRow(removeButton);
        });
    });
}());
