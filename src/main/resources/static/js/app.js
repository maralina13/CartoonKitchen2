(function(){
    const t = document.querySelector('meta[name="_csrf"]');
    const h = document.querySelector('meta[name="_csrf_header"]');
    function csrfFetch(url, opts={}) {
        const headers = Object.assign({}, opts.headers || {});
        if (t && h) headers[h.content] = t.content;
        return fetch(url, Object.assign({}, opts, { headers }));
    }
    window.csrfFetch = csrfFetch;

    document.addEventListener('DOMContentLoaded', () => {
        const q = document.getElementById('q');
        const box = document.getElementById('results');
        if (!q || !box) return;
        let timer;
        q.addEventListener('input', () => {
            clearTimeout(timer);
            const query = q.value.trim();
            if (!query) { box.innerHTML = ''; return; }
            timer = setTimeout(async () => {
                try {
                    const r = await csrfFetch('/api/dishes/search?q='+encodeURIComponent(query));
                    if (!r.ok) throw new Error('Search failed');
                    const items = await r.json();
                    box.innerHTML = items.map(d => `<li><a href="/dishes/${d.id}">${d.name}</a></li>`).join('');
                } catch(e) { alert('Ошибка поиска: '+e.message); }
            }, 250);
        });
    });
})();
async function uploadAndSet(fileInputId, urlInputSelector) {
    const el = document.getElementById(fileInputId);
    if (!el || !el.files || el.files.length === 0) return;
    const fd = new FormData();
    fd.append('file', el.files[0]);

    const r = await csrfFetch('/media/upload', { method: 'POST', body: fd });
    if (!r.ok) { alert('Загрузка не удалась'); return; }
    const data = await r.json();
    const urlInput = document.querySelector(urlInputSelector);
    if (urlInput) urlInput.value = data.url;
}

window.addEventListener('DOMContentLoaded', () => {
    const finalFile = document.getElementById('finalImageFile');
    const cartoonFile = document.getElementById('cartoonImageFile');
    if (finalFile) finalFile.addEventListener('change', () => uploadAndSet('finalImageFile', 'input[name="finalImageUrl"]'));
    if (cartoonFile) cartoonFile.addEventListener('change', () => uploadAndSet('cartoonImageFile', 'input[name="cartoonImageUrl"]'));
});
