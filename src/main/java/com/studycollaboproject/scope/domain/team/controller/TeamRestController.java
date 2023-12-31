package com.studycollaboproject.scope.domain.team.controller;

import com.studycollaboproject.scope.global.common.mail.MailDto;
import com.studycollaboproject.scope.domain.team.dto.MemberListResponseDto;
import com.studycollaboproject.scope.global.common.ResponseDto;
import com.studycollaboproject.scope.domain.team.dto.TeamRequestDto;
import com.studycollaboproject.scope.global.error.exception.ErrorCode;
import com.studycollaboproject.scope.global.error.exception.ForbiddenException;
import com.studycollaboproject.scope.global.error.exception.NoAuthException;
import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.global.security.UserDetailsImpl;
import com.studycollaboproject.scope.global.common.mail.MailService;
import com.studycollaboproject.scope.domain.post.service.PostService;
import com.studycollaboproject.scope.domain.team.service.TeamService;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Team Controller", description = "팀원 조회 및 팀원 승인")
public class TeamRestController {

    private final TeamService teamService;
    private final UserService userService;
    private final PostService postService;
    private final MailService mailService;

    @CacheEvict(value = "Post", allEntries=true)
    @Operation(summary = "팀원 승인/거절")
    @PostMapping("/api/team/{postId}")
    public ResponseEntity<Object> acceptMember(@Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId,
                                               @RequestBody TeamRequestDto requestDto,
                                               @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws MessagingException {
        // [예외처리] 로그인 정보가 없을 때
        String snsId = Optional.ofNullable(userDetails).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        ).getSnsId();

        //로그인 사용자 정보 불러오기
        User user = userService.loadUserBySnsId(snsId);
        //로그인 사용자가 해당 프로젝트의 생성자 인지 확인
        Post post = postService.loadPostIfOwner(postId, user);
        //지원자 정보 확인
        User applyUser = userService.loadUserByUserId(requestDto.getUserId());
        //지원자 승인/거절
        //지원자 승인시 승인된 지원자에게 알림 메일 발송
        if (teamService.acceptMember(post, applyUser, requestDto.isAccept())) {
            mailService.acceptTeamMailBuilder(new MailDto(applyUser,post));
        }
        //지원지 목록 출력
        List<MemberListResponseDto> responseDto = teamService.getMember(postId);
        return new ResponseEntity<>(
                new ResponseDto("신청 상태가 변경되었습니다.", responseDto),
                HttpStatus.OK
        );
    }

    @Operation(summary = "팀원 조회")
    @GetMapping("/api/team/{postId}")
    public ResponseEntity<Object> getMember(@Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId) {
        List<MemberListResponseDto> responseDto = teamService.getMember(postId);
        return new ResponseEntity<>(
                new ResponseDto("팀원을 조회하였습니다.", responseDto),
                HttpStatus.OK
        );
    }

    @CacheEvict(value = "Post", allEntries=true)
    @Operation(summary = "팀원 강퇴")
    @DeleteMapping("/api/team/resignation/{postId}")
    public ResponseEntity<Object> memberResignation(@Parameter(description = "유저 ID", in = ParameterIn.QUERY) @RequestParam Long userId,
                                                    @Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId,
                                                    @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Post post = postService.loadPostByPostId(postId);
        User user = Optional.ofNullable(userDetails).map(UserDetailsImpl::getUser).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        );

        if (post.getUser().getId().equals(user.getId())) {
            User member = userService.loadUserByUserId(userId);
            teamService.memberDelete(member, post);
            return new ResponseEntity<>(
                    new ResponseDto("팀에서 팀원을 삭제했습니다.", ""),
                    HttpStatus.OK
            );
            // [예외처리] 팀원 강퇴를 요청한 사용자가 게시물 작성자가 아닐 때
        } else throw new ForbiddenException(ErrorCode.NO_AUTHORIZATION_ERROR);
    }

    @CacheEvict(value = "Post", allEntries=true)
    @Operation(summary = "팀 탈퇴")
    @DeleteMapping("/api/team/secession/{postId}")
    public ResponseEntity<Object> memberSecession(@Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Post post = postService.loadPostByPostId(postId);
        User user = Optional.ofNullable(userDetails).map(UserDetailsImpl::getUser).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        );
        teamService.memberDelete(user, post);
        return new ResponseEntity<>(
                new ResponseDto("팀에서 나왔습니다.", ""),
                HttpStatus.OK
        );
    }

}
