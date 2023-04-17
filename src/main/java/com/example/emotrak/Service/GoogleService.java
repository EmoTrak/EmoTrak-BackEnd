package com.example.emotrak.Service;

import com.example.emotrak.dto.OauthUserInfoDto;
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
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final Validation validation;
    private final UserService userService;

    @Value("${google_client_id}")
    private String clientId;

    @Value("${google_client_secret}")
    private String clientSecret;

    public void googleLogin(String code, String scope, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code, scope);

        // 2. 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
        OauthUserInfoDto oauthUserInfo = getGoogleUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User googleUser = registerGoogleUserIfNeeded(oauthUserInfo);

        // 4. JWT 토큰 반환
        TokenDto tokenDto = tokenProvider.generateTokenDto(googleUser, googleUser.getRole());
        log.info("JWT Access Token: {}", tokenDto.getAccessToken());
        validation.tokenToHeaders(tokenDto, response);
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
        body.add("redirect_uri", "http://iamnobody.xyz/oauth/google");
        body.add("redirect_uri", "http://localhost:3000/oauth/google");
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

    // 2. 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
    private OauthUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
        // 액세스 토큰이 유효한지 확인
        if (!isTokenValid(accessToken)) {
            log.error("Invalid or expired access token");
            throw new RuntimeException("Invalid or expired access token");
        }

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
        String nickname = jsonNode.get("name") != null ? jsonNode.get("name").asText() : generateUniqueRandomString(6);
        return new OauthUserInfoDto(id, email, nickname);
    }

    private boolean isTokenValid(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> tokenInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + accessToken,
                    HttpMethod.GET,
                    tokenInfoRequest,
                    String.class
            );
            return HttpStatus.OK.equals(response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error("Invalid or expired access token: {}", e.getMessage());
            return false;
        }
    }

    private String generateUniqueRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        String generatedString;

        do {
            generatedString = random.ints(length, 0, characters.length())
                    .mapToObj(characters::charAt)
                    .map(Object::toString)
                    .collect(Collectors.joining());
        } while (userRepository.existsByNickname(generatedString));

        return generatedString;
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

                String nickname = oauthUserInfo.getNickname();
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

    public void unlinkGoogleAccount(User user, String accessToken) {
        // 구글 ID가 없는 경우에 대한 예외 처리
        if (accessToken == null || user.getGoogleId() == null) {
            throw new CustomException(CustomErrorCode.NO_OAUTH_LINK);
        }
        boolean isUnlinked = unlinkGoogleAccountApi(accessToken);
        if (!isUnlinked) {
            throw new CustomException(CustomErrorCode.OAUTH_UNLINK_FAILED);
        }
        log.info("user.getId() = {}", user.getId());
        log.info("user.getGoogleId() = {}", user.getGoogleId());
        log.info("user = {}", user);
//        userService.deleteUser(user);
        log.info("구글 연동해제 완료");
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
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                log.error("Access token is invalid or expired");
            } else if (statusCode == HttpStatus.NOT_FOUND) {
                log.error("Token not found or user does not exist");
            } else {
                log.error("Google unlink failed");
            }
            return false;
        }
    }
}
