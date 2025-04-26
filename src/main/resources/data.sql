INSERT INTO users (id, username, password) VALUES 
    (1, 'user1', '$2a$10$xLH7A3...'),  -- パスワードはBCryptでハッシュ化
    (2, 'user2', '$2a$10$yTKR8B...'),
    (3, 'user3', '$2a$10$abcdef...');

-- メモデータにuser_idを指定
INSERT INTO note (title, content, user_id, last_edited, sort_order) VALUES
    ('買い物リスト1', '卵・牛乳, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test', 1, CURRENT_TIMESTAMP, 0),
    ('買い物リスト2', '卵・牛乳, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test', 1, CURRENT_TIMESTAMP, 1),
    ('買い物リスト3', '卵・牛乳, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test', 1, CURRENT_TIMESTAMP, 2),
    ('買い物リスト4', '卵・牛乳, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test', 1, CURRENT_TIMESTAMP, 3),
    ('買い物リスト5', '卵・牛乳, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test', 1, CURRENT_TIMESTAMP, 4),
    ('買い物リスト6', '卵・牛乳, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test, test', 1, CURRENT_TIMESTAMP, 5),
    ('プロジェクトタスク', 'API実装', 2, CURRENT_TIMESTAMP, 2),
    ('アイデア', '新機能のUI', 3, CURRENT_TIMESTAMP, 3);