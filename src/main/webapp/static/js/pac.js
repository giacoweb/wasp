(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {

        var createModal = document.getElementById("pac-create-modal");
        var editModal   = document.getElementById("pac-edit-modal");

        function openModal(modal) {
            if (!modal) return;
            modal.hidden = false;
            var firstInput = modal.querySelector("input:not([type=hidden]):not([type=checkbox]), select, textarea");
            if (firstInput) firstInput.focus();
        }

        function closeOpenModals() {
            [createModal, editModal].forEach(function (m) {
                if (m && !m.hidden) m.hidden = true;
            });
        }

        function setFieldError(input, message) {
            var field = input.closest(".form-field");
            if (!field) return;
            field.classList.add("is-invalid");
            var err = field.querySelector(".form-error");
            if (err) err.textContent = message;
        }

        function clearFieldError(input) {
            var field = input.closest(".form-field");
            if (!field) return;
            field.classList.remove("is-invalid");
            var err = field.querySelector(".form-error");
            if (err) err.textContent = "";
        }

        function validateForm(form) {
            var firstInvalid = null;

            Array.prototype.forEach.call(form.elements, function (el) {
                if (!el.willValidate) return;
                clearFieldError(el);
                if (!el.checkValidity()) {
                    setFieldError(el, el.validationMessage);
                    if (!firstInvalid) firstInvalid = el;
                }
            });

            var monthly = form.elements["monthlyAmount"];
            var target  = form.elements["targetAmount"];
            if (monthly && target && monthly.value && target.value) {
                if (parseFloat(monthly.value) > parseFloat(target.value)) {
                    setFieldError(monthly, "Monthly amount cannot exceed the target.");
                    if (!firstInvalid) firstInvalid = monthly;
                }
            }

            if (firstInvalid) firstInvalid.focus();
            return firstInvalid === null;
        }

        function syncActiveCheckbox(checkbox) {
            var hidden = document.getElementById("edit-active-value");
            var val = checkbox.checked ? "true" : "false";
            if (hidden) hidden.value = val;
            checkbox.setAttribute("aria-checked", val);
        }

        function bindOpenCreate(btn) {
            if (!btn) return;
            btn.addEventListener("click", function () { openModal(createModal); });
        }
        bindOpenCreate(document.getElementById("pac-create-btn"));
        bindOpenCreate(document.getElementById("pac-empty-create-btn"));

        document.addEventListener("click", function (e) {

            var editBtn = e.target.closest(".js-edit-plan");
            if (editBtn) {
                var d    = editBtn.dataset;
                var form = editModal.querySelector("form");
                form.elements["planId"].value        = d.id || "";
                form.elements["assetName"].value     = d.assetName || "";
                form.elements["monthlyAmount"].value = d.monthlyAmount || "";
                form.elements["targetAmount"].value  = d.targetAmount || "";
                form.elements["startDate"].value     = d.startDate || "";

                var checkbox = document.getElementById("edit-active");
                var isActive = d.active === "true";
                if (checkbox) {
                    checkbox.checked = isActive;
                    syncActiveCheckbox(checkbox);
                }
                openModal(editModal);
                return;
            }

            if (e.target.closest(".js-modal-close")) {
                closeOpenModals();
                return;
            }
            if (e.target.classList && e.target.classList.contains("modal-overlay")) {
                closeOpenModals();
            }
        });

        document.addEventListener("submit", function (e) {
            var form = e.target;

            if (form.classList && form.classList.contains("js-delete-form")) {
                var name = form.dataset.assetName || "this plan";
                if (!window.confirm("Delete PAC plan \"" + name + "\"? This cannot be undone.")) {
                    e.preventDefault();
                }
                return;
            }

            if (form.classList && form.classList.contains("js-validate")) {
                if (!validateForm(form)) e.preventDefault();
            }
        });

        document.addEventListener("keydown", function (e) {
            if (e.key === "Escape") closeOpenModals();
        });

        document.addEventListener("change", function (e) {
            if (e.target.id === "edit-active") {
                syncActiveCheckbox(e.target);
            }
        });

        document.addEventListener("input", function (e) {
            if (e.target.matches && e.target.matches(".form-input")) {
                clearFieldError(e.target);
            }
        });
    });
})();
