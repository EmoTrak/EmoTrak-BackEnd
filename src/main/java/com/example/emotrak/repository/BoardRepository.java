package com.example.emotrak.repository;

import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Daily, Long> {

    void deleteAllByUser(User user);

    @Query(value = " SELECT d.id, d.img_url "
                 + "   FROM daily d"
                 + "  WHERE d.emotion_id in (:emo)"
                 + "    AND d.share = true ", nativeQuery = true)
    List<Object[]> getBoardImages(@Param("emo") List<Long> emoList, Pageable pageable);
}
