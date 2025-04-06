package com.abehiroto.jkeep.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;
import com.abehiroto.jkeep.repository.NoteRepository;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note saveNewNote(Note note, User user) {
        note.setUser(user);
        note.setOrder(0); // 新規メモは常に先頭
        note.setLastEdited(LocalDateTime.now());
        return noteRepository.save(note);
    }
}