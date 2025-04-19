package com.abehiroto.jkeep.dto;

import com.abehiroto.jkeep.bean.Note;
import java.util.Optional;

public class NoteDtoAssembler {
	
	public static NoteSummaryDTO toDto(Note note) {
        String originalContent = Optional.ofNullable(note.getContent()).orElse("");
        String summaryContent;

        if (note.getOrder() != null && note.getOrder() == 0) {
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
                .order(note.getOrder())
                .build();
    }
}
