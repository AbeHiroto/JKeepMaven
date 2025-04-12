package com.abehiroto.jkeep.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.context.annotation.Import; // TestConfig を読み込む場合
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// import com.abehiroto.jkeep.config.TestConfig;
import com.abehiroto.jkeep.bean.User;
import com.abehiroto.jkeep.repository.NoteRepository;
import com.abehiroto.jkeep.repository.UserRepository;

@SpringBootTest // Spring Boot アプリケーションコンテキストをロード
@AutoConfigureMockMvc // MockMvc を自動設定
@ActiveProfiles("test") // テスト用プロファイルを有効化
// @Import(TestConfig.class) // もし TestConfig の Bean (Clockなど) が必要ならインポート
class NoteControllerTest {

	@Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private NoteRepository noteRepository;
    
    @BeforeEach // ← 各テストメソッド実行前にこのメソッドを実行
    void setUp() {
        // データのクリア (NoteがUserを参照しているので先に削除)
        noteRepository.deleteAll();
        userRepository.deleteAll();

        // テストユーザーの準備
        User testUser = User.builder()
            .username("testuser")
            // ↓ insert-test-user.sql にあったハッシュ化済みパスワードを使用
            .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeijv.G2gUiMVutwf7KIjVGO.PXPKz3Gm")
            .build();
        userRepository.save(testUser); // DBに保存 (IDは自動採番される)

        // 必要であれば、このユーザーに紐づくNoteデータもここで準備できる
        // Note note1 = Note.builder().user(testUser).title("...").build();
        // noteRepository.save(note1);
    }

    @Test
    void accessToNotesWithoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/notes"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void accessToH2ConsoleWithoutAuthentication_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/h2-console/"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser") // 簡単な認証済みテスト
    void accessToNotesWithMockUser_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/notes"))
               .andExpect(status().isOk());
    }
    
    @Test
    void accessToNotesWithInvalidHttpBasic_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/notes").with(httpBasic("invaliduser", "wrongpassword")))
               .andExpect(status().isUnauthorized());
    }

    // 他のテストケース...
}