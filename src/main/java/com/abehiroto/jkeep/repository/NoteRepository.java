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
    @Query("UPDATE Note n SET n.sortOrder = n.sortOrder + 1 WHERE n.user.id = :userId")
    int incrementAllOrdersByUser(@Param("userId") Long userId);
    
    // // active関係なくすべてユーザーのノートすべて取得
    // List<Note> findByUserOrderBySortOrderAsc(User user);
    
    // active=trueのみ取得
    List<Note> findByUserAndActiveTrueOrderBySortOrderAsc(User user);
    
    Optional<Note> findByIdAndUserUsername(Long id, String username);
    
    // // active関係なく最初のノート取得
    // Optional<Note> findFirstByUserOrderBySortOrderAsc(User user);
    
    Optional<Note> findFirstByUserAndActiveTrueOrderBySortOrderAsc(User user);
    
    @Modifying
    @Query("UPDATE Note n SET n.sortOrder = n.sortOrder - 1 WHERE n.active = true AND n.sortOrder > :sortOrder")
    void decrementSortOrdersAfter(int sortOrder);

    Optional<Note> findBySortOrder(int sortOrder);
    
    // ゴミ箱内のノート一覧
    List<Note> findByUserAndActiveFalseOrderByLastEditedDesc(User user);

    // 復元処理用：active=trueのノート一覧
    List<Note> findByUser_UsernameAndActiveTrueOrderBySortOrderAsc(String username);
    
    // ゴミ箱メインエリアに表示するノート
    Optional<Note> findTop1ByUserAndActiveFalseOrderByLastEditedDesc(User user);
    
    // ゴミ箱内の特定のノートの内容を取得
    Optional<Note> findByIdAndUserAndActiveFalse(Long id, User user);
}