// src/test/java/com/abehiroto/jkeep/service/NoteServiceTest.java
package com.abehiroto.jkeep.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class NoteServiceTest {

    @Autowired
    private NoteService noteService;

    @Test
    void testSaveNote() {
        // テストケースをここに実装
    }
}