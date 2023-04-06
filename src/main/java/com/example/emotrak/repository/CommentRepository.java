package com.example.emotrak.repository;

import com.example.emotrak.entity.Comment;
import com.example.emotrak.entity.Daily;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByDaily(Daily daily, Pageable pageable);
}
