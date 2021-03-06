package com.where.WhereYouAt.service;

import com.where.WhereYouAt.annotation.Decode;
import com.where.WhereYouAt.controller.dto.user.LoginRequestDto;
import com.where.WhereYouAt.controller.dto.user.LoginResponseDto;
import com.where.WhereYouAt.controller.dto.user.UserDto;
import com.where.WhereYouAt.domain.User;
import com.where.WhereYouAt.domain.dto.Birthday;
import com.where.WhereYouAt.domain.utils.JwtUtil;
import com.where.WhereYouAt.domain.utils.Uploader;
import com.where.WhereYouAt.exception.*;
import com.where.WhereYouAt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final Uploader uploader;

    private final JwtUtil jwtUtil;


    //회원 조회
    public User getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("계정이 존재하지 않습니다"));
        return user;
    }

    //프로필 이미지 조회
    public User getMyinfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(NotExistedUserIdException::new);

        return user;
    }

    //회원가입
    public User createUser(UserDto userDto){
        Optional<User> existedUser = userRepository.findByUserId(userDto.getUserId());
        //이미 아이디가 있다면 error 처리
        if(existedUser.isPresent()){
            throw new AlreadyExistedUserIdException();
        }
        Optional<User> existedUsernick = userRepository.findByNickname(userDto.getNickname());
        if(existedUsernick.isPresent()){
            throw new AlreadyExistedNicknameException();
        }

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        User user = User.builder()
                .userId(userDto.getUserId())
                .nickname(userDto.getNickname())
                .gender(userDto.getGender())
                .birthday(Birthday.of(LocalDate.parse(userDto.getBirthday())))
                .build();
        user.setPassword(encodedPassword);
        user.setProfileImg("https://gogoeverybodyy.s3.ap-northeast-2.amazonaws.com/static/gogo.profile.png");
        return userRepository.save(user);
    }

    //회원 탈퇴
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(NotExistedUserIdException::new);

        userRepository.delete(user);
    }

    //로그인 인증
    @Decode
    public LoginResponseDto authentication(LoginRequestDto dto){
        User user = userRepository.findByUserId(dto.getUserId())
                .orElseThrow(NotExistedUserIdException::new);

        if(!passwordEncoder.matches(dto.getPassword(),user.getPassword())){
            throw new PasswordWrongException();
        }

        String jwt = jwtUtil.createToken(user.getId(),user.getNickname());

        return LoginResponseDto.builder().jwt(jwt).nickname(user.getNickname()).build();
    }

    //아이디 중복 확인
    public void checkUserId(String userId) {
        Optional<User> user = userRepository.findByUserId(userId);

        if(user.isPresent()){
            throw new AlreadyExistedUserIdException();
        }
    }

    //닉네임 중복 확인
    public void checkNickname(String nickname){
        Optional<User> user = userRepository.findByNickname(nickname);

        if(user.isPresent()){
            throw new AlreadyExistedUserIdException();
        }
    }

    //프로필 수정
    public void modifyUser(Long userId, String nickname) {
        //todo: user 검사
        Optional<User> checkingUser = userRepository.findByNickname(nickname);
        if(checkingUser.isPresent()){
            throw new AlreadyExistedNicknameException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(NotExistedUserIdException::new);

        user.setNickname(nickname);
    }

    //프로필 이미지 업로드
    public String uploadImg(Long userId, MultipartFile file) throws IOException{
        User user = userRepository.findById(userId)
                .orElseThrow(NotExistedUserIdException::new);

        if(file.isEmpty()){
            throw new InCorrectInformationException();
        }

        user.setProfileImg(uploader.upload(file,"static"));
        return uploader.upload(file,"static");
    }
}
