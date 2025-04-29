package com.abehiroto.jkeep.dto;

import com.abehiroto.jkeep.bean.Note;
import java.util.Optional;

public class NoteDtoAssembler {
	
	public static NoteSummaryDTO toSummaryDto(Note note) {
        String originalContent = Optional.ofNullable(note.getContent()).orElse("");
        String summaryContent;

        if (note.getSortOrder() != null && note.getSortOrder() == 0) {
            summaryContent = originalContent;
        } else {
            int maxLength = 28;
            summaryContent = (originalContent.length() <= maxLength)
                    ? originalContent
                    : originalContent.substring(0, maxLength) + "...";
        }

        return NoteSummaryDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .summaryContent(summaryContent)
                .lastEdited(note.getLastEdited())
                .order(note.getSortOrder())
                .build();
    }
	
    public static NoteDetailDTO toDetailDto(Note note) {
        return NoteDetailDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent()) // 全文をそのまま渡す
                .lastEdited(note.getLastEdited())
                .order(note.getSortOrder())
                .build();
    }
}
