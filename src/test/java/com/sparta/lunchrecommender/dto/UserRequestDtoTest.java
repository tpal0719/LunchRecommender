package com.sparta.lunchrecommender.dto;

import com.sparta.lunchrecommender.domain.user.constant.UserStatus;
import com.sparta.lunchrecommender.domain.user.dto.UserRequestDto;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import com.sparta.lunchrecommender.domain.user.entity.User;

import jakarta.validation.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRequestDtoTest {

    private Validator validator;
    User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = new User(
                "abcd12345",
                "Asdf12345@",
                "박세미",
                "semi",
                "test@example.com",
                "안녕하세요.",
                UserStatus.UNAUTHORIZED
        );
    }


    @Nested
    @DisplayName("회원가입 테스트")
    class SignUpTests {

        @Test
        @DisplayName("회원가입 성공")
        void signupSuccessful() {
            User compareUser = new User(
                    "abcd12345",
                    "Asdf12345@",
                    "박세미",
                    "semi",
                    "test@example.com",
                    "안녕하세요.",
                    UserStatus.UNAUTHORIZED
            );

            assertEquals(compareUser.getLoginId(), user.getLoginId());
            assertEquals(compareUser.getPassword(), user.getPassword());
            assertEquals(compareUser.getName(), user.getName());
            assertEquals(compareUser.getNickname(), user.getNickname());
            assertEquals(compareUser.getEmail(), user.getEmail());
            assertEquals(compareUser.getIntro(), user.getIntro());
            assertEquals(UserStatus.UNAUTHORIZED.getStatus(), user.getStatus());
            assertNotNull(user.getStatusModifiedAt());
        }

        @Test
        @DisplayName("회원가입 실패 : 아이디 양식")
        void signupFailed_idError() {
            UserRequestDto userRequestDto = new UserRequestDto(
                    "aaa",
                    "Asdf12345@",
                    "박세미",
                    "semi",
                    "test@example.com",
                    "안녕하세요."
            );
            Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

            //then
            Assertions.assertFalse(violations.isEmpty(),"아이디 양식 오류");
        }


        @Test
        @DisplayName("회원가입 실패 : 비밀번호 양식")
        void signupFailed_PasswordError() {
            UserRequestDto userRequestDto = new UserRequestDto(
                    "abcd12345",
                    "123456789",
                    "박세미",
                    "semi",
                    "test@example.com",
                    "안녕하세요."
            );
            Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

            //then
            Assertions.assertFalse(violations.isEmpty(),"비밀번호 양식 오류");
        }
    }




    @Test
    @DisplayName("USER STATE 변경(탈퇴)")
    void userStatusChange(){
        // 상태 변경 전
        assertEquals(UserStatus.UNAUTHORIZED.getStatus(), user.getStatus());

        // 상태 변경 후
        user.setStatus(UserStatus.ACTIVE);
        assertEquals(UserStatus.ACTIVE.getStatus(), user.getStatus());
        assertNotNull(user.getStatusModifiedAt());
    }

}