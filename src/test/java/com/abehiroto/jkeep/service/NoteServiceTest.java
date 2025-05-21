package com.abehiroto.jkeep.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
// import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;
import com.abehiroto.jkeep.repository.NoteRepository;
import com.abehiroto.jkeep.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {	

    @Mock private NoteRepository noteRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private NoteService noteService;

//    // Mockitoによる不要なスタビングの検知のエラーを回避するため各メソッド内に移動
//    @BeforeEach
//    void setUp() {共通モック設定}
    
    private User createTestUser() {
	    return User.builder().id(1L).username("test").password("password").build();
	}

	private Note createTestNote(String title) {
	    return Note.builder().title(title).build();
	}
	
	@Nested
	class SaveNewNoteTest {
		@Test
	    void shouldSetDefaultValues() {
	        // 初期設定
	        User user = User.builder().id(1L).build();
	        Note newNote = Note.builder().title("Test").build();
	        
	        when(noteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

	        // 実行 & 検証
	        Note result = noteService.saveNewNote(newNote, user);
	        
	        assertAll(
	            () -> assertEquals(0, result.getSortOrder()),
	            () -> assertEquals(user, result.getUser()),
	            () -> assertNotNull(result.getLastEdited())
	        );
	    }
	    
	    @Test
	    void shouldSetDefaultTitleWhenTitleIsBlank() {
	        Note note = createTestNote("   ");
	        
	        when(noteRepository.save(any(Note.class)))
	        .thenAnswer(inv -> inv.getArgument(0));
	    
//	        when(noteRepository.findByUserOrderByOrderAsc(any(User.class)))
//	        .thenReturn(List.of());
	        
	        Note result = noteService.saveNewNote(note, createTestUser());
	        assertEquals("無題のメモ", result.getTitle());
	    }
	    
	    @Test
	    void shouldTrimContentWhitespace() {
	        User user = createTestUser();
	        Note note = Note.builder()
	            .title("Test")
	            .content("  Hello World  ")
	            .build();
	        
	        when(noteRepository.save(any(Note.class)))
	        .thenAnswer(inv -> inv.getArgument(0));
	    
//	        when(noteRepository.findByUserOrderByOrderAsc(any(User.class)))
//	        .thenReturn(List.of());
	        
	        Note result = noteService.saveNewNote(note, user);
	        assertEquals("Hello World", result.getContent());
	    }

	    @Test
	    void shouldRejectNullNote() {
	        assertThrows(IllegalArgumentException.class, () -> {
	            noteService.saveNewNote(null, createTestUser());
	        });
	    }
	    
	    @Test
	    void shouldHandleNullContent() {
	        Note note = createTestNote("タイトル").toBuilder()
	            .content(null)
	            .build();
	        
	        when(noteRepository.save(any(Note.class)))
	        .thenAnswer(inv -> inv.getArgument(0));
	    
//	        when(noteRepository.findByUserOrderByOrderAsc(any(User.class)))
//	        .thenReturn(List.of());
	        
	        Note result = noteService.saveNewNote(note, createTestUser());
	        assertTrue(result.getContent().isEmpty());
	    }
	    
	    @Test
	    void shouldAllowEmptyContent() {
	        User user = createTestUser();
	        Note note = Note.builder().title("タイトル").content("").build();
	        
	        when(noteRepository.save(any(Note.class)))
	        .thenAnswer(inv -> inv.getArgument(0));
	    
//	        when(noteRepository.findByUserOrderByOrderAsc(any(User.class)))
//	        .thenReturn(List.of());
	        
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
	        
	        when(noteRepository.save(any(Note.class)))
	        .thenAnswer(inv -> inv.getArgument(0));
	    
//	        when(noteRepository.findByUserOrderByOrderAsc(any(User.class)))
//	        .thenReturn(List.of());
	        
	        Note result = noteService.saveNewNote(note, user);
	        assertTrue(result.getContent().isEmpty());
	    }
	    
	    @Test
	    void shouldIncrementOrderWhenNoteExists() {
	        User user = createTestUser();
	        
	        // モック設定
	        when(noteRepository.incrementAllOrdersByUser(user.getId()))
	            .thenReturn(1); // 更新件数を1と仮定
	        when(noteRepository.save(any(Note.class)))
	        	.thenAnswer(inv -> inv.getArgument(0));
	        
	        Note newNote = Note.builder().title("New Note").build();
	        Note result = noteService.saveNewNote(newNote, user);
	        
	        // 検証
	        verify(noteRepository).incrementAllOrdersByUser(user.getId());
	        assertThat(result.getSortOrder()).isEqualTo(0);
	    }
//	    @Test
//	    void shouldIncrementOrderWhenNoteExists() {
//	        User user = User.builder().id(1L).build();
//	        Note existingNote = Note.builder().id(1L).order(0).user(user).build();
//	        
//	        when(noteRepository.findByUserOrderByOrderAsc(user))
//	            .thenReturn(List.of(existingNote));
//	        
//	        Note newNote = Note.builder().title("New Note").build();
//	        noteService.saveNewNote(newNote, user);
//	        
//	        verify(noteRepository).saveAll(argThat((List<Note> notes) ->
//	        notes.get(0).getOrder() == 1 && notes.size() == 1
//	        ));
//	    }
	    
	    @Test
	    void incrementAllOrdersByUser_shouldReturnUpdateCount() {
	        when(noteRepository.incrementAllOrdersByUser(anyLong()))
	            .thenReturn(2);
	        
	        int updated = noteRepository.incrementAllOrdersByUser(1L);
	        assertThat(updated).isEqualTo(2);
	    }
	    
	    @Test
	    @DisplayName("255文字を超えるタイトルは自動的に切り詰められる")
	    void shouldTruncateOverlengthTitle() {
	        String longTitle = "a".repeat(300);
	        Note note = createTestNote(longTitle);
	        
	        when(noteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
	        
	        Note result = noteService.saveNewNote(note, createTestUser());
	        assertThat(result.getTitle())
	            .hasSize(255)
	            .startsWith("aaaaaaaaaa"); // 切り詰め後の内容を検証
	    }

	    @ParameterizedTest
	    @ValueSource(ints = {255, 256, 1000})
	    @DisplayName("様々な長さのタイトル入力テスト")
	    void shouldHandleVariousTitleLengths(int length) {
	        String title = "a".repeat(length);
	        Note note = createTestNote(title);
	        
	        when(noteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
	        
	        Note result = noteService.saveNewNote(note, createTestUser());
	        assertThat(result.getTitle()).hasSizeLessThanOrEqualTo(255);
	    }
	}
	
	@Nested
	class EditNoteTest {

	    @Test
	    void shouldUpdateNoteWhenUserMatches() {
	        User user = createTestUser();
	        Note existingNote = Note.builder()
	                .id(1L)
	                .title("Old Title")
	                .content("Old Content")
	                .user(user)
	                .build();

	        when(noteRepository.findById(1L)).thenReturn(Optional.of(existingNote));
	        when(noteRepository.save(any(Note.class))).thenAnswer(inv -> inv.getArgument(0));

	        Note updated = noteService.editNote(1L, "New Title", "New Content", "test");

	        assertEquals("New Title", updated.getTitle());
	        assertEquals("New Content", updated.getContent());
	    }

	    @Test
	    void shouldThrowWhenNoteNotFound() {
	        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

	        assertThrows(IllegalArgumentException.class, () ->
	            noteService.editNote(1L, "Title", "Content", "test")
	        );
	    }

	    @Test
	    void shouldThrowWhenUserIsNotOwner() {
	        User user = createTestUser();
	        user.setUsername("owner");
	        Note note = Note.builder().id(1L).user(user).build();

	        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

	        assertThrows(SecurityException.class, () ->
	            noteService.editNote(1L, "X", "Y", "notOwner")
	        );
	    }
	}
	
	@Nested
	class MoveNoteToTrashTest {

	    @Test
	    void shouldMarkNoteAsTrashedAndUpdateOrder() {
	        Note note = Note.builder()
	            .id(1L)
	            .sortOrder(2)
	            .build();

	        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

	        noteService.moveNoteToTrash(1L);

	        assertFalse(note.isActive());
	        assertEquals(1000, note.getSortOrder());
	        verify(noteRepository).decrementSortOrdersAfter(2);
	        verify(noteRepository).save(note);
	    }

	    @Test
	    void shouldThrowWhenNoteNotFound() {
	        when(noteRepository.findById(99L)).thenReturn(Optional.empty());

	        assertThrows(RuntimeException.class, () -> {
	            noteService.moveNoteToTrash(99L);
	        });
	    }
	}
	
	@Nested
	class RestoreNoteTest {

	    @Test
	    void shouldRestoreNoteAndShiftOthers() {
	        User user = createTestUser();
	        Note targetNote = Note.builder()
	                .id(1L)
	                .user(user)
	                .active(false)
	                .sortOrder(1000)
	                .build();

	        Note otherNote = Note.builder()
	                .id(2L)
	                .user(user)
	                .active(true)
	                .sortOrder(0)
	                .build();

	        // ↓不要なスタブのため削除
//	        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
	        when(noteRepository.findByIdAndUserUsername(1L, "test")).thenReturn(Optional.of(targetNote));
	        when(noteRepository.findByUser_UsernameAndActiveTrueOrderBySortOrderAsc("test"))
	            .thenReturn(List.of(otherNote));

	        noteService.restoreNote(1L, "test");

	        assertTrue(targetNote.isActive());
	        assertEquals(0, targetNote.getSortOrder());
	        assertEquals(1, otherNote.getSortOrder()); // +1されているか

	        verify(noteRepository).saveAll(List.of(otherNote));
	        verify(noteRepository).save(targetNote);
	    }
	}
	
	@Nested
	class GetNoteByIdTests {

	    @Test
	    void shouldThrowWhenNoteNotFoundForUser() {
	        when(noteRepository.findByIdAndUserUsername(1L, "test"))
	            .thenReturn(Optional.empty());

	        assertThrows(IllegalArgumentException.class, () -> {
	            noteService.getNoteByIdAndUser(1L, "test");
	        });
	    }
	}

	@Nested
	class GetTrashedNoteByIdTests {

	    @Test
	    void shouldThrowWhenUserNotFound() {
	        when(userRepository.findByUsername("test"))
	            .thenReturn(Optional.empty());

	        assertThrows(UsernameNotFoundException.class, () -> {
	            noteService.getTrashedNoteByIdAndUser(1L, "test");
	        });
	    }

	    @Test
	    void shouldThrowWhenNoteNotInTrash() {
	        User user = createTestUser();
	        when(userRepository.findByUsername("test"))
	            .thenReturn(Optional.of(user));
	        when(noteRepository.findByIdAndUserAndActiveFalse(1L, user))
	            .thenReturn(Optional.empty());

	        assertThrows(EntityNotFoundException.class, () -> {
	            noteService.getTrashedNoteByIdAndUser(1L, "test");
	        });
	    }
	}
}