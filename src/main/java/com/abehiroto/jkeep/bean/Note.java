package com.abehiroto.jkeep.bean;

import jakarta.persistence.*;
// import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private LocalDateTime lastEdited;
    
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer order = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @PrePersist
    @PreUpdate
    private void updateTimestamps() {
        this.lastEdited = LocalDateTime.now();
    }
    
    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("タイトル必須");
        }
        this.title = title;
    }
    
    public void setOrder(Integer order) {
        if (order < 0) throw new IllegalArgumentException("順序は0以上");
        this.order = order;
    }
    
    // テスト用ファクトリメソッド
    public static Note createTestNote(String title, User user) {
        return builder()
            .title(title)
            .user(user)
            .order(0)
            .build();
    }
}