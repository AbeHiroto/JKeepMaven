document.addEventListener("DOMContentLoaded", function () {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    async function saveNewNote(title, content) {
        const response = await fetch('/api/notes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ title, content }),
            credentials: "include"
        });
        console.log(title, content);
        if (!response.ok) {
            alert("ノートの保存に失敗しました");
        }
    }

    // ここに createButton などのイベントハンドラ設定を移動
    const createButton = document.getElementById('create-note-button');
    const newNoteArea = document.getElementById('new-note-area');
    const mainArea = document.getElementById('main-area');
    const saveBtn = document.getElementById('save-new-note');
    const cancelBtn = document.getElementById('cancel-new-note');

    createButton.addEventListener('click', () => {
        mainArea.style.display = 'none';
        newNoteArea.style.display = 'block';
        document.getElementById('new-title').value = '';
        document.getElementById('new-content').value = '';
    });

	if (cancelBtn) {
	    cancelBtn.addEventListener('click', async () => {
	        const title = document.getElementById('new-title').value.trim();
	        const content = document.getElementById('new-content').value.trim();
	        if (title || content) {
	            await saveNewNote(title, content);
	        }
	        await getAllNotes();
	        newNoteArea.style.display = 'none';
	        mainArea.style.display = 'block';
	    });
	}

	if (saveBtn) {
	    saveBtn.addEventListener('click', async () => {
	        const title = document.getElementById('new-title').value.trim();
	        const content = document.getElementById('new-content').value.trim();
	        await saveNewNote(title, content);
	        await getAllNotes();
	        newNoteArea.style.display = 'none';
	        mainArea.style.display = 'block';
	    });
	}
	
	const sidebarNotes = document.getElementById('sidebar-notes');
	if (sidebarNotes && sidebarNotes.children.length === 0) {
	    // まだ一覧が描画されていない（直接URLアクセスなど）
	    fetch('/notes/list-data')
	        .then(res => res.json())
	        .then(data => {
	            sidebarNotes.innerHTML = ''; // いったんクリア
	            data.forEach(note => {
	                const div = document.createElement('div');
	                div.className = 'note-summary';
	                div.id = `note-${note.id}`;
	                div.onclick = () => {
	                    window.location.href = `/notes/${note.id}`;
	                };
	                div.innerHTML = `
	                    <div class="note-title">${note.title}</div>
	                    <div class="note-content-preview">${
	                        note.summaryContent.length > 24
	                            ? note.summaryContent.substring(0, 24) + '…'
	                            : note.summaryContent
	                    }</div>
	                `;
	                sidebarNotes.appendChild(div);
	            });
	        })
	        .catch(err => {
	            console.error('ノート一覧の取得に失敗しました', err);
	        });
	}
	
	getAllNotes(); // 初回読み込み時にも実行
});

async function getAllNotes() {
    const sidebarNotes = document.getElementById('sidebar-notes');
    if (!sidebarNotes) return;

    try {
        const res = await fetch('/notes/list-data');
        const data = await res.json();

        sidebarNotes.innerHTML = ''; // クリアして再描画
        data.forEach(note => {
            const div = document.createElement('div');
            div.className = 'note-summary';
            div.id = `note-${note.id}`;
            div.onclick = () => {
                window.location.href = `/notes/${note.id}`;
            };
            div.innerHTML = `
                <div class="note-title">${note.title}</div>
                <div class="note-content-preview">${
                    note.summaryContent.length > 24
                        ? note.summaryContent.substring(0, 24) + '…'
                        : note.summaryContent
                }</div>
            `;
            sidebarNotes.appendChild(div);
        });
    } catch (err) {
        console.error('ノート一覧の取得に失敗しました', err);
    }
}


function loadNote(noteId) {
    window.location.href = `/notes/${noteId}`;
}

