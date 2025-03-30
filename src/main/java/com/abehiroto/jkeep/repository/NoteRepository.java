package com.abehiroto.jkeep.repository;

import com.abehiroto.jkeep.bean.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
    // 基本的なCRUD操作はJpaRepositoryが自動提供
    // 例: findAll(), save(), deleteById()など
}