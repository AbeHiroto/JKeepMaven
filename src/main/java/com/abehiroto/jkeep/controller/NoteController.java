package com.abehiroto.jkeep.controller;

import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;
import com.abehiroto.jkeep.dto.NoteCreateRequest;
import com.abehiroto.jkeep.dto.NoteSummaryDTO;
import com.abehiroto.jkeep.service.NoteService;
import com.abehiroto.jkeep.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequestMapping("/notes")
public class NoteController {
    private final NoteService noteService;
    private final UserRepository userRepository; // UserRepository をインジェクション

    // コンストラクタで UserRepository も受け取る
    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showNoteList(Model model, @AuthenticationPrincipal UserDetails userDetails) { // ★ 引数変更
        // --- 認証ユーザーを取得 (引数から) ---
        if (userDetails == null) {
            throw new IllegalStateException("User details not found in security context.");
        }
        String username = userDetails.getUsername();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // --- Service メソッド呼び出し ---
        List<NoteSummaryDTO> noteSummaries = noteService.getAllNotes(currentUser);

        model.addAttribute("notes", noteSummaries);
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("newNote", new Note());
        return "notes/list";
    }

    @PostMapping
    public String createNote(@ModelAttribute("newNote") Note note, @AuthenticationPrincipal UserDetails userDetails) { // ★ 引数変更
        // --- 認証ユーザーを取得 (引数から) ---
        if (userDetails == null) {
            throw new IllegalStateException("User details not found in security context.");
        }
        String username = userDetails.getUsername();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // --- Service を呼び出してノートを保存 ---
        noteService.saveNewNote(note, currentUser);

        // --- 一覧表示ページにリダイレクト ---
        return "redirect:/notes";
    }
    
    @RestController
    @RequestMapping("/api/notes")
    public class NoteRestController {

        private final NoteService noteService;
        private final UserRepository userRepository;

        public NoteRestController(NoteService noteService, UserRepository userRepository) {
            this.noteService = noteService;
            this.userRepository = userRepository;
        }

        @PostMapping
        public ResponseEntity<?> createNoteFromJson(
                @RequestBody NoteCreateRequest request,
                @AuthenticationPrincipal UserDetails userDetails) {

            if (userDetails == null) {
                return ResponseEntity.status(401).body("ログインユーザーが見つかりません");
            }

            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + username));

            Note note = new Note();
            note.setTitle(request.getTitle());
            note.setContent(request.getContent());

            Note savedNote = noteService.saveNewNote(note, user);

            return ResponseEntity.ok().build(); // 必要に応じてsavedNoteの情報を返す
        }
    }
}