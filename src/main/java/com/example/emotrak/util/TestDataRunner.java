package com.example.emotrak.util;

import com.example.emotrak.dto.board.BoardRequestDto;
import com.example.emotrak.dto.comment.CommentRequestDto;
import com.example.emotrak.dto.report.ReportRequestDto;
import com.example.emotrak.entity.*;
import com.example.emotrak.repository.*;
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
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final ReportRepository reportRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        User testUser1 = new User(passwordEncoder.encode("jingulee123"), "jingulee@naver.com", "jingulee", UserRoleEnum.USER);
        User testUser2 = new User(passwordEncoder.encode("jingulee123"), "jingulee2@naver.com", "jingulee2", UserRoleEnum.USER);
        User testUser3 = new User(passwordEncoder.encode("jingulee123"), "tester@naver.com", "tester", UserRoleEnum.USER);
        User testAdmin = new User(passwordEncoder.encode("admin123"), "admin@naver.com", "admin", UserRoleEnum.ADMIN);

        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
        testUser3 = userRepository.save(testUser3);
        testAdmin = userRepository.save(testAdmin);

        List<User> userList = new ArrayList<>();
        userList.add(testUser1);
        userList.add(testUser2);
        userList.add(testUser3);
        userList.add(testAdmin);

        createMonthData();
        createEmotionData();
        createDailyData(userList);
        createCommentData();
        createLikeData();
//        createReportData(userList);

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

    private void createDailyData(List<User> userList) {
        List<Emotion> emotionList = new ArrayList<>();
        emotionList.add(new Emotion(1L));
        emotionList.add(new Emotion(2L));
        emotionList.add(new Emotion(3L));
        emotionList.add(new Emotion(4L));
        emotionList.add(new Emotion(5L));
        emotionList.add(new Emotion(6L));

        String imageUrl = "https://emotraks3bucket.s3.ap-northeast-2.amazonaws.com/864148d4-d71d-4bdd-a224-2199c1373d1a_blob";
        Emotion emotion;
        BoardRequestDto boardRequestDto;
        int ranNum;

        int[] days = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        for (int k = 0; k < days.length; k++) {
            for (int i = 1; i <= days[k]; i++){
                for (int j = 0; j < 2; j++)
                {
                    ranNum = (int)(Math.random() * emotionList.size());
                    emotion = emotionList.get(ranNum);
                    boardRequestDto = new BoardRequestDto(false, 2023, k+1, i, emotion.getId(), (int) (Math.random() * 5) + 1, "날이 좋아서 기분이 좋아요", true, false);
                    for (User user : userList) {
                        dailyRepository.save(new Daily(imageUrl, boardRequestDto, user, emotion));
                    }
                }
            }
        }
    }

    private void createCommentData () {
        Daily daily = new Daily();
        ReportRequestDto reportRequestDto = new ReportRequestDto("신고합니다");
        Long dailyCount = dailyRepository.count();
        User user = new User();
        User user2 = new User();
        User user3 = new User();
        user.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        for (int i = 1; i <= dailyCount; i++)
        {
            if (i == (int)(dailyCount / 3)) {
                user.setId(2L);
                user2.setId(3L);
                user3.setId(1L);
            }
            if (i == (int)(dailyCount * 2 / 3)) {
                user.setId(3L);
                user2.setId(1L);
                user3.setId(2L);
            }

            daily.setId((long)i);
            CommentRequestDto commentRequestDto = new CommentRequestDto(i + "");
            Comment comment = new Comment(commentRequestDto, daily, user);
            commentRepository.save(comment);
            Likes likes = new Likes(daily, user2);
            likesRepository.save(likes);
            Report report = new Report(reportRequestDto, user3, daily);
            reportRepository.save(report);
        }
    }

    private void createLikeData () {
        Comment comment = new Comment();
        Long commentCount = commentRepository.count();
        Likes likes;
        ReportRequestDto reportRequestDto = new ReportRequestDto("신고합니다");
        User user = new User();
        User user2 = new User();
        User user3 = new User();
        user.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        for (int i = 1; i <= commentCount; i++)
        {
            if (i == (int)(commentCount / 3)) {
                user.setId(2L);
                user2.setId(3L);
                user3.setId(1L);
            }
            if (i == (int)(commentCount * 2 / 3)) {
                user.setId(3L);
                user2.setId(1L);
                user3.setId(2L);
            }

            comment.setId((long)i);
            likes = new Likes(comment, user);
            likesRepository.save(likes);
            likes = new Likes(comment, user2);
            likesRepository.save(likes);
            Report report = new Report(reportRequestDto, user3, comment);
            reportRepository.save(report);
        }
    }
//
//    private void createReportData (List<User> userList) {
//        int ranNum;
//        long ranNum2;
//
//        Report report;
//        ReportRequestDto reportRequestDto = new ReportRequestDto("신고합니다");
//
//        for (int i = 1; i <= 300; i++)
//        {
//            ranNum = (int)(Math.random() * userList.size());
//            ranNum2 = (long)(Math.random() * dailyRepository.count()) + 1;
//            Optional<Daily> optionalDaily = dailyRepository.findById(ranNum2);
//            if (optionalDaily.isEmpty()) continue;
//
//            report = new Report(reportRequestDto, userList.get(0), optionalDaily.get());
//            reportRepository.save(report);
//
//            ranNum = (int)(Math.random() * userList.size());
//            ranNum2 = (int)(Math.random() * commentRepository.count()) + 1;
//            Optional<Comment> optionalComment = commentRepository.findById(ranNum2);
//            if (optionalDaily.isEmpty()) continue;
//
//            report = new Report(reportRequestDto, userList.get(0), optionalComment.get());
//            reportRepository.save(report);
//        }
//    }

}
