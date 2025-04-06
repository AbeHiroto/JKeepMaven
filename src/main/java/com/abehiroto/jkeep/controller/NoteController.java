package com.abehiroto.jkeep.controller;

import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

//    @GetMapping
//    public String showNoteList(Model model) {
//        model.addAttribute("notes", noteService.getAllNotes());
//        return "notes/list";  // Thymeleafテンプレートを指定
//    }
//
//    @PostMapping
//    public String createNote(@ModelAttribute Note note) {
//        noteService.saveNote(note);
//        return "redirect:/notes";  // 一覧画面にリダイレクト
//    }
}