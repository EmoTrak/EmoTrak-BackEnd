package com.example.emotrak.Service;

import com.example.emotrak.dto.user.OauthUserInfoDto;
import com.example.emotrak.dto.user.TokenDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.jwt.TokenProvider;
import com.example.emotrak.repository.UserRepository;
import com.example.emotrak.jwt.Validation;
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
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final Validation validation;

    @Value("${client_id}")
    private String clientId;

    @Value("${client_secret}")
    private String clientSecret;

    public void naverLogin(String code, String state, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰 & 리프레시 토큰" 요청
        Map<String, String> tokens = getToken(code, state);
        String accessToken = tokens.get("access_token");
        String refreshToken = tokens.get("refresh_token");
        // 2. 토큰으로 네이버 API 호출 : "액세스 토큰"으로 "네이버 사용자 정보" 가져오기
        OauthUserInfoDto oauthUserInfo = getNaverUserInfo(accessToken);
        // 3. 필요시에 회원가입
        User naverUser = registerNaverUserIfNeeded(oauthUserInfo);
        // 4. JWT 토큰 반환
        TokenDto tokenDto = tokenProvider.generateTokenDto(naverUser, naverUser.getRole());
        log.info("JWT Access Token: {}", tokenDto.getAccessToken());
        log.info("JWT Refresh Token: {}", tokenDto.getRefreshToken());
        validation.tokenToHeaders(tokenDto,response);
    }
    // 1. "인가 코드"로 "액세스 토큰 & 리프레시 토큰" 요청
    private Map<String, String> getToken(String code, String state) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", "http://iamnobody.xyz/oauth/naver");
        body.add("redirect_uri", "https://emotrak.vercel.app/oauth/naver");
        body.add("redirect_uri", "http://localhost:3000/oauth/naver");
//        body.add("redirect_uri", "http://localhost:8080/naver/callback");
        body.add("redirect_uri", "http://pingu-lj.shop/naver/callback");
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
        // HTTP 응답 (JSON) -> 액세스 토큰 & 리프레시 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        log.info("JSON Data: {}", jsonNode);
        String accessToken = jsonNode.get("access_token").asText();
        String refreshToken = jsonNode.get("refresh_token").asText();

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;

    }
    // 2. 토큰으로 네이버 API 호출 : "액세스 토큰"으로 "네이버 사용자 정보" 가져오기
    private OauthUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
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
        return new OauthUserInfoDto(id, email, nickname);
    }
    private User registerNaverUserIfNeeded (OauthUserInfoDto oauthUserInfo) {
        String naverId = oauthUserInfo.getId();
        User naverUser = userRepository.findByNaverId(naverId).
                orElse(null);
        if (naverUser == null) {
            String naverEmail = oauthUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(naverEmail).orElse(null);
            if (sameEmailUser != null) {
                naverUser = sameEmailUser;
                naverUser = naverUser.naverIdUpdate(naverId);
            } else {
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                String email = oauthUserInfo.getEmail();

                String nickname = oauthUserInfo.getNickname();
                boolean hasNickname = userRepository.existsByNickname(nickname);
                if (hasNickname) {
                    nickname = oauthUserInfo.getNickname() + "_" + userRepository.getUniqueNameSuffix(nickname);
                }

                naverUser = new User(encodedPassword, email, nickname, null, naverId, null, UserRoleEnum.USER);
            }
            userRepository.save(naverUser);
        }
        return naverUser;
    }

    public void unlinkNaver(User user, String accessToken, String refreshToken) {
        if (!isTokenValid(accessToken)) {
            // 유효하지 않은 토큰인 경우, 토큰 갱신
            accessToken = refreshAccessToken(refreshToken);
            if (accessToken == null || !isTokenValid(accessToken)) {
                throw new CustomException(CustomErrorCode.INVALID_OAUTH_TOKEN);
            }
        }
        // 연동해제를 위한 네이버 API 호출
        boolean isUnlinked = unlinkNaverAccountApi(accessToken);
        if (!isUnlinked) {
            throw new CustomException(CustomErrorCode.OAUTH_UNLINK_FAILED);
        }
        // 연동 해제 후 리프레시 토큰을 사용하여 액세스 토큰을 갱신하려고 시도
        String newAccessToken = refreshAccessToken(refreshToken);
        if (newAccessToken != null) {
            throw new CustomException(CustomErrorCode.OAUTH_UNLINK_FAILED);
        }
        log.info("네이버 연동해제 완료: userId={}", user.getId());
    }

    private boolean unlinkNaverAccountApi(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "delete");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("access_token", accessToken);
        body.add("service_provider", "NAVER");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return response.getStatusCode() == HttpStatus.OK;
    }

    // 토큰의 유효성 검사
    private boolean isTokenValid(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();

        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://openapi.naver.com/v1/nid/me",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }

    // 유효하지 않은 토큰인 경우, 토큰 갱신
    private String refreshAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();

        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://nid.naver.com/oauth2.0/token",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("access_token").asText();
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

}