package com.abehiroto.jkeep.controller;

import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;
import com.abehiroto.jkeep.dto.NoteCreateRequest;
import com.abehiroto.jkeep.repository.UserRepository;
import com.abehiroto.jkeep.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

        noteService.saveNewNote(note, user);

        return ResponseEntity.ok().build(); // 必要に応じてsavedNoteの情報を返す
    }
}
