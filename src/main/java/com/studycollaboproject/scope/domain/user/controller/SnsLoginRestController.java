package com.studycollaboproject.scope.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.studycollaboproject.scope.domain.user.dto.SnsInfoDto;
import com.studycollaboproject.scope.global.listener.SessionUserCounter;
import com.studycollaboproject.scope.domain.user.service.GithubUserService;
import com.studycollaboproject.scope.domain.user.service.KakaoUserService;
import com.studycollaboproject.scope.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "SNS Login Controller", description = "소셜 로그인")
public class SnsLoginRestController {

    private final KakaoUserService kakaoUserService;
    private final UserService userService;
    private final GithubUserService githubUserService;
    private final SessionUserCounter userCounter;

    @Operation(summary = "카카오 로그인")
    @GetMapping("/api/login/kakao")
    public ResponseEntity<Object> kakaoLogin(@Parameter(description = "코드 값", in = ParameterIn.QUERY) @RequestParam String code, HttpServletRequest servletRequest) throws JsonProcessingException {
        SnsInfoDto snsInfoDto = kakaoUserService.kakaoLogin(code);
        servletRequest.getSession();
        int count = userCounter.getCount();
        System.out.println("count = " + count);


        return new ResponseEntity<>(
                userService.SignupUserCheck(snsInfoDto.getId(), "K-"),
                HttpStatus.OK
        );
    }

    @Operation(summary = "Github 로그인")
    @GetMapping("/api/login/github")
    public ResponseEntity<Object> githubLogin(@Parameter(description = "코드 값", in = ParameterIn.QUERY) @RequestParam String code, HttpServletRequest servletRequest) throws JsonProcessingException {
        SnsInfoDto snsInfoDto = githubUserService.githubLogin(code);
        servletRequest.getSession();
        int count = userCounter.getCount();
        System.out.println("count = " + count);

        return new ResponseEntity<>(
                userService.SignupUserCheck(snsInfoDto.getId(), "G-"),
                HttpStatus.OK
        );
    }
}
