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
    
    @Column(name = "sort_order", nullable = false)  // カラム名を明示的に指定
    private Integer order = 0;  // Javaフィールド名はorderでも可
    
 // ユーザー関連付け（多対1）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
 // 明示的にセッターメソッドを定義（Lombokと共存可能）
    public void setUser(User user) {
        this.user = user;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void setLastEdited(LocalDateTime lastEdited) {
        this.lastEdited = lastEdited;
    }
    
 // 明示的にゲッターを定義（テストで必要になるため）
    public Integer getOrder() {
        return this.order;
    }

    public LocalDateTime getLastEdited() {
        return this.lastEdited;
    }

    public User getUser() {
        return this.user;
    }
}