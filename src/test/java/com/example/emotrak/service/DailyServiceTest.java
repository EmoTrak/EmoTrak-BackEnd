package com.example.emotrak.service;

import com.example.emotrak.dto.board.BoardRequestDto;
import com.example.emotrak.dto.daily.DailyDetailResponseDto;
import com.example.emotrak.dto.daily.DailyMonthResponseDto;
import com.example.emotrak.dto.daily.DailyResponseDto;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.Emotion;
import com.example.emotrak.entity.User;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.DailyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DailyServiceTest {
    @InjectMocks
    private DailyService dailyService;
    @Mock
    private DailyRepository dailyRepository;

    private int year;
    private int month;
    private User user = new User();
    private Long dailyId;
    private Emotion emotion = new Emotion();
    private BoardRequestDto boardRequestDto;
    private Daily daily;

    @BeforeEach
    void setup() {
        year = 2022;
        month = 3;
        user.setId(1L);
        emotion.setId(1L);
        boardRequestDto = new BoardRequestDto(
                true, year, month, 1,
                1L, 5, "오늘은 기분이 좋았어요!",
                true, false
        );
        daily = new Daily("", boardRequestDto, user, emotion);
    }

    @Nested
    @DisplayName("월별 내역 조회")
    class getDailyMonth {
        @Test
        @DisplayName("성공 케이스")
        public void a_getDailyMonth() {
            // given
            List<DailyMonthResponseDto> dailyMonthResponseDtoList = new ArrayList<>();
            DailyMonthResponseDto daily1 = new DailyMonthResponseDto(1L, 1, 1L, "내용입니다.", "imgUrl");
            DailyMonthResponseDto daily2 = new DailyMonthResponseDto(2L, 1, 2L, "내용입니다.", "imgUrl");
            DailyMonthResponseDto daily3 = new DailyMonthResponseDto(3L, 2, 3L, "내용입니다.", "imgUrl");
            DailyMonthResponseDto daily4 = new DailyMonthResponseDto(4L, 2, 4L, "내용입니다.", "imgUrl");
            DailyMonthResponseDto daily5 = new DailyMonthResponseDto(5L, 3, 5L, "내용입니다.", "imgUrl");
            DailyMonthResponseDto daily6 = new DailyMonthResponseDto(6L, 3, 6L, "내용입니다.", "imgUrl");
            dailyMonthResponseDtoList.add(daily1);
            dailyMonthResponseDtoList.add(daily2);
            dailyMonthResponseDtoList.add(daily3);
            dailyMonthResponseDtoList.add(daily4);
            dailyMonthResponseDtoList.add(daily5);
            dailyMonthResponseDtoList.add(daily6);

            // Mocking repository
            Mockito.when(dailyRepository.getDailyMonth(year, month, user.getId())).thenReturn(dailyMonthResponseDtoList);

            // when
            DailyResponseDto dailyResponseDto = dailyService.getDailyMonth(year, month, user);

            // then
            assertEquals(dailyResponseDto.getYear(), year);
            assertEquals(dailyResponseDto.getMonth(), month);
            assertEquals(dailyResponseDto.getContents(), dailyMonthResponseDtoList);
        }
    }
    @Nested
    @DisplayName("일별 내역 조회")
    class getDailyDetail {
        @Test
        @DisplayName("성공 케이스")
        public void b_getDailyDetail() {
            // given
            List<DailyDetailResponseDto> dailyDetailResponseDtoList = new ArrayList<>();
            DailyDetailResponseDto daily1 = new DailyDetailResponseDto(1L, 1, 1L, 1, "내용입니다.", "", true, false, false);
            DailyDetailResponseDto daily2 = new DailyDetailResponseDto(2L, 1, 2L, 2, "내용입니다.", "", true, false, false);
            DailyDetailResponseDto daily3 = new DailyDetailResponseDto(3L, 1, 3L, 3, "내용입니다.", "", true, false, false);
            DailyDetailResponseDto daily4 = new DailyDetailResponseDto(4L, 1, 4L, 4, "내용입니다.", "", true, false, false);
            DailyDetailResponseDto daily5 = new DailyDetailResponseDto(5L, 1, 5L, 5, "내용입니다.", "", true, false, false);
            DailyDetailResponseDto daily6 = new DailyDetailResponseDto(6L, 1, 6L, 1, "내용입니다.", "", true, false, false);
            dailyDetailResponseDtoList.add(daily1);
            dailyDetailResponseDtoList.add(daily2);
            dailyDetailResponseDtoList.add(daily3);
            dailyDetailResponseDtoList.add(daily4);
            dailyDetailResponseDtoList.add(daily5);
            dailyDetailResponseDtoList.add(daily6);

            // Mocking repository
            Mockito.when(dailyRepository.findById(dailyId)).thenReturn(Optional.of(daily));
            Mockito.when(dailyRepository.getDailyDetail(dailyId)).thenReturn(dailyDetailResponseDtoList);

            // when
            DailyResponseDto dailyResponseDto = dailyService.getDailyDetail(dailyId, user);

            // then
            assertEquals(dailyResponseDto.getYear(), year);
            assertEquals(dailyResponseDto.getMonth(), month);
            assertEquals(dailyResponseDto.getContents(), dailyDetailResponseDtoList);
        }
        @Nested
        @DisplayName("실패 케이스")
        class getDailyFail {
            @Test
            @DisplayName("게시물을 찾을 수 없음")
            public void c_getDailyDetailFail1() {
                // given
                dailyId = 2L;

                // when
                CustomException customException = assertThrows(CustomException.class, () -> {
                    dailyService.getDailyDetail(dailyId, user);
                });

                // then
                assertEquals("선택한 게시물을 찾을 수 없습니다.", customException.getErrorCode().getMessage());
            }

            @Test
            @DisplayName("권한이 없음")
            public void d_getDailyDetailFail2() {
                // given
                User user2 = new User();
                user2.setId(2L);

                /*
                Mockito.lenient().when(dailyRepository.findById(dailyId)).thenReturn(Optional.of(daily));
                Mockito.lenient() : 더 유연한 스텁 모드
                                    스텁을 설정해도 해당 메소드가 호출되지 않더라도 경고 메시지가 출력되지 않음
                 */

                // Mocking repository
                Mockito.when(dailyRepository.findById(dailyId)).thenReturn(Optional.of(daily));

                // when
                CustomException customException = assertThrows(CustomException.class, () -> {
                    dailyService.getDailyDetail(dailyId, user2);
                });

                // then
                assertEquals("권한이 없습니다.", customException.getErrorCode().getMessage());
            }
        }
    }
}