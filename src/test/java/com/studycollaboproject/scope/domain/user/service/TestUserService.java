package com.studycollaboproject.scope.domain.user.service;

import com.studycollaboproject.scope.domain.user.dto.SignupTestDto;
import com.studycollaboproject.scope.domain.user.dto.UserRequestDto;
import com.studycollaboproject.scope.domain.user.dto.UserResponseDto;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.domain.user.repository.UserRepository;
import com.studycollaboproject.scope.global.util.TechStackConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@ActiveProfiles("dev")
public class TestUserService {

    @Autowired
    private UserService userService;

    User user;
    List<String> techStack;

    @BeforeEach
    public void setUp() {
        user = new User(
            "testSns",
            "testPropensityType",
            "test",
            "test",
            "test@test.com");

        techStack = new ArrayList<>();
        techStack.add("Java");
        techStack.add("Spring Boot");

    }

    // 1. UserSerivce saveOnlyUser() -> 만들고
    // 2. Test : DB에 값이 정확하게 들어가지는지
    // 2-1 User stub 객체 만들기
    // 2-2 TechStack stub 객체 만들기


    @Test
    public void testSaveUser() {
        // 테스트에 필요한 가짜 데이터 생성
        UserResponseDto userResponseDto = userService.saveUser(techStack, user);

        assertEquals(user.getNickname(), userResponseDto.getNickname());

    }
}
