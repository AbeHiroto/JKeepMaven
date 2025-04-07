package com.abehiroto.jkeep.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
        // テストデータ準備（Builderパターン使用）
        User user = User.builder()
            .id(1L)
            .username("user1")
            .password("password")
            .build();
        
        Note newNote = Note.builder()
            .title("Test Title")
            .content("Test Content")
            .build();

        // モック設定
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
            Note saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // テスト実行
        Note result = noteService.saveNewNote(newNote, user);

        // 検証
        assertAll(
            () -> assertEquals(0, result.getOrder()),
            () -> assertNotNull(result.getLastEdited()),
            () -> assertEquals(user, result.getUser())
        );
    }
}