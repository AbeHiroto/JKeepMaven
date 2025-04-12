package com.abehiroto.jkeep.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
// Lombok を使うか、手動で Getter/Setter/コンストラクタを定義

@Getter
@Setter
@Builder // Builderパターンを使うと便利
public class NoteSummaryDTO {
    private Long id;
    private String title;
    private String summaryContent; // 加工後のコンテンツ or 全文
    private LocalDateTime lastEdited;
    private Integer order;
    // User情報は通常、一覧表示DTOには含めません
}