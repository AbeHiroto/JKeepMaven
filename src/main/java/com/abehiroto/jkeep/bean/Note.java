package com.abehiroto.jkeep.bean;

import jakarta.persistence.*;
// import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
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
    private Integer sortOrder = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true; // true: アクティブ, false: ゴミ箱

    @Column(nullable = false)
    @Builder.Default
    private boolean pinned = false; // true: ピン留め

    @Column(length = 20)
    private String color; // 例: "red", "blue", "green"
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @PrePersist
    @PreUpdate
    private void updateTimestamps() {
        this.lastEdited = LocalDateTime.now();
    }
    
//    // ※タイトル空欄時のエラーはここが原因！！！コメントアウトしたコードで正常動作
//    public void setTitle(String title) {
//        this.title = (title == null || title.isBlank()) ? null : title.trim();
//    }
    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("タイトル必須");
        }
        this.title = title;
    }
    
    public void setOrder(Integer order) {
        if (order < 0) throw new IllegalArgumentException("順序は0以上");
        this.sortOrder = order;
    }
    
    // テスト用ファクトリメソッド
    public static Note createTestNote(String title, User user) {
        return builder()
            .title(title)
            .user(user)
            .sortOrder(0)
            .build();
    }
}