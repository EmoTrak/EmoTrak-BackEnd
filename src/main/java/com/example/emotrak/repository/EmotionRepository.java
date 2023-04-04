package com.example.emotrak.repository;

import com.example.emotrak.entity.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionRepository extends JpaRepository<Emotion,Long> {
    Optional<Emotion> findById(Long id);
}
