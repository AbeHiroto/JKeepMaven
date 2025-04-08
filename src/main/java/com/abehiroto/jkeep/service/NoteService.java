package com.abehiroto.jkeep.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;
import com.abehiroto.jkeep.repository.NoteRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note saveNewNote(Note note, User user) {
        if (note == null) {
            throw new IllegalArgumentException("メモデータ必須");
        }

        // タイトルと本文のデフォルト設定
        String title = (note.getTitle() == null || note.getTitle().isBlank()) ? 
            "無題のメモ" : note.getTitle().trim();
        
        // 変更後（より意図が明確）
        String content = Optional.ofNullable(note.getContent())
                               .map(String::trim)
                               .orElse("");
//        // 変更前
//        String content = note.getContent() != null ? note.getContent().trim() : "";

        // 既存メモのorder調整
        List<Note> existingNotes = noteRepository.findByUserOrderByOrderAsc(user);
        if (!existingNotes.isEmpty()) {
            existingNotes.forEach(n -> n.setOrder(n.getOrder() + 1));
            noteRepository.saveAll(existingNotes);
        }

        // 新規メモの作成
        Note newNote = Note.builder()
            .title(title)
            .content(content)
            .user(user)
            .order(0)
            .lastEdited(LocalDateTime.now())
            .build();

        return noteRepository.save(newNote);
    }
}