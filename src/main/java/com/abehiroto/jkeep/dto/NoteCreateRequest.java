package com.abehiroto.jkeep.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteCreateRequest {
    private String title;
    private String content;
}
