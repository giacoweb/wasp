document.addEventListener("DOMContentLoaded", function() {

    const containers = document.querySelectorAll('.table-paginated-container');

    containers.forEach(container => {
        const tbody = container.querySelector('.table-paginate-body');
        const info = container.querySelector('.table-pagination-info');
        const buttonsContainer = container.querySelector('.table-pagination-buttons');

        if (!tbody || !info || !buttonsContainer) return;
        const rows = tbody.querySelectorAll('tr');
        const total = rows.length;

        const perPage = parseInt(container.getAttribute('data-per-page')) || 12;
        const itemName = container.getAttribute('data-item-name') || 'items';

        let page = 1;
        const pages = Math.ceil(total / perPage);
        function showPage() {
            const start = (page - 1) * perPage;
            const end = Math.min(start + perPage, total);

            for (let i = 0; i < total; i++) {
                rows[i].style.display = (i >= start && i < end) ? '' : 'none';
            }

            info.textContent = `${start + 1} – ${end} of ${total} ${itemName}`;
            buttonsContainer.innerHTML = '';

            const prev = document.createElement('button');
            prev.textContent = '‹ Prev';
            prev.className = 'btn btn-ghost btn-sm'; 
            prev.disabled = page === 1;
            prev.onclick = () => { page--; showPage(); };
            buttonsContainer.appendChild(prev);

            for (let p = 1; p <= pages; p++) {
                const btn = document.createElement('button');
                btn.textContent = p;
                btn.className = p === page ? 'btn btn-primary btn-sm' : 'btn btn-ghost btn-sm';
                btn.onclick = () => { page = p; showPage(); };
                buttonsContainer.appendChild(btn);
            }


            const next = document.createElement('button');
            next.textContent = 'Next ›';
            next.className = 'btn btn-ghost btn-sm';
            next.disabled = page === pages;
            next.onclick = () => { page++; showPage(); };
            buttonsContainer.appendChild(next);
        }

        if (total > 0) {
            showPage();
        } else {
            info.textContent = `0 ${itemName}`;
        }
    });
});