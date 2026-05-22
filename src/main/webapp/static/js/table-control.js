var rows    = document.querySelectorAll('#amortBody tr');
var total   = rows.length;
var perPage = 12;
var page    = 1;
var pages   = Math.ceil(total / perPage);

function showPage() {
    var start = (page - 1) * perPage;
    var end   = Math.min(start + perPage, total);
    for (var i = 0; i < total; i++) {
        rows[i].style.display = (i >= start && i < end) ? '' : 'none';
    }
    document.getElementById('pageInfo').textContent =
        (start + 1) + ' – ' + end + ' of ' + total + ' months';
    var container = document.getElementById('pageButtons');
    container.innerHTML = '';
    // Previous
    var prev = document.createElement('button');
    prev.textContent = '‹ Prev';
    prev.className = 'btn btn-ghost btn-sm';
    prev.disabled = page === 1;
    prev.onclick = function() { page--; showPage(); };
    container.appendChild(prev);
    // Page numbers
    for (var p = 1; p <= pages; p++) {
        var btn = document.createElement('button');
        btn.textContent = p;
        btn.className = p === page ? 'btn btn-primary btn-sm' : 'btn btn-ghost btn-sm';
        btn.onclick = (function(n) { return function() { page = n; showPage(); }; })(p);
        container.appendChild(btn);
    }
    // Next
    var next = document.createElement('button');
    next.textContent = 'Next ›';
    next.className = 'btn btn-ghost btn-sm';
    next.disabled = page === pages;
    next.onclick = function() { page++; showPage(); };
    container.appendChild(next);
}
showPage();