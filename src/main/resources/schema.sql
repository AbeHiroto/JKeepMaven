-- 既存テーブル削除（オプション）
DROP TABLE IF EXISTS note;

-- テーブル作成
CREATE TABLE IF NOT EXISTS user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE note (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    last_edited TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    sort_order INT NOT NULL DEFAULT 0
    CONSTRAINT fk_note_user FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 複合インデックス追加
CREATE INDEX idx_note_user_sort ON note(user_id, sort_order);