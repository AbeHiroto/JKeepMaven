package com.abehiroto.jkeep.controller;

import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;
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

//    // --- @AuthenticationPrincipalで置き換え ---
//    private User getCurrentAuthenticatedUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
//            throw new IllegalStateException("User not authenticated"); // またはログインページへリダイレクトなど
//        }
//
//        String username;
//        Object principal = authentication.getPrincipal();
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails) principal).getUsername();
//        } else if (principal instanceof String) {
//            username = (String) principal;
//        } else {
//            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
//        }
//
//        return userRepository.findByUsername(username)
//               .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//    }
}