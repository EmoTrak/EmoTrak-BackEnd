package com.example.emotrak.service;

import com.example.emotrak.dto.user.CheckEmailRequestDto;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PropertySource("classpath:application.properties")
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailSendService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    //인증번호 생성
    private final String ePw = createKey();

    @Value("${spring.mail.username}")
    private String id;

    public MimeMessage createMessage(String to)throws MessagingException, UnsupportedEncodingException {
//        log.info("보내는 대상 : "+ to);
//        log.info("인증 번호 : " + ePw);
        MimeMessage  message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to); // to 보내는 대상
        message.setSubject("회원가입 인증 코드: "); //메일 제목

        // 메일 내용 메일의 subtype을 html로 지정하여 html문법 사용 가능
        String msg="";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 회원가입 화면에서 입력해주세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += ePw;
        msg += "</td></tr></tbody></table></div>";

        message.setText(msg, "utf-8", "html"); //내용, charset타입, subtype
        message.setFrom(new InternetAddress(id,"Emotrak_Admin")); //보내는 사람의 메일 주소, 보내는 사람 이름

        return message;
    }

    // 인증코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }
    // 이메일 중복 체크. 이메일이 있으면 true - 중복된 이메일 반환 / 이메일이 없으면 false 사용가능한 이메일
    public void signupEmailCheck(CheckEmailRequestDto checkEmailRequestDto) {

        // 이메일이 비어있는지 체크
        if(checkEmailRequestDto.getEmail().equals("")) throw new CustomException(CustomErrorCode.EMAIL_BLANK);

        // 이메일 형식이 일치하는지 체크
        Pattern passPattern1 = Pattern.compile("^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$"); // 정규식을 적는부분 e+을 지우고 쓰세요
        Matcher matcher = passPattern1.matcher(checkEmailRequestDto.getEmail());
        if(!matcher.find()) throw new CustomException(CustomErrorCode.NOT_EMAIL_PATTERN);

        // 중복된 이메일이 존재하는지 체크
        boolean isEmailExist = userRepository.existsByEmail(checkEmailRequestDto.getEmail());
        if(isEmailExist){
            throw new CustomException(CustomErrorCode.DUPLICATE_EMAIL);
        }
    }

    /*
        메일 발송
        sendSimpleMessage의 매개변수로 들어온 to는 인증번호를 받을 메일주소
        MimeMessage 객체 안에 내가 전송할 메일의 내용을 담아준다.
        bean으로 등록해둔 javaMailSender 객체를 사용하여 이메일 send
     */
    public String sendSimpleMessage(CheckEmailRequestDto checkEmailRequestDto)throws Exception {

        signupEmailCheck(checkEmailRequestDto);

        MimeMessage message = createMessage(checkEmailRequestDto.getEmail());
        try{
            javaMailSender.send(message); // 메일 발송
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw; // 메일로 보냈던 인증 코드를 서버로 리턴
    }
}