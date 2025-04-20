package com.abehiroto.jkeep.service;


import java.time.LocalDateTime;
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
// import com.abehiroto.jkeep.repository.UserRepository;
import com.abehiroto.jkeep.repository.NoteRepository;
import com.abehiroto.jkeep.dto.NoteSummaryDTO;
import com.abehiroto.jkeep.dto.NoteDtoAssembler;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class NoteService {
    private final NoteRepository noteRepository;
    // private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        // this.userRepository = userRepository;
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
            .order(0)
            .lastEdited(LocalDateTime.now())
            .build();

        return noteRepository.save(newNote);
    }
    
    public List<NoteSummaryDTO> getAllNotes(User user) {

        // 3. ユーザーに紐づくノートを order 昇順で取得
        List<Note> notes = noteRepository.findByUserOrderByOrderAsc(user);

        // 4. Note リストを NoteSummaryDTO リストに変換（コンテンツ加工含む）
        return notes.stream()
                .map(NoteDtoAssembler::toDto)
                .collect(Collectors.toList());
//        return notes.stream()
//                .map(this::convertToDto) // 各 Note を DTO に変換
//                .collect(Collectors.toList());
    }
}