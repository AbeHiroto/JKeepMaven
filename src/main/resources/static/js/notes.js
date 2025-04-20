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

    cancelBtn.addEventListener('click', async () => {
        const title = document.getElementById('new-title').value.trim();
        const content = document.getElementById('new-content').value.trim();
        if (title || content) {
            await saveNewNote(title, content);
        }
        await getAllNotes(); // メモ一覧更新
        newNoteArea.style.display = 'none';
        mainArea.style.display = 'block';
    });

    saveBtn.addEventListener('click', async () => {
        const title = document.getElementById('new-title').value.trim();
        const content = document.getElementById('new-content').value.trim();
        await saveNewNote(title, content);
        await getAllNotes();
        newNoteArea.style.display = 'none';
        mainArea.style.display = 'block';
    });

    async function saveNewNote(title, content) {
        const response = await fetch('/notes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ title, content })
        });
        if (!response.ok) {
            alert("ノートの保存に失敗しました");
        }
    }

    async function getAllNotes() {
        // ページをリロードするか、ノート一覧を動的に再描画
        location.reload(); // 簡易的にリロードでもOK
    }