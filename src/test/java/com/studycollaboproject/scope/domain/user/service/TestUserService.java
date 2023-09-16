package com.studycollaboproject.scope.domain.user.service;

import com.studycollaboproject.scope.domain.user.dto.UserResponseDto;
import com.studycollaboproject.scope.domain.user.repository.UserRepository;
import com.studycollaboproject.scope.global.util.TechStackConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TestUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TechStackConverter techStackConverter;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);


    }

    // 1. UserSerivce saveOnlyUser() -> 만들고
    // 2. Test : DB에 값이 정확하게 들어가지는지
    // 2-1 User stub 객체 만들기
    // 2-2 TechStack stub 객체 만들기


    @Test
    public void testSaveUser() {
        // 테스트에 필요한 가짜 데이터 생성
        User user = new User();
        List<String> techStack = new ArrayList<>();
        techStack.add("Java");
        techStack.add("Spring Boot");

        // userRepository.save() 메서드의 모의 동작 설정
        when(userRepository.save(user)).thenReturn(user);

        // techStackConverter.convertStringToTechStack() 메서드의 모의 동작 설정
        when(techStackConverter.convertStringToTechStack(techStack, user, null)).thenReturn(new ArrayList<>());

        // UserService의 saveUser() 메서드 호출
        UserResponseDto savedUserResponse = userService.saveUser(techStack, user);

        // 테스트 결과 검증
        assertEquals(user, savedUserResponse.getUser());
        // 추가적인 검증을 수행할 수 있습니다.
    }

}
