package com.example.emotrak.repository;

import com.example.emotrak.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByKakaoId(Long id);

    @Query(value = " SELECT COALESCE(MAX(SUBSTRING_INDEX(nickname, '_', -1)), 0) + 1"
                 + "   FROM users"
                 + "  WHERE SUBSTRING_INDEX(nickname, '_', 1) = :nickname"
                 + "    AND nickname <> :nickname ")
    Long getKakaoName(@Param("nickname") String nickname);

    Optional<User> findByNaverId(String naverId);

    @Query(value = " SELECT COALESCE(MAX(SUBSTRING_INDEX(nickname, '_', -1)), 0) + 1"
            + "   FROM users"
            + "  WHERE SUBSTRING_INDEX(nickname, '_', 1) = :nickname"
            + "    AND nickname <> :nickname ",
            nativeQuery = true) //nativeQuery = true를 추가
    Long getNaverName(@Param("nickname") String nickname);

}