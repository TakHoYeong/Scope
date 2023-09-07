package com.studycollaboproject.scope.domain.applicant.controller;

import com.studycollaboproject.scope.domain.applicant.service.ApplicantService;
import com.studycollaboproject.scope.domain.applicant.dto.ApplicantRequestDto;
import com.studycollaboproject.scope.domain.applicant.model.Applicant;
import com.studycollaboproject.scope.global.common.mail.MailDto;
import com.studycollaboproject.scope.domain.team.dto.MemberListResponseDto;
import com.studycollaboproject.scope.global.common.ResponseDto;
import com.studycollaboproject.scope.global.error.exception.ErrorCode;
import com.studycollaboproject.scope.global.error.exception.ForbiddenException;
import com.studycollaboproject.scope.global.error.exception.NoAuthException;
import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.global.security.UserDetailsImpl;
import com.studycollaboproject.scope.global.common.mail.MailService;
import com.studycollaboproject.scope.domain.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;


@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Applicant Controller", description = "모집 지원하기 및 지원취소")
public class ApplicantRestController {

    private final ApplicantService applicantService;
    private final PostService postService;
    private final MailService mailService;

    @Operation(summary = "모집 지원하기")
    @PostMapping("/api/applicant/{postId}")
    public ResponseEntity<Object> apply(@Parameter(in = ParameterIn.PATH, description = "프로젝트 ID") @PathVariable Long postId,
                                        @RequestBody ApplicantRequestDto requestDto,
                                        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws MessagingException {
        String snsId = Optional.ofNullable(userDetails).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        ).getSnsId();

        Applicant applicant = applicantService.applyPost(snsId, postId, requestDto.getComment());
        mailService.applicantMailBuilder(new MailDto(applicant));
        return new ResponseEntity<>(
                new ResponseDto("프로젝트에 지원되었습니다.", ""),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "모집 지원취소")
    @DeleteMapping("/api/applicant/{postId}")
    public ResponseEntity<Object> cancelApply(@Parameter(in = ParameterIn.PATH, description = "프로젝트 ID") @PathVariable Long postId,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String snsId = Optional.ofNullable(userDetails).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        ).getSnsId();
        applicantService.cancelApply(snsId, postId);

        return new ResponseEntity<>(
                new ResponseDto("프로젝트 지원이 취소되었습니다.", ""),
                HttpStatus.OK
        );

    }

    @Operation(summary = "모집 현황")
    @GetMapping("/api/applicant/{postId}")
    public ResponseEntity<Object> getApplicant(@Parameter(in = ParameterIn.PATH, description = "프로젝트 ID") @PathVariable Long postId,
                                               @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String snsId = Optional.ofNullable(userDetails).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        ).getSnsId();

        Post post = postService.loadPostByPostId(postId);
        Optional.ofNullable(post).map(Post::getUser).map(User::getSnsId).filter(o -> o.equals(snsId)).orElseThrow(
                () -> new ForbiddenException(ErrorCode.NO_AUTHORIZATION_ERROR)
        );

        List<MemberListResponseDto> responseDto = applicantService.getApplicant(post);
        return new ResponseEntity<>(
                new ResponseDto("모집 지원 현황 조회 성공", responseDto),
                HttpStatus.OK
        );

    }
}
