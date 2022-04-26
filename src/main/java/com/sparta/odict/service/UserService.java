package com.sparta.odict.service;

import com.sparta.odict.dto.SignupRequestDto;
import com.sparta.odict.dto.UserRequestDto;
import com.sparta.odict.exception.CustomException;
import com.sparta.odict.exception.ErrorCode;
import com.sparta.odict.model.User;
import com.sparta.odict.model.UserRoleEnum;
import com.sparta.odict.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String ADMIN_TOKEN = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    public void registerUser(SignupRequestDto requestDto) {

        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String pwd = requestDto.getPwd();
        String pwdCheck = requestDto.getPwdCheck();
        String profileImage = requestDto.getProfileImage();
        String age = requestDto.getAge();
        Long postCount = 0L;

        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        //아이디 유효성 검사
        checkUserName(username);
        //닉네임 유효성 검사
        checkNickName(nickname);
        //비밀번호 유효성 검사
        checkUserPw(pwd, pwdCheck);

        // 패스워드 암호화
        String enPassword = passwordEncoder.encode(requestDto.getPwd());
        User user = new User(username, nickname, enPassword, age, profileImage, postCount, role);
        userRepository.save(user); // DB 저장

    }

    // 아이디 유효성 검사
    private void checkUserName(String username) {
        Optional<User> foundByUserName = userRepository.findByUsername(username);
        if (foundByUserName.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_USER_NAME);
        }
        Pattern userNamePattern = Pattern.compile("\\w+@\\w+\\.\\w+(\\.\\w+)?");
        Matcher userNameMatcher = userNamePattern.matcher(username);
        if (username.length() == 0) {
            throw new CustomException(ErrorCode.BLANK_USER_NAME);
        }
        if (!userNameMatcher.matches()) {
            throw new CustomException(ErrorCode.INVALID_PATTERN_USER_NAME);
        }
    }

    //닉네임 유효성 검사
    private void checkNickName(String nickname) {
        Pattern nickNamePattern = Pattern.compile("^\\S{2,8}$");
        Matcher nickNameMatcher = nickNamePattern.matcher(nickname);

        Optional<User> foundByNickName = userRepository.findByNickname(nickname);
        if (foundByNickName.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_USER_NICKNAME);
        }
        if (nickname.length() == 0) {
            throw new CustomException(ErrorCode.BLANK_USER_NICKNAME);
        }
        if (!nickNameMatcher.matches()) {
            throw new CustomException(ErrorCode.INVALID_PATTERN_USER_NICKNAME);
        }
    }

    //비밀번호 유효성 검사
    private void checkUserPw(String pwd, String pwdCheck) {
        Pattern userPwPattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,20}$");
        Matcher userPwMatcher = userPwPattern.matcher(pwd);
        if (pwd.length() == 0) {
            throw new CustomException(ErrorCode.BLANK_USER_PW);
        }
        if (!userPwMatcher.matches()) {
            throw new CustomException(ErrorCode.INVALID_PATTERN_USER_PW);
        }
        // password 일치여부
        if (pwdCheck.length() == 0) {
            throw new CustomException(ErrorCode.BLANK_USER_PW_CHECK);
        }
        if (!pwd.equals(pwdCheck)) {
            throw new CustomException(ErrorCode.NOT_EQUAL_USER_PW_CHECK);
        }
    }

    public User userLogin(UserRequestDto userRequestDto) {
        User user = userRepository.findByUsername(userRequestDto.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );

        // 패스워드 암호화
        if (!passwordEncoder.matches(userRequestDto.getPwd(), user.getPwd())) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER_ID);
        }
        return user;
    }
}