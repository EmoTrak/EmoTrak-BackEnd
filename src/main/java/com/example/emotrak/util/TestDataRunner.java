package com.example.emotrak.util;

import com.example.emotrak.entity.Emotion;
import com.example.emotrak.entity.Month;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.repository.EmotionRepository;
import com.example.emotrak.repository.MonthRepository;
import com.example.emotrak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MonthRepository monthRepository;
    private final EmotionRepository emotionRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 테스트 User 생성
        User testUser1 = new User(passwordEncoder.encode("jingulee123"), "jingulee1@naver.com", "jingulee", UserRoleEnum.USER);
        User testUser2 = new User(passwordEncoder.encode("jingulee123"), "jingulee2@naver.com", "jingulee2", UserRoleEnum.USER);
        User testAdminUser1 = new User(passwordEncoder.encode("admin123"), "admin@naver.com", "admin", UserRoleEnum.ADMIN);

        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
        testAdminUser1 = userRepository.save(testAdminUser1);

        createMonthData();
        createEmotionData();
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

}
