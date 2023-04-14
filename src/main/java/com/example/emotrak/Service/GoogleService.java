package com.example.emotrak.Service;

import com.example.emotrak.dto.GoogleUserInfoDto;
import com.example.emotrak.dto.NaverUserInfoDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.jwt.JwtUtil;
import com.example.emotrak.repository.UserRepository;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${google_client_id}")
    private String clientId;

    @Value("${google_client_secret}")
    private String clientSecret;

    public void googleLogin(String code, String scope, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code, scope);
        // 2. 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
        GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);
        // 3. 필요시에 회원가입
        User googleUser = registerGoogleUserIfNeeded(googleUserInfo);
        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(googleUser.getEmail(), googleUser.getRole());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, createToken);
        response.addHeader("nickname", googleUser.getNickname());
    }
    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getToken(String code, String scope) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", "http://localhost:3000/oauth/google");
        body.add("redirect_uri", "http://localhost:8080/google/callback");
        body.add("redirect_uri", "http://3.38.102.13:8080/google/callback");
        body.add("code", code);
        body.add("scope", scope); // 스코프 추가
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> tokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                tokenRequest,
                String.class
        );
        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        System.out.println("jsonNode = " + jsonNode);
        System.out.println("jsonNode.toString() = " + jsonNode.toString());
        log.info("JSON Data: {}", jsonNode.toString());
        return jsonNode.get("access_token").asText();

    }
    // 2. 토큰으로 네이버 API 호출 : "액세스 토큰"으로 "네이버 사용자 정보" 가져오기
    private GoogleUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.POST,
                googleUserInfoRequest,
                String.class
        );


        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String id = jsonNode.get("sub").asText(); // 변경된 부분, Google 사용자 정보 API 응답에서 response 필드는 존재하지 않는다
        String email = jsonNode.get("email").asText();
        String nickname = jsonNode.get("name") != null ? jsonNode.get("name").asText() : UUID.randomUUID().toString();
        return new GoogleUserInfoDto(id, email, nickname);
    }
    private User registerGoogleUserIfNeeded(GoogleUserInfoDto googleUserInfo) {
        String googleId = googleUserInfo.getId();
        User googleUser = userRepository.findByGoogleId(googleId).orElse(null);
        if (googleUser == null) {
            String googleEmail = googleUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(googleEmail).orElse(null);
            if (sameEmailUser != null) {
                googleUser = sameEmailUser;
                googleUser = googleUser.googleIdUpdate(googleId);
            } else {
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                String email = googleUserInfo.getEmail();

                String nickname = googleUserInfo.getNickname();
                boolean hasNickname = userRepository.existsByNickname(nickname);
                if (hasNickname) {
                    nickname = googleUserInfo.getNickname() + "_" + userRepository.getUniqueNameSuffix(nickname);
                }

                googleUser = new User(encodedPassword, email, nickname, null, null, googleId, UserRoleEnum.USER);
            }
            userRepository.save(googleUser);
        }
        return googleUser;
    }

}
