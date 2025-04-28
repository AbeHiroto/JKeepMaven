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
	
	const deleteMainNoteBtn = document.getElementById('delete-main-note');
	if (deleteMainNoteBtn) {
	    deleteMainNoteBtn.addEventListener('click', () => {
	        const noteId = document.querySelector('#main-note-form input[name="id"]').value;
	        deleteNote(noteId);
	    });
	}
	
	getAllNotes(); // 初回読み込み時にも実行
});

async function deleteNote(noteId) {
    if (!confirm("本当にこのノートをゴミ箱に移動しますか？")) {
        return;
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const response = await fetch(`/notes/${noteId}/trash`, {
        method: 'POST',
        headers: {
            [csrfHeader]: csrfToken
        },
        credentials: "include"
    });

    if (response.ok) {
        console.log(`ノートID ${noteId} をゴミ箱に移動しました`);
        await refreshSidebarAndMainArea();
    } else {
        alert("削除に失敗しました");
    }
}

// サイドバーとメインエリアを両方更新する
async function refreshSidebarAndMainArea() {
    await getAllNotes();      // サイドバー更新
    await loadFirstNote();    // sort_order=0のノートを取得して編集エリア更新
}

async function getAllNotes() {
    const sidebarNotes = document.getElementById('sidebar-notes');
    const response = await fetch('/notes/list-data', { credentials: "include" });
    if (!response.ok) {
        console.error("ノート一覧取得失敗");
        return;
    }

    const notes = await response.json();
    sidebarNotes.innerHTML = '';

    notes.forEach(note => {
        const div = document.createElement('div');
        div.className = 'note-summary';
        div.id = `note-${note.id}`;
		div.innerHTML = `
		    <div class="note-header">
		        <div class="note-title">${note.title}</div>
				<div class="note-actions">
				    <button type="button" class="move-note-up" data-note-id="${note.id}">▲</button>
				    <button type="button" class="move-note-down" data-note-id="${note.id}">▼</button>
				    <button type="button" class="delete-note-button" data-note-id="${note.id}">🗑️</button>
				</div>
		    </div>
		    <div class="note-content-preview">${
		        note.summaryContent.length > 24
		            ? note.summaryContent.substring(0, 24) + '…'
		            : note.summaryContent
		    }</div>
		`;
		
		// 上移動ボタン
		div.querySelector('.move-note-up').addEventListener('click', (e) => {
		    e.stopPropagation();
		    moveNote(note.id, "up");
		});

		// 下移動ボタン
		div.querySelector('.move-note-down').addEventListener('click', (e) => {
		    e.stopPropagation();
		    moveNote(note.id, "down");
		});

        div.querySelector('.delete-note-button').addEventListener('click', (e) => {
            e.stopPropagation(); // ノートクリックを防止
            deleteNote(note.id);
        });

        div.addEventListener('click', () => {
            window.location.href = `/notes/${note.id}`;
        });

        sidebarNotes.appendChild(div);
    });
}

function loadNote(noteId) {
    window.location.href = `/notes/${noteId}`;
}

async function loadFirstNote() {
    const response = await fetch('/notes/list-data', { credentials: "include" });
    if (!response.ok) {
        console.error("ノート一覧取得失敗");
        return;
    }
    const notes = await response.json();
    if (notes.length === 0) {
        location.reload(); // ノートゼロ件ならリロードして「ノートがありません」表示に
        return;
    }
    const firstNoteId = notes.find(n => n.sortOrder === 0)?.id || notes[0].id;
    window.location.href = `/notes/${firstNoteId}`;
}

// 簡易的な実装。最終的にはドラッグ操作で並べ替えできるようにする
async function moveNote(noteId, direction) {
	const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
	const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
	
    const response = await fetch(`/notes/${noteId}/move?direction=${direction}`, {
        method: "POST",
		headers: {
		    [csrfHeader]: csrfToken
		},
        credentials: "include"
    });

    if (!response.ok) {
        console.error("ノート移動失敗");
        return;
    }

    await getAllNotes(); // 成功したらノート一覧リロード
}