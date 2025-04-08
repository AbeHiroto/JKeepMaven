package com.abehiroto.jkeep.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

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
	
	private User createTestUser() {
	    return User.builder().id(1L).username("test").password("password").build();
	}

	private Note createTestNote(String title) {
	    return Note.builder().title(title).build();
	}

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
    void shouldSetDefaultTitleWhenTitleIsBlank() {
        User user = createTestUser();
        Note note = Note.builder().title("   ").build();
        
        Note result = noteService.saveNewNote(note, user);
        assertEquals("無題のメモ", result.getTitle());
    }
    
//    // 空白の自動削除
//    @Test
//    void shouldTrimContentWhitespace() {
//        User user = createTestUser();
//        Note note = Note.builder()
//            .title("Test")
//            .content("  Hello World  ")
//            .build();
//        
//        Note result = noteService.saveNewNote(note, user);
//        assertEquals("Hello World", result.getContent());
//    }

    @Test
    void shouldRejectNullNote() {
        assertThrows(IllegalArgumentException.class, () -> {
            noteService.saveNewNote(null, createTestUser());
        });
    }
    
    @Test
    void shouldHandleNullContent() {
        User user = createTestUser();
        Note note = Note.builder().title("タイトル").content(null).build();
        
        Note result = noteService.saveNewNote(note, user);
        assertTrue(result.getContent().isEmpty());
    }
    
    @Test
    void shouldAllowEmptyContent() {
        User user = createTestUser();
        Note note = Note.builder().title("タイトル").content("").build();
        
        Note result = noteService.saveNewNote(note, user);
        assertTrue(result.getContent().isEmpty());
    }
    
    @Test
    void shouldTrimWhitespaceOnlyContent() {
        User user = createTestUser();
        Note note = Note.builder()
            .title("タイトル")
            .content("   ") // 半角スペースのみ
            .build();
        
        Note result = noteService.saveNewNote(note, user);
        assertTrue(result.getContent().isEmpty());
    }
    
    @Test
    void shouldIncrementOrderWhenNoteExists() {
        User user = User.builder().id(1L).build();
        Note existingNote = Note.builder().id(1L).order(0).user(user).build();
        
        when(noteRepository.findByUserOrderByOrderAsc(user))
            .thenReturn(List.of(existingNote));
        
        Note newNote = Note.builder().title("New Note").build();
        noteService.saveNewNote(newNote, user);
        
        verify(noteRepository).saveAll(argThat((List<Note> notes) ->
        notes.get(0).getOrder() == 1 && notes.size() == 1
        ));
        // ↓型が明示されていないためエラー↑で型明示
//        verify(noteRepository).saveAll(argThat(notes ->
//            notes.get(0).getOrder() == 1 &&
//            notes.size() == 1
//        ));
    }

    @Test
    void shouldThrowExceptionWhenNoteIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            noteService.saveNewNote(null, User.builder().build());
        });
    }
}