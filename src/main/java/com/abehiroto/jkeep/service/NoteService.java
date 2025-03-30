package com.abehiroto.jkeep.service;

import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NoteService {
    private final NoteRepository noteRepository;

    // コンストラクタインジェクション
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note saveNote(Note note) {
        note.setLastEdited(LocalDateTime.now());  // 更新日時を自動設定
        return noteRepository.save(note);
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();  // 全件取得
    }
}