package com.abehiroto.jkeep.repository;

import com.abehiroto.jkeep.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // ユーザー名で検索するメソッド
}