package com.example.emotrak.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Nested
@DisplayName("Daily Entity")
class DailyTest {
    @Test
    @DisplayName("@AllArgsConstructor 테스트")
    public void DailyAllArgs() {
        User user = new User();
        user.setId(1L);

        Emotion emotion = new Emotion();
        emotion.setId(1L);

        Daily daily = new Daily(1L, 2023, 5, 1, emotion, user, "내용", 5, "img", false, false, false);
        assertEquals(1L, daily.getId());
        assertEquals(2023, daily.getDailyYear());
        assertEquals(5, daily.getDailyMonth());
        assertEquals(1, daily.getDailyDay());
        assertEquals(emotion, daily.getEmotion());
        assertEquals(user, daily.getUser());
        assertEquals("내용", daily.getDetail());
        assertEquals(5, daily.getStar());
        assertEquals("img", daily.getImgUrl());
        assertEquals(false, daily.isShare());
        assertEquals(false, daily.isHasRestrict());
        assertEquals(false, daily.isDraw());
    }
    @Test
    @DisplayName("@Setter 테스트")
    public void DailySetter() {
        User user = new User();
        user.setId(1L);
        Emotion emotion = new Emotion();
        emotion.setId(2L);

        Daily daily = new Daily();
        daily.setId(2L);
        daily.setDailyYear(2024);
        daily.setDailyMonth(6);
        daily.setDailyDay(2);
        daily.setEmotion(emotion);
        daily.setUser(user);
        daily.setDetail("내용이다.");
        daily.setStar(1);
        daily.setImgUrl("imgUrl");
        daily.setShare(true);
        daily.setHasRestrict(true);
        daily.setDraw(true);

        assertEquals(2L, daily.getId());
        assertEquals(2024, daily.getDailyYear());
        assertEquals(6, daily.getDailyMonth());
        assertEquals(2, daily.getDailyDay());
        assertEquals(emotion, daily.getEmotion());
        assertEquals(user, daily.getUser());
        assertEquals("내용이다.", daily.getDetail());
        assertEquals(1, daily.getStar());
        assertEquals("imgUrl", daily.getImgUrl());
        assertEquals(true, daily.isShare());
        assertEquals(true, daily.isHasRestrict());
        assertEquals(true, daily.isDraw());
    }
}