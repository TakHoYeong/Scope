package com.studycollaboproject.scope.domain.user.controller;

import com.studycollaboproject.scope.domain.user.dto.MypageResponseDto;
import com.studycollaboproject.scope.domain.user.dto.SignupRequestDto;
import com.studycollaboproject.scope.domain.user.dto.UserRequestDto;
import com.studycollaboproject.scope.domain.user.dto.UserResponseDto;
import com.studycollaboproject.scope.global.common.ResponseDto;
import com.studycollaboproject.scope.global.error.exception.BadRequestException;
import com.studycollaboproject.scope.global.error.exception.ErrorCode;
import com.studycollaboproject.scope.global.error.exception.ForbiddenException;
import com.studycollaboproject.scope.global.error.exception.NoAuthException;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.global.security.UserDetailsImpl;
import com.studycollaboproject.scope.global.common.mail.MailService;
import com.studycollaboproject.scope.domain.post.service.PostService;
import com.studycollaboproject.scope.domain.propensityTest.service.TestService;
import com.studycollaboproject.scope.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "User Controller", description = "회원 가입 및 회원 조회/수정")
public class UserRestController {

    private final PostService postService;
    private final UserService userService;
    private final TestService testService;
    private final MailService mailService;


    @Operation(summary = "마이 페이지")
    @GetMapping("/api/user/{userId}")
    public ResponseEntity<Object> getMyPage(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Parameter(description = "조회하고자 하는 사용자의 ID", in = ParameterIn.PATH) @PathVariable Long userId) {
        User user = userService.loadUserByUserId(userId);

        String snsId = Optional.ofNullable(userDetails).map(UserDetailsImpl::getSnsId).orElse("");
        MypageResponseDto responseDto = postService.getMyPostList(user, snsId);

        return new ResponseEntity<>(
                new ResponseDto("회원 정보 조회 성공", responseDto),
                HttpStatus.OK
        );
    }

    @Operation(summary = "회원 정보 수정")
    @PostMapping("/api/user/{userId}")
    public ResponseEntity<Object> updateUserinfo(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestBody UserRequestDto userRequestDto, @Parameter(description = "수정하고자 하는 사용자의 ID", in = ParameterIn.PATH) @PathVariable Long userId) {
        if (userId.equals(userDetails.getUser().getId())) {
            UserResponseDto userResponseDto = userService.updateUserInfo(userDetails.getUsername(), userRequestDto);
            return new ResponseEntity<>(
                    new ResponseDto("회원 정보가 수정되었습니다.", userResponseDto),
                    HttpStatus.OK
            );
        }
        // [예외처리] 로그인 정보가 없을 때
        else {
            throw new ForbiddenException(ErrorCode.NO_AUTHORIZATION_ERROR);
        }
    }

    @Operation(summary = "회원 가입 - 회원 정보와 테스트 결과 저장")
    @PostMapping("/api/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {

        String userTestResult = testService.testResult(signupRequestDto.getUserPropensityType());
        String memberTestResult = testService.testResult(signupRequestDto.getMemberPropensityType());
        User user = new User(signupRequestDto, userTestResult, memberTestResult);
        String token = userService.createToken(user);
        UserResponseDto userResponseDto = userService.saveUser(signupRequestDto.getTechStack(), user);

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", userResponseDto);

        return new ResponseEntity<>(
                new ResponseDto("회원가입이 되었습니다.", map),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "이메일 중복 확인")
    @GetMapping("/api/login/email")
    public ResponseEntity<Object> emailCheck(@Parameter(description = "이메일", in = ParameterIn.QUERY) @RequestParam String email) {

        if (userService.emailCheckByEmail(email)) {
            throw new BadRequestException(ErrorCode.ALREADY_EMAIL_ERROR);
        }
        return new ResponseEntity<>(
                new ResponseDto("사용 가능한 메일입니다.", ""),
                HttpStatus.OK
        );
    }

    @Operation(summary = "닉네임 중복 확인")
    @GetMapping("/api/login/nickname")
    public ResponseEntity<Object> nicknameCheck(@Parameter(description = "닉네임", in = ParameterIn.QUERY) @RequestParam String nickname) {
        if (nickname.length() < 2 || nickname.length() > 5) {
            throw new BadRequestException(ErrorCode.INVALID_INPUT_ERROR);
        }
        if (userService.nicknameCheckByNickname(nickname)) {
            throw new BadRequestException(ErrorCode.ALREADY_NICKNAME_ERROR);
        }
        return new ResponseEntity<>(
                new ResponseDto("사용가능한 닉네임입니다.", ""),
                HttpStatus.OK
        );
    }

    @Operation(summary = "유저 소개 업데이트")
    @PostMapping("/api/user/{userId}/desc")
    public ResponseEntity<Object> updateUserDesc(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestBody String introduction, @Parameter(description = "수정하고자 하는 사용자의 ID", in = ParameterIn.PATH) @PathVariable Long userId) {
        if (userId.equals(userDetails.getUser().getId())) {
            UserResponseDto userResponseDto = userService.updateUserDesc(userDetails.getUsername(), introduction);
            return new ResponseEntity<>(
                    new ResponseDto("회원 소개가 수정되었습니다.", userResponseDto),
                    HttpStatus.OK
            );
        }
        // [예외처리] 로그인 정보가 없을 때
        else throw new ForbiddenException(ErrorCode.NO_AUTHORIZATION_ERROR);

    }

    @Operation(summary = "북마크 추가")
    @PostMapping("/api/bookmark/{postId}")
    public ResponseEntity<Object> bookmarkCheck(@Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId,
                                                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails.getUser() == null) {
            throw new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR);
        }
        return new ResponseEntity<>(
                postService.bookmarkPost(postId, userDetails.getSnsId()),
                HttpStatus.CREATED
        );
    }

    @CacheEvict(value = "Post", allEntries = true)
    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("api/user/{userId}")
    public ResponseEntity<Object> deleteUser(@Parameter(description = "탈퇴하려는 회원 ID", in = ParameterIn.PATH) @PathVariable Long userId,
                                             @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails.getUser().getId().equals(userId)) {
            return new ResponseEntity<>(
                    userService.deleteUser(userDetails.getUser()),
                    HttpStatus.OK
            );
        }
        // [예외처리] 로그인 정보가 없을 때
        else {
            throw new ForbiddenException(ErrorCode.NO_AUTHORIZATION_ERROR);
        }
    }

    @Operation(summary = "이메일 인증 전송")
    @GetMapping("api/user/email")
    public ResponseEntity<Object> emailAuthentication(@Parameter(description = "이메일", in = ParameterIn.QUERY) @RequestParam String email,
                                                      @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws MessagingException {

        User user = userService.setEmailAuthCode(userDetails.getSnsId());
        mailService.authMailSender(email, user);

        return new ResponseEntity<>(
                new ResponseDto("이메일이 전송되었습니다.", ""),
                HttpStatus.OK
        );
    }


}
