package com.abehiroto.jkeep.service;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;
import com.abehiroto.jkeep.repository.UserRepository;
import com.abehiroto.jkeep.repository.NoteRepository;
import com.abehiroto.jkeep.dto.NoteSummaryDTO;
import com.abehiroto.jkeep.dto.NoteDtoAssembler;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }
    
    public Note saveNewNote(Note note, User user) {
    	if (note == null) throw new IllegalArgumentException("Note must not be null");

        // タイトルと本文のデフォルト設定
        String title = (note.getTitle() == null || note.getTitle().isBlank()) ? 
            "無題のメモ" :
            	note.getTitle().trim().substring(0, Math.min(255, note.getTitle().trim().length()));
        
        // 変更後（より意図が明確）
        String content = Optional.ofNullable(note.getContent())
                               .map(String::trim)
                               .orElse("");

        // 既存メモのorder調整
        noteRepository.incrementAllOrdersByUser(user.getId());

        // 新規メモの作成
        Note newNote = Note.builder()
            .title(title)
            .content(content)
            .user(user)
            .sortOrder(0)
            .lastEdited(LocalDateTime.now())
            .build();

        return noteRepository.save(newNote);
    }
    
    public Optional<Note> getNoteByIdAndUsername(Long id, String username) {
        return noteRepository.findByIdAndUserUsername(id, username);
    }

    
    public List<Note> getAllNotesByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(noteRepository::findByUserAndActiveTrueOrderBySortOrderAsc)
            .orElse(Collections.emptyList());
    }
    
    public Note editNote(Long noteId, String newTitle, String newContent, String username) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("ノートが存在しません"));

        // 所有者の確認
        if (!note.getUser().getUsername().equals(username)) {
            throw new SecurityException("このノートを編集する権限がありません");
        }

        note.setTitle(newTitle);
        note.setContent(newContent);
        return noteRepository.save(note);
    }
    
    public List<Note> findByUserOrderByOrderAsc(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
        return noteRepository.findByUserAndActiveTrueOrderBySortOrderAsc(user);
    }

    
    // 初期画面読み込み時用
    public List<NoteSummaryDTO> getAllNotes(User user) {

        // 3. ユーザーに紐づくノートを order 昇順で取得
        List<Note> notes = noteRepository.findByUserAndActiveTrueOrderBySortOrderAsc(user);

        // 4. Note リストを NoteSummaryDTO リストに変換（コンテンツ加工含む）
        return notes.stream()
                .map(NoteDtoAssembler::toDto)
                .collect(Collectors.toList());
//        return notes.stream()
//                .map(this::convertToDto) // 各 Note を DTO に変換
//                .collect(Collectors.toList());
    }
    
    public Optional<Note> findFirstNoteBySortOrder(User user) {
        return noteRepository.findFirstByUserAndActiveTrueOrderBySortOrderAsc(user);
    }

    // ノート選択時に一覧を取得
    public List<NoteSummaryDTO> getNotesForUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
        return noteRepository.findByUserAndActiveTrueOrderBySortOrderAsc(user).stream()
            .map(NoteDtoAssembler::toDto)
            .collect(Collectors.toList());
    }
    
    public Note getNoteByIdAndUser(Long id, String username) {
        return noteRepository.findByIdAndUserUsername(id, username)
            .orElseThrow(() -> new IllegalArgumentException("該当するノートが見つかりません"));
    }
    
    @Transactional
    public void moveNoteToTrash(Long noteId) {
        // 1. 対象ノートを取得
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new RuntimeException("Note not found"));

        int oldSortOrder = note.getSortOrder();

        // 2. ノートのactiveとsortOrderを更新
        note.setActive(false);
        note.setSortOrder(1000);
        noteRepository.save(note);

        // 3. 他のノートのsort_orderを1減らす
        noteRepository.decrementSortOrdersAfter(oldSortOrder);
    }
    
    @Transactional
    public void moveNote(Long noteId, String direction) {
        Note targetNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("ノートが見つかりません"));

        int currentOrder = targetNote.getSortOrder();
        int newOrder = "up".equals(direction) ? currentOrder - 1 : currentOrder + 1;

        // 境界チェック（必要に応じて）
        if (newOrder < 0) {
            return; // 最上部より上には行かせない
        }

        // 同じsort_orderを持つノートを探す
        Optional<Note> conflictNoteOpt = noteRepository.findBySortOrder(newOrder);
        if (conflictNoteOpt.isPresent()) {
            Note conflictNote = conflictNoteOpt.get();
            conflictNote.setSortOrder(currentOrder);
            noteRepository.save(conflictNote);
        }

        targetNote.setSortOrder(newOrder);
        noteRepository.save(targetNote);
    }
}