package com.example.emotrak.controller;

import com.example.emotrak.dto.daily.DailyMonthResponseDto;
import com.example.emotrak.dto.daily.DailyResponseDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.security.UserDetailsImpl;
import com.example.emotrak.service.DailyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class DailyControllerTest {
    MockMvc mockMvc;
    @Mock
    private DailyService dailyService;
    @Autowired
    private WebApplicationContext wac;

    UserDetailsImpl userDetails;
    User user;
    UsernamePasswordAuthenticationToken authentication;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // Spring Security 인증 설정
        user = new User("1234", "user2@test.com", "user2Nickname", UserRoleEnum.USER);
        user.setId(1L);
        userDetails = new UserDetailsImpl(user);
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void testGetDailyMonth() throws Exception {
        int year = 2023;
        int month = 5;

        List<DailyMonthResponseDto> dailyMonthResponseDtoList = Arrays.asList(
                new DailyMonthResponseDto(1L, 1, 1L, "내용", "img"),
                new DailyMonthResponseDto(2L, 2, 2L, "내용", "img")
        );
        DailyResponseDto dailyResponseDto = new DailyResponseDto(2023, 5, dailyMonthResponseDtoList);

        List<Object> objectList = (List<Object>) dailyResponseDto.getContents();
        assertEquals(objectList.size(), 2);

        when(dailyService.getDailyMonth(year, month, user)).thenReturn(dailyResponseDto);

        mockMvc.perform(get("/daily")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month))
                        .with(authentication(authentication))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message").value("조회 완료"))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.data.year").value(year))
                .andExpect(jsonPath("$.data.month").value(month))

//                .andExpect(jsonPath("$.data.contents", hasSize(2)))
//                .andExpect(jsonPath("$.data.contents[0].id").value(1L))
//                .andExpect(jsonPath("$.data.contents[0].day").value(1))
//                .andExpect(jsonPath("$.data.contents[0].emoId").value(1L))
//                .andExpect(jsonPath("$.data.contents[0].detail").value("내용"))
//                .andExpect(jsonPath("$.data.contents[0].imgUrl").value("img"))
//                .andExpect(jsonPath("$.data.contents[1].id").value(2L))
//                .andExpect(jsonPath("$.data.contents[1].day").value(2))
//                .andExpect(jsonPath("$.data.contents[1].emoId").value(2L))
//                .andExpect(jsonPath("$.data.contents[1].detail").value("내용"))
//                .andExpect(jsonPath("$.data.contents[1].imgUrl").value("img"))
        ;

    }
}