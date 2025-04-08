package com.abehiroto.jkeep.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;
import com.abehiroto.jkeep.repository.NoteRepository;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void saveNewNote_ShouldSetDefaultValues() {
        // 初期設定
        User user = User.builder().id(1L).build();
        Note newNote = Note.builder().title("Test").build();
        
        when(noteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // 実行 & 検証
        Note result = noteService.saveNewNote(newNote, user);
        
        assertAll(
            () -> assertEquals(0, result.getOrder()),
            () -> assertEquals(user, result.getUser()),
            () -> assertNotNull(result.getLastEdited())
        );
    }
    
    @Test
    void shouldIncrementOrderWhenNoteExists() {
        User user = User.builder().id(1L).build();
        Note existingNote = Note.builder().id(1L).order(0).user(user).build();
        
        when(noteRepository.findByUserOrderByOrderAsc(user))
            .thenReturn(List.of(existingNote));
        
        Note newNote = Note.builder().title("New Note").build();
        noteService.saveNewNote(newNote, user);
        
        verify(noteRepository).saveAll(argThat(notes ->
            notes.get(0).getOrder() == 1 &&
            notes.size() == 1
        ));
    }

    @Test
    void shouldThrowExceptionWhenNoteIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            noteService.saveNewNote(null, User.builder().build());
        });
    }
}