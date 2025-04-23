package com.abehiroto.jkeep.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NoteDetailDTO {
    private Long id;
    private String title;
    private String content; // ← 本文全体
    private LocalDateTime lastEdited;
    private Integer order;
}