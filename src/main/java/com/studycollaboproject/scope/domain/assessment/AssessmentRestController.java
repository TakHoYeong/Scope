package com.studycollaboproject.scope.domain.assessment;

import com.studycollaboproject.scope.global.common.mail.MailDto;
import com.studycollaboproject.scope.global.common.ResponseDto;
import com.studycollaboproject.scope.global.error.exception.ErrorCode;
import com.studycollaboproject.scope.global.error.exception.NoAuthException;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.global.security.UserDetailsImpl;
import com.studycollaboproject.scope.global.common.mail.MailService;
import com.studycollaboproject.scope.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Assessment Controller", description = "팀원 평가")
public class AssessmentRestController {

    private final AssessmentService assessmentService;
    private final UserService userService;
    private final MailService mailService;

    @Operation(summary = "팀원 평가")
    @PostMapping("/api/assessment/{postId}")
    public ResponseEntity<Object> assessmentMember(@Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId,
                                                   @RequestBody AssessmentRequestDto requestDto,
                                                   @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws MessagingException {
        // [예외처리] 로그인 정보가 없을 때
        String snsId = Optional.ofNullable(userDetails).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        ).getSnsId();

        User user = userService.loadUserBySnsId(snsId);
        MailDto mailDto = assessmentService.assessmentMember(postId, user, requestDto.getUserIds());

        //팀장을 제외한 팀원들에게 프로젝트 종료와 평가를 알리는 메일 보내기
        mailService.assessmentMailBuilder(mailDto);
        return new ResponseEntity<>(
                new ResponseDto("팀원 평가 정보가 성공적으로 저장되었습니다.", ""),
                HttpStatus.OK
        );

    }
}
