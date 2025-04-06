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
        // テストデータ準備
        User user = new User(1L, "user1", "password");
        Note newNote = new Note();
        newNote.setTitle("Test Title");
        newNote.setContent("Test Content");

        // モック設定
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
            Note saved = invocation.getArgument(0);
            saved.setId(1L); // 保存時にIDがセットされると仮定
            return saved;
        });

        // テスト実行
        Note result = noteService.saveNewNote(newNote, user);

        // 検証
        assertAll(
            () -> assertEquals(0, result.getOrder(), "新規メモのsort_orderは0"),
            () -> assertNotNull(result.getLastEdited(), "最終更新日時が設定されている"),
            () -> assertEquals(user, result.getUser(), "ユーザーが紐づいている")
        );
    }
}