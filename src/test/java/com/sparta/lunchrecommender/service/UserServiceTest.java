package com.sparta.lunchrecommender.service;

import com.sparta.lunchrecommender.domain.auth.repository.VerificationTokenRepository;
import com.sparta.lunchrecommender.domain.user.constant.UserStatus;
import com.sparta.lunchrecommender.domain.user.dto.UserRequestDto;
import com.sparta.lunchrecommender.domain.user.entity.User;
import com.sparta.lunchrecommender.domain.user.repository.UserRepository;
import com.sparta.lunchrecommender.domain.user.service.UserService;
import com.sparta.lunchrecommender.global.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserUtil userUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    private UserService userService;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserRequestDto userRequestDto;

    @BeforeEach
    void initData(){
        userRequestDto = new UserRequestDto(
                "abcd12345",
                "Asdf12345@",
                "이름",
                "닉네임",
                "testemail@gmail.com",
                "안녕하세요.");
    }


    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() {
        // given
        when(userRepository.findByLoginId(userRequestDto.getLoginId())).thenReturn(Optional.empty());

        // when
        String resultUrl = userService.signup(userRequestDto);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(UserStatus.UNAUTHORIZED.getStatus(), savedUser.getStatus());
        assertEquals(userRequestDto.getLoginId(), savedUser.getLoginId());
        assertEquals(userRequestDto.getEmail(), savedUser.getEmail());

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals("testemail@gmail.com", sentMessage.getTo()[0]);
        assertEquals("LunchRecommender 회원가입 메일 인증", sentMessage.getSubject());

        String expectedText = "메일 인증을 받으려면 링크를 클릭하세요: " + resultUrl;
        assertEquals(expectedText, sentMessage.getText());
    }

    @Test
    @DisplayName("회원가입 실패 : 이미 있는 유저")
    void signupFailed__DuplicateUser() {
        // given
        User existingUser = new User();
        when(userRepository.findByLoginId(userRequestDto.getLoginId())).thenReturn(Optional.of(existingUser));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.signup(userRequestDto));
        assertEquals("이미 등록된 회원입니다.", exception.getMessage());
    }

}