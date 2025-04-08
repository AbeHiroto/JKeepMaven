package com.abehiroto.jkeep.repository;

import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
    // 基本的なCRUD操作はJpaRepositoryが自動提供
    // 例: findAll(), save(), deleteById()など
	// ユーザーに紐づくノートをorderの昇順で取得
    List<Note> findByUserOrderByOrderAsc(User user);
}