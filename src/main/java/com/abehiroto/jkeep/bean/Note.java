package com.abehiroto.jkeep.bean;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 自動採番ID
    
    @Column(nullable = false)
    private String title;  // メモのタイトル（必須）
    
    @Column(columnDefinition = "TEXT")
    private String content;  // メモ本文（長文対応）
    
    private LocalDateTime lastEdited;  // 最終更新日時
    
    // 明示的にsetterを追加
    public void setLastEdited(LocalDateTime lastEdited) {
        this.lastEdited = lastEdited;
    }
}