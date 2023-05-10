package com.example.emotrak.service;


import com.example.emotrak.dto.user.CheckEmailRequestDto;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EmailSendServiceTest {
    @InjectMocks
    private EmailSendService emailSendService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private MimeMessage mimeMessage;
    private CheckEmailRequestDto checkEmailRequestDto;
    @Value("${spring.mail.username}")
    private String id;

    @BeforeEach
    void setUp() {
        checkEmailRequestDto = new CheckEmailRequestDto(id);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

        @DisplayName("메세지 생성")
        @Test
        void createMessage() throws MessagingException, UnsupportedEncodingException {
            //when
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
            //then
            emailSendService.createMessage(id);
        }

        @DisplayName("가입 시 이메일 체크")
        @Test
        void signupEmailCheck(){
            //given
            checkEmailRequestDto.setEmail("asdfzxcv@naver.com");
            //when
            when(userRepository.existsByEmail(checkEmailRequestDto.getEmail())).thenReturn(false);
            //then
            emailSendService.signupEmailCheck(checkEmailRequestDto);
        }

        @DisplayName("이메일 인증 메일 발송")
        @Test
        void sendSimpleMessage() throws Exception {
            //given
            checkEmailRequestDto.setEmail("asdfzxcv@naver.com");
            //when
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);//when(!passwordEncoder.matches(loginRequestDto.getPassword(), encodePassword)).thenReturn(true);
            //then
            emailSendService.sendSimpleMessage(checkEmailRequestDto);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

        @Test
        @DisplayName("이메일 공백 체크")
        public void emailEmptyCheck() {
            //give
            checkEmailRequestDto.setEmail("");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                emailSendService.signupEmailCheck(checkEmailRequestDto);
            });

            // then
            assertEquals("이메일을 입력해주세요.", customException.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("이메일 형식 틀림")
        public void emailPatternCheck() {
            //give
            checkEmailRequestDto.setEmail("qwer1234");

            //when
            CustomException customException = assertThrows(CustomException.class, () -> {
                emailSendService.signupEmailCheck(checkEmailRequestDto);
            });

            // then
            assertEquals("올바른 이메일 형식이 아닙니다.", customException.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("이메일 중복 됨")
        public void emailCheck() {
            //give
            checkEmailRequestDto.setEmail("user123567@naver.com");

            //when
            Mockito.when(userRepository.existsByEmail(checkEmailRequestDto.getEmail())).thenReturn(true);
            CustomException customException = assertThrows(CustomException.class, () -> {
                emailSendService.signupEmailCheck(checkEmailRequestDto);
            });

            // then
            assertEquals("중복된 이메일이 존재합니다.", customException.getErrorCode().getMessage());
        }

    }
}