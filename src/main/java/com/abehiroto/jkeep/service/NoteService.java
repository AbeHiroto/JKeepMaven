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
    
    /**
     * 現在認証されているユーザーのノート一覧（サマリー）を取得する。
     * @return ノートサマリーDTOのリスト
     */
    public List<NoteSummaryDTO> getAllNotes(User user) {
    	// 認証ユーザーの取得はController層に移動

        // 3. ユーザーに紐づくノートを order 昇順で取得
        List<Note> notes = noteRepository.findByUserOrderByOrderAsc(user);

        // 4. Note リストを NoteSummaryDTO リストに変換（コンテンツ加工含む）
        return notes.stream()
                .map(this::convertToDto) // 各 Note を DTO に変換
                .collect(Collectors.toList());
    }

    // --- Helper Methods ---

    private NoteSummaryDTO convertToDto(Note note) {
        String summaryContent;
        String originalContent = Optional.ofNullable(note.getContent()).orElse(""); // Nullチェック

        if (note.getOrder() != null && note.getOrder() == 0) {
            // order が 0 なら全文
            summaryContent = originalContent;
        } else {
            // order が 0 以外なら加工 (例: 最初の1行、または50文字)
            // 例1: 最初の1行を取得
            // summaryContent = originalContent.lines().findFirst().orElse("");

            // 例2: 最初の28文字を取得 (+ "..." を付ける)
             int maxLength = 28;
             if (originalContent.length() <= maxLength) {
                 summaryContent = originalContent;
             } else {
                 summaryContent = originalContent.substring(0, maxLength) + "...";
             }
        }

        return NoteSummaryDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .summaryContent(summaryContent) // 加工後のコンテンツ
                .lastEdited(note.getLastEdited())
                .order(note.getOrder())
                .build();
    }

//    /**
//     * Spring Security コンテキストから現在のユーザー名を取得する。
//     */
//    private String getCurrentUsername() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
//            // 適切な例外処理またはエラーハンドリング
//            throw new IllegalStateException("User not authenticated");
//        }
//
//        Object principal = authentication.getPrincipal();
//        if (principal instanceof UserDetails) {
//            return ((UserDetails) principal).getUsername();
//        } else if (principal instanceof String) {
//            // Principal が単なる文字列の場合 (例: テストなど)
//            return (String) principal;
//        }
//        // 想定外の Principal タイプの場合
//        // ここは User エンティティを直接 Principal として使う実装など、
//        // アプリケーションの認証実装に合わせて調整が必要
//        throw new IllegalStateException("Cannot determine username from principal type: " + principal.getClass());
//    }
}