package com.example.emotrak.repository;

import com.example.emotrak.entity.Daily;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Daily, Long> {
}
