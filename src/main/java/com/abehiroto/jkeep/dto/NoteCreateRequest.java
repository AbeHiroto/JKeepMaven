package com.abehiroto.jkeep.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteCreateRequest {
    private String title;
    private String content;
    
    // ゲッター・セッター
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
