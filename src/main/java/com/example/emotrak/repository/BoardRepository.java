package com.example.emotrak.repository;

import com.example.emotrak.dto.board.BoardDetailResponseDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Daily, Long> {
    @Query("SELECT new com.example.emotrak.dto.board.BoardDetailResponseDto("
            + "d, "
            + "u, "
            + "d.id, " // 이후 생성자에서 이를 무시하므로 여기서는 d.id를 사용합니다.
            + "CAST((SELECT COUNT(l) FROM Likes l WHERE l.daily.id = :dailyId) AS int), "
            + "(SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Likes l WHERE l.user.id = :userId AND l.daily.id = :dailyId), "
            + "false, " // lastPage는 나중에 설정하므로 여기서는 기본값인 false를 사용합니다.
            + "(SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Report r WHERE r.user.id = :userId AND r.daily.id = :dailyId), "
            + "CAST((SELECT COUNT(c) FROM Comment c WHERE c.daily.id = :dailyId) AS int)) "
            + "FROM Daily d JOIN d.user u WHERE d.id = :dailyId")
    Optional<BoardDetailResponseDto> findBoardDetailWithCommentsByUserAndDaily(@Param("dailyId") Long dailyId,
                                                                               @Param("userId") Long userId);




    // 사용자와 특정 날짜에 해당하는 게시물 수를 검색
    @Query(" SELECT COUNT(d) "
         + "   FROM Daily d "
         + "  WHERE d.user = :user "
         + "    AND d.dailyYear = :year "
         + "    AND d.dailyMonth = :month "
         + "    AND d.dailyDay = :day")
    long countDailyPostsByUserAndDate(@Param("user") User user,
                                      @Param("year") int year,
                                      @Param("month") int month,
                                      @Param("day") int day);

    void deleteAllByUser(User user);

    @Query(value = " SELECT d.id, d.img_url "
                 + "   FROM daily d"
                 + "  WHERE d.emotion_id in (:emo)"
                 + "    AND d.share = true "
                 + "  ORDER BY d.created_at desc ", nativeQuery = true)
    List<Object[]> getBoardImagesRecent(@Param("emo") List<Long> emoList, Pageable pageable);

    @Query(value = " SELECT d.id, d.img_url"
                 + "   FROM daily d left join likes l on d.id = l.daily_id"
                 + "  WHERE d.emotion_id in (:emo)"
                 + "    AND d.share = true "
                 + "  GROUP BY d.id, d.img_url"
                 + "  ORDER BY count(l.daily_id)  desc, d.created_at desc", nativeQuery = true)
    List<Object[]> getBoardImagesPopular(@Param("emo") List<Long> emoList, Pageable pageable);
}
