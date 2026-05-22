(function () {
    var shell    = document.querySelector(".app-shell");
    var toggle   = document.getElementById("sidebar-toggle");
    var hamburger = document.getElementById("hamburger");
    var backdrop  = document.getElementById("sidebar-backdrop");
    if (!shell) return;

    /* Desktop collapse */
    if (toggle) {
        if (window.innerWidth > 640 && localStorage.getItem("sidebarCollapsed") === "1") {
            shell.classList.add("sidebar-collapsed");
        }
        toggle.addEventListener("click", function () {
            shell.classList.toggle("sidebar-collapsed");
            localStorage.setItem("sidebarCollapsed", shell.classList.contains("sidebar-collapsed") ? "1" : "0");
        });
    }

    /* Mobile overlay */
    function closeMobile() { shell.classList.remove("sidebar-open"); }
    if (hamburger) hamburger.addEventListener("click", function () { shell.classList.toggle("sidebar-open"); });
    if (backdrop)  backdrop.addEventListener("click", closeMobile);
})();
