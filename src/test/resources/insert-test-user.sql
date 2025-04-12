-- src/test/resources/insert-test-user.sql
-- id カラムとその値を削除し、DBの自動採番に任せる
INSERT INTO users (username, password) VALUES ('testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeijv.G2gUiMVutwf7KIjVGO.PXPKz3Gm');

-- もしNoteデータも入れる場合、user_id には何が入るか不明になるため注意が必要
-- (テストコード側で User を取得してから Note を作成・保存する方が確実)
-- INSERT INTO note (user_id, title, content, ...) VALUES (?, 'Test Note 1', 'Content 1', ...);