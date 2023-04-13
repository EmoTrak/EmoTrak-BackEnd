package com.example.emotrak.util;

import com.example.emotrak.dto.BoardRequestDto;
import com.example.emotrak.entity.*;
import com.example.emotrak.repository.DailyRepository;
import com.example.emotrak.repository.EmotionRepository;
import com.example.emotrak.repository.MonthRepository;
import com.example.emotrak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MonthRepository monthRepository;
    private final EmotionRepository emotionRepository;
    private final DailyRepository dailyRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        User testUser1 = new User(passwordEncoder.encode("jingulee123"), "jingulee@naver.com", "jingulee", UserRoleEnum.USER);
        User testUser2 = new User(passwordEncoder.encode("jingulee123"), "jingulee2@naver.com", "jingulee2", UserRoleEnum.USER);
        User testUser3 = new User(passwordEncoder.encode("jingulee123"), "tester@naver.com", "tester", UserRoleEnum.USER);
        User testAdminUser1 = new User(passwordEncoder.encode("admin123"), "admin@naver.com", "admin", UserRoleEnum.ADMIN);

        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
        testUser3 = userRepository.save(testUser3);
        testAdminUser1 = userRepository.save(testAdminUser1);

        List<User> userList = new ArrayList<>();
        userList.add(testUser1);
        userList.add(testUser2);
        userList.add(testUser3);
        userList.add(testAdminUser1);

        createMonthData();
        createEmotionData();
        createDailyData(testUser1);
        createCommentData(userList);

    }

    private void createMonthData() {
        for (int i = 1; i <= 12; i++)
            monthRepository.save(new Month(i));
    }

    private void createEmotionData() {
        emotionRepository.save(new Emotion(1L,"기쁨", 1));
        emotionRepository.save(new Emotion(2L,"뿌듯", 1));
        emotionRepository.save(new Emotion(3L,"평온", 1));
        emotionRepository.save(new Emotion(4L,"당황", 2));
        emotionRepository.save(new Emotion(5L,"화남", 2));
        emotionRepository.save(new Emotion(6L,"슬픔", 2));
    }

    private void createDailyData(User testUser1) {
        List<Emotion> emotionList = new ArrayList<>();
        emotionList.add(new Emotion(1L));
        emotionList.add(new Emotion(2L));
        emotionList.add(new Emotion(3L));
        emotionList.add(new Emotion(4L));
        emotionList.add(new Emotion(5L));
        emotionList.add(new Emotion(6L));

        String imageUrl = "https://emotraks3bucket.s3.ap-northeast-2.amazonaws.com/50dd2535-8021-4e8f-80d2-2da4e87caa7b_penguin.png";
        Emotion emotion;
        BoardRequestDto boardRequestDto;
        int ranNum;

        for (int i = 1; i < 15; i++){
            for (int j = 0; j < 2; j++)
            {
                ranNum = (int)(Math.random() * 6);
                System.out.println("ranNum = " + ranNum);
                emotion = emotionList.get(ranNum);
                boardRequestDto = new BoardRequestDto(2023, 4, i, emotion.getId(), (int) (Math.random() * 5) + 1, "날이 좋아서 기분이 좋아요", true, false);
                dailyRepository.save(new Daily(imageUrl, boardRequestDto, testUser1, emotion));
            }
        }
    }

    private void createCommentData (List<User> userList) {

    }

}
