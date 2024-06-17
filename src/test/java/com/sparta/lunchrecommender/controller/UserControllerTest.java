package com.sparta.lunchrecommender.controller;

import com.sparta.lunchrecommender.domain.user.constant.UserStatus;
import com.sparta.lunchrecommender.domain.user.controller.UserController;
import com.sparta.lunchrecommender.domain.user.dto.PasswordRequestDto;
import com.sparta.lunchrecommender.domain.user.dto.UserRequestDto;
import com.sparta.lunchrecommender.domain.user.entity.User;
import com.sparta.lunchrecommender.domain.user.service.UserService;
import com.sparta.lunchrecommender.global.dto.HttpResponseDto;
import com.sparta.lunchrecommender.global.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserRequestDto userRequestDto;
    private PasswordRequestDto passwordRequestDto;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto(
                "testUser",
                "Asdf12345@",
                "TestUser",
                "Test User",
                "test@example.com",
                "Hello, I am a tester."
        );

        passwordRequestDto = new PasswordRequestDto();
        passwordRequestDto.setPassword("password123");

        userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUser()).thenReturn(new User("testUser", "password123", "Test User", "Tester", "test@example.com", "Hello, I am a tester.", UserStatus.ACTIVE));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
    }

    @Test
    @DisplayName("회원 가입 테스트 : ")
    void signup() {
        // given
        String expectedUrl = "https://localhost:443/api/auth/confirm?token=12345";
        when(userService.signup(any(UserRequestDto.class))).thenReturn(expectedUrl);

        // when
        ResponseEntity<HttpResponseDto> response = userController.signup(userRequestDto);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("회원가입 성공, 메일을 인증해주세요.", response.getBody().getMessage());
        assertEquals(expectedUrl, response.getBody().getData());

        ArgumentCaptor<UserRequestDto> userRequestCaptor = ArgumentCaptor.forClass(UserRequestDto.class);
        verify(userService).signup(userRequestCaptor.capture());
        assertEquals("testUser", userRequestCaptor.getValue().getLoginId());
    }




}