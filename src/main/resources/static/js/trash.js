document.addEventListener("DOMContentLoaded", function () {
    const noteSummaries = document.querySelectorAll(".note-summary");
    const mainArea = document.getElementById("main-area");

    noteSummaries.forEach(summary => {
        summary.addEventListener("click", function () {
            const title = this.dataset.title;
            const content = this.dataset.content;
            const id = this.dataset.id;
            const csrfParam = document.querySelector("input[name=_csrf]").name;
            const csrfToken = document.querySelector("input[name=_csrf]").value;

            mainArea.innerHTML = `
                <h1>ゴミ箱</h1>
                <div>
                    <h2>${title}</h2>
                    <p>${content}</p>
                    <form action="/notes/restore/${id}" method="post">
                        <input type="hidden" name="${csrfParam}" value="${csrfToken}" />
                        <button type="submit">元に戻す</button>
                    </form>
                </div>
            `;
        });
    });
});

function loadNote(noteId) {
    window.location.href = '/notes/trash/' + noteId;
}