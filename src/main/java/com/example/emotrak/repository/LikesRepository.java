package com.example.emotrak.repository;

import com.example.emotrak.entity.Comment;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.Likes;
import com.example.emotrak.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes,Long> {
    Optional<Likes> findByUserAndDaily(User user, Daily daily);
    void deleteByUserAndDaily(User user, Daily daily);

        Optional<Likes> findByUserAndComment(User user, Comment comment);
        void deleteByUserAndComment(User user, Comment comment);
}
