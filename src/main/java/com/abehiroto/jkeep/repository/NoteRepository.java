package com.abehiroto.jkeep.repository;

// import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
// ※誤ったインポート！！※import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.abehiroto.jkeep.bean.Note;
import com.abehiroto.jkeep.bean.User;


public interface NoteRepository extends JpaRepository<Note, Long> {

    // 一括更新クエリ
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Note n SET n.order = n.order + 1 WHERE n.user.id = :userId")
    int incrementAllOrdersByUser(@Param("userId") Long userId);
    
    List<Note> findByUserOrderByOrderAsc(User user);
    
    Optional<Note> findByIdAndUserUsername(Long id, String username);

}