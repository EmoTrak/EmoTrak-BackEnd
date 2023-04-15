package com.example.emotrak.Service;

import com.example.emotrak.dto.NaverUserInfoDto;
import com.example.emotrak.dto.TokenDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.jwt.TokenProvider;
import com.example.emotrak.repository.UserRepository;
import com.example.emotrak.util.Validation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final Validation validation;
    private final UserService userService;

    @Value("${client_id}")
    private String clientId;

    @Value("${client_secret}")
    private String clientSecret;

    public void naverLogin(String code, String state, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code, state);
        // 2. 토큰으로 네이버 API 호출 : "액세스 토큰"으로 "네이버 사용자 정보" 가져오기
        NaverUserInfoDto naverUserInfo = getNaverUserInfo(accessToken);
        // 3. 필요시에 회원가입
        User naverUser = registerNaverUserIfNeeded(naverUserInfo);
        // 4. JWT 토큰 반환
        TokenDto tokenDto = tokenProvider.generateTokenDto(naverUser, naverUser.getRole());
        validation.tokenToHeaders(tokenDto,response);
    }
    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getToken(String code, String state) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", "http://localhost:3000/oauth/naver");
        body.add("redirect_uri", "http://localhost:8080/naver/callback");
        body.add("redirect_uri", "http://3.38.102.13:8080/naver/callback");
        body.add("code", code);
        body.add("state", state);
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> tokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
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
    private NaverUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode responseNode = jsonNode.get("response");
        String id = responseNode.get("id").asText(); // 변경된 부분
        String email = responseNode.get("email").asText();
        String nickname = responseNode.get("nickname").asText();
        return new NaverUserInfoDto(id, email, nickname);
    }
    private User registerNaverUserIfNeeded (NaverUserInfoDto naverUserInfo) {
        String naverId = naverUserInfo.getId();
        User naverUser = userRepository.findByNaverId(naverId).
                orElse(null);
        if (naverUser == null) {
            String naverEmail = naverUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(naverEmail).orElse(null);
            if (sameEmailUser != null) {
                naverUser = sameEmailUser;
                naverUser = naverUser.naverIdUpdate(naverId);
            } else {
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                String email = naverUserInfo.getEmail();

                String nickname = naverUserInfo.getNickname();
                boolean hasNickname = userRepository.existsByNickname(nickname);
                if (hasNickname) {
                    nickname = naverUserInfo.getNickname() + "_" + userRepository.getUniqueNameSuffix(nickname);
                }

                naverUser = new User(encodedPassword, email, nickname, null, naverId, null, UserRoleEnum.USER);
            }
            userRepository.save(naverUser);
        }
        return naverUser;
    }

    public void unlinkNaverAccount(User user, String accessToken) {
        // 사용자가 없거나 네이버 ID가 없는 경우에 대한 예외 처리
        if (user == null || user.getNaverId() == null) {
            throw new CustomException(CustomErrorCode.NO_NAVER_LINK);
        }
        // 연동해제를 위한 네이버 API 호출
        boolean isUnlinked = unlinkNaverAccountApi(accessToken);
        if (!isUnlinked) {
            throw new CustomException(CustomErrorCode.NAVER_UNLINK_FAILED);
        }
        userService.userDelete(user);
    }

    private boolean unlinkNaverAccountApi(String accessToken) {
        String clientId = this.clientId;
        String clientSecret = this.clientSecret;

        String url = "https://nid.naver.com/oauth2.0/token?grant_type=delete" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&access_token=" + accessToken +
                "&service_provider=NAVER";

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                url,
                HttpMethod.GET,
                null,
                String.class
        );

        return response.getStatusCode() == HttpStatus.OK;
    }

}