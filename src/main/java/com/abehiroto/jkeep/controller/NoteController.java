package com.abehiroto.jkeep.controller;

import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;
import com.abehiroto.jkeep.dto.NoteCreateRequest;
import com.abehiroto.jkeep.dto.NoteDetailDTO;
import com.abehiroto.jkeep.dto.NoteDtoAssembler;
import com.abehiroto.jkeep.dto.NoteSummaryDTO;
import com.abehiroto.jkeep.service.NoteService;
import com.abehiroto.jkeep.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final UserRepository userRepository;

    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showDefaultList(Model model, @AuthenticationPrincipal UserDetails userDetails) { // ★ 引数変更
        // --- 認証ユーザーを取得 (引数から) ---
        if (userDetails == null) {
            throw new IllegalStateException("User details not found in security context.");
        }
        String username = userDetails.getUsername();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // ノート一覧の取得
        List<NoteSummaryDTO> noteSummaries = noteService.getAllNotes(currentUser);
        
        // sort_orderが0のノートを取得
        Optional<Note> firstNoteOpt = noteService.findFirstNoteBySortOrder(currentUser);
        NoteDetailDTO selectedNote = null;
        if (firstNoteOpt.isPresent()) {
            selectedNote = NoteDtoAssembler.toDetailDto(firstNoteOpt.get());
        }

        model.addAttribute("notes", noteSummaries);
        model.addAttribute("selectedNote", selectedNote);
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("newNote", new Note());
        return "notes/list";
    }
    
    // 個別のノート読み込み時にサイドバーをロード
    @GetMapping("/list-data")
    @ResponseBody
    public List<NoteSummaryDTO> getNoteList(Principal principal) {
        String username = principal.getName();
        return noteService.getNotesForUser(username);
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
    
    @GetMapping("/{id}")
    public String showNote(@PathVariable Long id, Model model, Principal principal) {
        String username = principal.getName();

        Note note = noteService.getNoteByIdAndUser(id, username);
        NoteDetailDTO selectedNote = NoteDtoAssembler.toDetailDto(note);

        List<NoteSummaryDTO> noteList = noteService.getNotesForUser(username);

        model.addAttribute("selectedNote", selectedNote);
        model.addAttribute("noteList", noteList);
        
        model.addAttribute("newNote", new Note());

        return "notes/list";
    }
    
    @PostMapping("/edit")
    public String updateNote(@RequestParam Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             Principal principal) {
        noteService.editNote(id, title, content, principal.getName());
        return "redirect:/notes/" + id;  // 更新後、そのノートにリダイレクト
    }
    
    @PostMapping("/{noteId}/trash")
    public ResponseEntity<Void> moveNoteToTrash(@PathVariable Long noteId) {
        noteService.moveNoteToTrash(noteId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<?> moveNote(@PathVariable Long id, @RequestParam String direction) {
        noteService.moveNote(id, direction);
        return ResponseEntity.ok().build();
    }

    
    // ※実験用。要別ファイル切り出し
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