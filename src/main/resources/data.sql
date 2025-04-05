INSERT INTO user (id, username, password) VALUES 
    (1, 'user1', '$2a$10$xLH7A3...'),  -- パスワードはBCryptでハッシュ化
    (2, 'user2', '$2a$10$yTKR8B...');

-- メモデータにuser_idを指定
INSERT INTO note (title, content, user_id, last_edited, sort_order) VALUES
    ('買い物リスト', '卵・牛乳', 1, CURRENT_TIMESTAMP, 1),
    ('プロジェクトタスク', 'API実装', 1, CURRENT_TIMESTAMP, 2),
    ('アイデア', '新機能のUI', 2, CURRENT_TIMESTAMP, 1);