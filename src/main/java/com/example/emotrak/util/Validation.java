package com.example.emotrak.util;

import com.example.emotrak.dto.TokenDto;
import com.example.emotrak.entity.User;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.jwt.TokenProvider;
import com.example.emotrak.repository.RefreshTokenRepository;
import com.example.emotrak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class Validation {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public void checkAccessToken (HttpServletRequest request, User user){
        if (!tokenProvider.validateToken(request.getHeader("Authorization").substring(7)))
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
        if (null == user) throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
    }

//    @Transactional(readOnly = true)
//    public User getPresentEmail(String email) {
//        Optional<User> optionalMember = userRepository.findByEmail(email);
//        return optionalMember.orElse(null);
//    }

    @Transactional
    public User validateUserToRefresh(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getUserFromAuthentication();
    }

    @Transactional
    public User validateUserToAccess(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Authorization").substring(7))) {
            return null;
        }
        return tokenProvider.getUserFromAuthentication();
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

    public void accessTokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

    public User isPresentUserFollow(String email) {
        Optional<User> optionalMember = userRepository.findByEmail(email);
        return optionalMember.orElseThrow(
                () -> new CustomException(CustomErrorCode.USER_NOT_FOUND)
        );
    }

    @Transactional(readOnly = true)
    public User getPresentEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElseThrow(()->new IllegalArgumentException("MEMBER_NOT_FOUND"));
    }

    public User getDeleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElseThrow(()-> new IllegalArgumentException("MEMBER_NOT_FOUND"));
    }

    public RefreshToken getDeleteToken(User user) {
        Optional<RefreshToken> optionalUser = refreshTokenRepository.findByUser(user);
        return optionalUser.orElseThrow(()->new IllegalArgumentException("INVALID_TOKEN"));
    }


}