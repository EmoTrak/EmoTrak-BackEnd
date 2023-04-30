package com.example.emotrak.service;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final Validation validation;

    @Value("${google_client_id}")
    private String clientId;

    @Value("${google_client_secret}")
    private String clientSecret;

    public void googleLogin(String code, String scope, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        Map<String, String> tokens = getToken(code, scope);
        String accessToken = tokens.get("access_token");
        String refreshToken = tokens.get("refresh_token");
        // 2. 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
        OauthUserInfoDto oauthUserInfo = getGoogleUserInfo(accessToken);
        // 3. 필요시에 회원가입
        User googleUser = registerGoogleUserIfNeeded(oauthUserInfo);
        // 4. 사용자 엔티티에 리프레시 토큰 저장
        if (refreshToken != null) {
            googleUser.updateGoogleRefresh(refreshToken);
        }
        // 5. JWT 토큰 반환
        TokenDto tokenDto = tokenProvider.generateTokenDto(googleUser, googleUser.getRole());
        validation.tokenToHeaders(tokenDto, response);
    }

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private Map<String, String> getToken(String code, String scope) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", "https://emotrak.vercel.app/oauth/google");
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
        String accessToken = jsonNode.get("access_token").asText();
        String refreshToken = jsonNode.has("refresh_token") ? jsonNode.get("refresh_token").asText() : null;

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;

    }

    // 2. 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
    private OauthUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
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
        String nickname = jsonNode.get("name") != null ? jsonNode.get("name").asText() : "google";
        return new OauthUserInfoDto(id, email, nickname);
    }

    private User registerGoogleUserIfNeeded(OauthUserInfoDto oauthUserInfo) {
        String googleId = oauthUserInfo.getId();
        User googleUser = userRepository.findByGoogleId(googleId).orElse(null);
        if (googleUser == null) {
            String googleEmail = oauthUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(googleEmail).orElse(null);
            if (sameEmailUser != null) {
                googleUser = sameEmailUser;
                googleUser = googleUser.googleIdUpdate(googleId);
            } else {
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                String email = oauthUserInfo.getEmail();

                String nickname = oauthUserInfo.getNickname().replace("_", "");
                boolean hasNickname = userRepository.existsByNickname(nickname);
                if (hasNickname) {
                    nickname = oauthUserInfo.getNickname() + "_" + userRepository.getUniqueNameSuffix(nickname);
                }

                googleUser = new User(encodedPassword, email, nickname, null, null, googleId, UserRoleEnum.USER);
            }
            userRepository.save(googleUser);
        }
        return googleUser;
    }

    public void unlinkGoogle(User user) {
        // DB에서 사용자의 리프레시 토큰 가져오기
        String refreshToken = user.getGoogleRefresh();
        // 리프레시 토큰을 사용하여 액세스 토큰 갱신
        String accessToken = refreshAccessToken(refreshToken);
        if (accessToken == null) {
            throw new CustomException(CustomErrorCode.INVALID_OAUTH_TOKEN);
        }
        // 연동해제를 위한 구글 API 호출
        boolean isUnlinked = unlinkGoogleAccountApi(accessToken);
        if (!isUnlinked) {
            throw new CustomException(CustomErrorCode.OAUTH_UNLINK_FAILED);
        }
    }


    private boolean unlinkGoogleAccountApi(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        // 액세스 토큰을 통해 API 호출
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://accounts.google.com/o/oauth2/revoke")
                .queryParam("token", accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        try {
            rt.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, String.class);
            return true;
        } catch (HttpClientErrorException ex) {
            HttpStatus statusCode = ex.getStatusCode();
            return statusCode == HttpStatus.UNAUTHORIZED || statusCode == HttpStatus.NOT_FOUND ? false : true;
        }
    }

    // 리프레시 토큰을 사용하여 액세스 토큰 갱신
    private String refreshAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                request,
                String.class
        );

        // 응답에서 액세스 토큰 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
        return jsonNode.get("access_token").asText();
    }
}
