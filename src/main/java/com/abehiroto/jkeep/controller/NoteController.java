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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
    public String showNoteList(Model model) {
        User currentUser = getCurrentAuthenticatedUser();
        List<NoteSummaryDTO> noteSummaries = noteService.getAllNotes(currentUser);
        model.addAttribute("notes", noteSummaries);
        model.addAttribute("username", currentUser.getUsername()); // ユーザー名をモデルに追加 (ヘッダー表示用など)
        model.addAttribute("newNote", new Note()); // ★空のNoteオブジェクトをモデルに追加 (フォームの th:object 用)
        return "notes/list";
    }

    // @PostMapping の createNote メソッドでも同様に User を取得して saveNewNote に渡す
    /**
     * 新しいノートを作成する処理 (POST /notes)
     * @param note フォームからバインドされた Note オブジェクト (title, content などが含まれる)
     * @return 一覧表示へのリダイレクトパス
     */
    @PostMapping // POST /notes リクエストを処理
    public String createNote(@ModelAttribute("newNote") Note note) { // ★th:object と合わせる
        // 1. 現在認証されているユーザーを取得
        User currentUser = getCurrentAuthenticatedUser();

        // 2. Service を呼び出してノートを保存
        //    Note オブジェクトにはフォームからの title, content が入っている
        //    User オブジェクトは Controller で取得したものを渡す
        noteService.saveNewNote(note, currentUser);

        // 3. ノート一覧表示ページにリダイレクト
        return "redirect:/notes";
    }

    // --- 認証ユーザー取得ヘルパーメソッド (例) ---
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User not authenticated"); // またはログインページへリダイレクトなど
        }

        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        return userRepository.findByUsername(username)
               .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
//@Controller
//@RequestMapping("/notes")
//public class NoteController {
//    private final NoteService noteService;
//
//    public NoteController(NoteService noteService) {
//        this.noteService = noteService;
//    }
//
//    @GetMapping
//    public String showNoteList(Model model) {
//        model.addAttribute("notes", noteService.getAllNotes());
//        return "notes/list";  // Thymeleafテンプレートを指定
//    }
//
////    @PostMapping
////    public String createNote(@ModelAttribute Note note) {
////        noteService.saveNote(note);
////        return "redirect:/notes";  // 一覧画面にリダイレクト
////    }
//}