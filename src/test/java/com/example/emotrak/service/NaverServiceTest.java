package com.example.emotrak.service;

import com.example.emotrak.jwt.TokenProvider;
import com.example.emotrak.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NaverServiceTest {
    @Autowired
    private NaverService naverService;

    @MockBean
    private RestTemplate rt;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TokenProvider tokenProvider;

    @Nested
    @DisplayName("NaverLogin")
    class naverLogin {
        @Test
        @DisplayName("정상적인 로그인")
        void NaverLoginTest() throws JsonProcessingException {
        }


    }




    @Nested
    @DisplayName("NaverUnlink")
    class naverUnlink {
        @Test
        @DisplayName("정상적인 연동해제")
        void UnlinkNaverTest() {

        }

        @Test
        @DisplayName("네이버 연동 해제 실패")
        void UnlinkNaverFailureTest() {


        }
    }




}