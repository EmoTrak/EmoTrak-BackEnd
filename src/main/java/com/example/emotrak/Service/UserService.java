package com.example.emotrak.Service;


import com.example.emotrak.dto.*;
import com.example.emotrak.entity.Daily;
import com.example.emotrak.entity.User;
import com.example.emotrak.entity.UserRoleEnum;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import com.example.emotrak.jwt.JwtUtil;

import com.example.emotrak.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.emotrak.entity.UserRoleEnum.ADMIN;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;
//    private final TokenProvider tokenProvider;
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final HttpServletResponse response;
//    private final Validation validation;
//    private final HashTagRepository hashTagRepository;
    // ADMIN_TOKEN
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    // 회원가입
    @Transactional
    public void signup(SignupRequestDto signupRequestDto){

        String email = signupRequestDto.getEmail();
        String password = encoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        // 이메일이 비어있는지 체크
        if(email.equals("")) throw new CustomException(CustomErrorCode.EMAIL_BLANK);
        // 패스워드가 비어있는지 체크
        if(signupRequestDto.getPassword().equals("")) throw new CustomException(CustomErrorCode.PASSWORD_BLANK);
        //닉네임이 비어있는지 체크
        if(nickname.equals("")) throw new CustomException(CustomErrorCode.NICKNAME_BLANK);

        // 클라이언트에서 중복을 확인하지만 서버에서도 한번더 중복을 확인함.
        boolean isEmailExist = userRepository.existsByEmail(signupRequestDto.getEmail());
        if (isEmailExist) throw new CustomException(CustomErrorCode.DUPLICATE_EMAIL);

        //닉네임이 중복되는지 체크
        boolean isNickExist = userRepository.existsByNickname(signupRequestDto.getNickname());
        if(isNickExist){
            throw new CustomException(CustomErrorCode.DUPLICATE_NICKNAME);
        }

        // 이메일 형식이 일치하는지 체크
        Pattern passPattern1 = Pattern.compile("^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$"); // 정규식을 적는부분 e+을 지우고 쓰세요
        Matcher matcher = passPattern1.matcher(email);
        if(!matcher.find()) throw new CustomException(CustomErrorCode.NOT_EMAIL_PATTERN);

        // 닉네임 형식이 일치하는지 체크
        passPattern1 = Pattern.compile("^[ㄱ-ㅎ|가-힣|a-z|A-Z|\\d$@!%*#?&()^]{1,8}$"); // 특수문자를 제외하는 정규식
        matcher = passPattern1.matcher(nickname);
        if(!matcher.find()) throw new CustomException(CustomErrorCode.NOT_NICKNAME_PATTERN);

        // 비밀번호 형식이 일치하는지 체크
        //passPattern1 = Pattern.compile("^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-Z\\d!@#$%^&*()_+]{8,15}$");
        //숫자와 소문자와 특수문자 !@#$%^&*()중 1개가 포함되어야 하며 8자~15자 사이 인 값
        passPattern1 = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d|$@!%*#?&()^]{8,15}$");
        matcher = passPattern1.matcher(signupRequestDto.getPassword());
        if (!matcher.find())throw new CustomException(CustomErrorCode.NOT_PASSWORD_PATTERN);

        UserRoleEnum role = UserRoleEnum.USER;

        User user = new User(password, email,nickname, role);
        userRepository.saveAndFlush(user);
    }

    // 로그인
    @Transactional(readOnly = true)
    public void login(LoginRequestDto loginRequestDto, HttpServletResponse response){
        String email = loginRequestDto.getEmail();

        // 이메일이 비어있는지 체크
        if(email.equals("")) throw new CustomException(CustomErrorCode.EMAIL_BLANK);
        // 패스워드가 비어있는지 체크
        if(loginRequestDto.getPassword().equals("")) throw new CustomException(CustomErrorCode.PASSWORD_BLANK);

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(CustomErrorCode.USER_NOT_FOUND)
        );

        String encodePassword = user.getPassword();

        if(!encoder.matches(loginRequestDto.getPassword(), encodePassword)){
            throw new CustomException(CustomErrorCode.NOT_PROPER_PASSWORD);
        }
//        TokenDto tokenDto = tokenProvider.generateTokenDto(user);
//        validation.tokenToHeaders(tokenDto,response);

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getEmail(), user.getRole()));
        response.addHeader("nickname", user.getNickname());
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
    public void signupNicknameCheck(CheckNicknameRequestDto checkNicknameRequestDto) {

        // 닉네임이 비어있는지 체크
        if(checkNicknameRequestDto.getNickname().equals("")) throw new CustomException(CustomErrorCode.NICKNAME_BLANK);

        // 중복된 닉네임이 있는지 체크
        boolean isNickExist = userRepository.existsByNickname(checkNicknameRequestDto.getNickname());
        if(isNickExist){
            throw new CustomException(CustomErrorCode.DUPLICATE_NICKNAME);
        }

        // 닉네임 형식이 일치하는지 체크
        Pattern passPattern1 = Pattern.compile("^[ㄱ-ㅎ|가-힣|a-z|A-Z|\\d$@!%*#?&()^]{1,8}$"); // 특스문자를 제외하는 정규식
        Matcher matcher = passPattern1.matcher(checkNicknameRequestDto.getNickname());
        if(!matcher.find()) throw new CustomException(CustomErrorCode.NOT_NICKNAME_PATTERN);
    }

    public UserResponseDto userMypage(User user) {
        // 유저 엔티티 가져오기
        Optional<User> getUser = userRepository.findById(user.getId());
        // 유저 엔티티가 없으면 에러
        if(!getUser.isPresent()){
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }
        User requestUser = getUser.get();

        return new UserResponseDto(requestUser.getEmail(),requestUser.getNickname(),requestUser.isHasSocial());
    }

    @Transactional
    public void nicknameUpdate(NicknameRequestDto nicknameRequestDto, User user) {
        // 유저 엔티티 가져오기
        Optional<User> getUser = userRepository.findById(user.getId());
        // 유저 엔티티가 없으면 에러
        if(!getUser.isPresent()){
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }
        User updateUser = getUser.get();
        // 닉네임이 비어있는지 체크
        if(nicknameRequestDto.getNickname().equals("")) throw new CustomException(CustomErrorCode.NICKNAME_BLANK);

        // 닉네임이 현재와 같은지 체크
        if(nicknameRequestDto.getNickname().equals(updateUser.getNickname())) throw new CustomException(CustomErrorCode.SAME_NICKNAME);

        // 중복된 닉네임이 있는지 체크
        boolean isNickExist = userRepository.existsByNickname(nicknameRequestDto.getNickname());
        if(isNickExist){
            throw new CustomException(CustomErrorCode.DUPLICATE_NICKNAME);
        }

        // 닉네임 형식이 일치하는지 체크
        Pattern passPattern1 = Pattern.compile("^[ㄱ-ㅎ|가-힣|a-z|A-Z|\\d$@!%*#?&()^]{1,8}$");
        Matcher matcher = passPattern1.matcher(nicknameRequestDto.getNickname());
        if(!matcher.find()) throw new CustomException(CustomErrorCode.DUPLICATE_NICKNAME);

        // 유저 닉네임 업데이트 및 저장
        updateUser.nicknameUpdate(nicknameRequestDto.getNickname());
        userRepository.save(updateUser);
    }

    @Transactional
    public void passwordUpdate(PasswordRequestDto passwordRequestDto, User user) {
        // 유저 엔티티 가져오기
        Optional<User> getUser = userRepository.findById(user.getId());
        // 유저 엔티티가 없으면 에러
        if(!getUser.isPresent()){
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }
        User updateUser = getUser.get();

        // 패스워드가 비어있는지 체크
        if(passwordRequestDto.getPassword().equals("")) throw new CustomException(CustomErrorCode.PASSWORD_BLANK);

        // 패스워드가 현재와 같은지 체크
        String encodePassword = updateUser.getPassword();
        if(encoder.matches(passwordRequestDto.getPassword(), encodePassword)){
            throw new CustomException(CustomErrorCode.SAME_PASSWORD);
        }

        // 비밀번호 형식이 일치하는지 체크
        //passPattern1 = Pattern.compile("^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-Z\\d!@#$%^&*()_+]{8,15}$");
        //숫자와 소문자와 특수문자 !@#$%^&*()중 1개가 포함되어야 하며 8자~15자 사이 인 값
        Pattern passPattern1 = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d|$@!%*#?&()^]{8,15}$");
        Matcher matcher = passPattern1.matcher(passwordRequestDto.getPassword());
        if (!matcher.find())throw new CustomException(CustomErrorCode.NOT_PASSWORD_PATTERN);
        // 패스워드 암호화
        String password = encoder.encode(passwordRequestDto.getPassword());
        // 패스워드 업데이트 및 저장
        updateUser.passwordUpdate(password);
        userRepository.save(updateUser);
    }
    @Transactional
    public void delete(User user) {
        // 유저 엔티티 가져오기
        Optional<User> getUser = userRepository.findById(user.getId());
        // 유저 엔티티가 없으면 에러
        if(!getUser.isPresent()){
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }
        User deleteUser = getUser.get();

        // 유저 아이디 삭제 체크 컬럼 Y 로 변경
        userRepository.delete(deleteUser);
    }
}
