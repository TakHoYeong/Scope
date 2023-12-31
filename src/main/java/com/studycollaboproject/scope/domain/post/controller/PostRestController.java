package com.studycollaboproject.scope.domain.post.controller;

import com.studycollaboproject.scope.domain.post.dto.*;
import com.studycollaboproject.scope.domain.team.dto.MemberListResponseDto;
import com.studycollaboproject.scope.global.common.ResponseDto;
import com.studycollaboproject.scope.global.error.exception.ErrorCode;
import com.studycollaboproject.scope.global.error.exception.NoAuthException;
import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.domain.user.model.UserStatus;
import com.studycollaboproject.scope.global.security.UserDetailsImpl;
import com.studycollaboproject.scope.domain.applicant.service.ApplicantService;
import com.studycollaboproject.scope.domain.post.service.PostService;
import com.studycollaboproject.scope.domain.team.service.TeamService;
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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Post Controller", description = "프로젝트 조회, 삭제, 작성 및 수정")
public class PostRestController {

    private final PostService postService;
    private final TeamService teamService;
    private final ApplicantService applicantService;

    @Operation(summary = "프로젝트 작성")
    @PostMapping("/api/post")
    @CacheEvict(value = "Post", allEntries=true)
    public ResponseEntity<Object> writePost(@Valid @RequestBody PostRequestDto postRequestDto,
                                            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // [예외처리] 로그인 정보가 없을 때
        String snsId = Optional.ofNullable(userDetails).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        ).getSnsId();

        Post writePost = postService.writePost(postRequestDto, snsId);
        return new ResponseEntity<>(
                new ResponseDto("게시물이 성공적으로 저장되었습니다.", new PostResponseDto(writePost)),
                HttpStatus.CREATED
        );

    }

    @Operation(summary = "프로젝트 조회")
    @GetMapping("/api/post")
    public ResponseEntity<Object> readPost(@Parameter(description = "필터", in = ParameterIn.QUERY, example = ";;;;;;;;;;;;;;") @RequestParam String filter,
                                           @Parameter(description = "정렬 기준", in = ParameterIn.QUERY, example = "createdAt") @RequestParam String sort,
                                           @Parameter(description = "북마크 / 추천", in = ParameterIn.QUERY, example = "bookmark", allowEmptyValue = true) @RequestParam String bookmarkRecommend,
                                           @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String snsId = "";

        if (bookmarkRecommend.equals("recommend") || bookmarkRecommend.equals("bookmark")) {
            snsId = Optional.ofNullable(userDetails).orElseThrow(
                    () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
            ).getSnsId();
        }

        List<PostResponseDto> postResponseDtos = postService.readPost(filter, sort, snsId, bookmarkRecommend);
        return new ResponseEntity<>(
                new ResponseDto("프로젝트 조회 성공", postResponseDtos),
                HttpStatus.OK
        );

    }

    @Operation(summary = "프로젝트 페이지 조회")
    @GetMapping("/api/post-page")
    public ResponseEntity<Object> readPostPage(@Parameter(description = "필터", in = ParameterIn.QUERY, example = ";;;;;;;;;;;;;;") @RequestParam String filter,
                                           @Parameter(description = "정렬 기준", in = ParameterIn.QUERY, example = "createdAt") @RequestParam String sort,
                                           @Parameter(description = "페이지 번호", in = ParameterIn.QUERY, example = "1") @RequestParam int page,
                                           @Parameter(description = "북마크 / 추천", in = ParameterIn.QUERY, example = "bookmark", allowEmptyValue = true) @RequestParam String bookmarkRecommend,
                                           @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String snsId = "";

        if (bookmarkRecommend.equals("recommend") || bookmarkRecommend.equals("bookmark")) {
            snsId = Optional.ofNullable(userDetails).orElseThrow(
                    () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
            ).getSnsId();
        }

        List<PostResponseDto> postResponseDtos = postService.readPostPage(filter, sort, page - 1, snsId, bookmarkRecommend);
        return new ResponseEntity<>(
                new ResponseDto("프로젝트 조회 성공", postResponseDtos),
                HttpStatus.OK
        );

    }

    @Operation(summary = "프로젝트 수정")
    @PostMapping("/api/post/{postId}")
    @CacheEvict(value = "Post", allEntries=true)
    public ResponseEntity<Object> editPost(
            @Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId,
            @RequestBody PostRequestDto postRequestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // [예외처리] 로그인 정보가 없을 때
        String snsId = Optional.ofNullable(userDetails).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        ).getSnsId();

        PostResponseDto responseDto = postService.editPost(postId, postRequestDto, snsId);
        return new ResponseEntity<>(
                new ResponseDto("게시물이 성공적으로 수정되었습니다.", responseDto),
                HttpStatus.OK
        );
    }

    @Operation(summary = "프로젝트 삭제")
    @DeleteMapping("/api/post/{postId}")
    @CacheEvict(value = "Post", allEntries=true)
    public ResponseEntity<Object> deletePost(
            @Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String snsId = Optional.ofNullable(userDetails).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        ).getSnsId();

        Long deletedId = postService.deletePost(postId, snsId);
        Map<String, Long> map = new HashMap<>();
        map.put("postId", deletedId);
        return new ResponseEntity<>(
                new ResponseDto("프로젝트가 성공적으로 삭제되었습니다.", map),
                HttpStatus.OK
        );
    }

    @Operation(summary = "프로젝트 상태 변경")
    @PostMapping("/api/post/{postId}/status")
    @CacheEvict(value = "Post", allEntries=true)
    public ResponseEntity<Object> updatePostStatus(@Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId,
                                                   @RequestBody ProjectStatusRequestDto requestDto,
                                                   @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String snsId = Optional.ofNullable(userDetails).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        ).getSnsId();

        PostResponseDto responseDto = postService.updateStatus(postId, requestDto.getProjectStatus(), snsId);
        return new ResponseEntity<>(
                new ResponseDto("프로젝트 상태가 성공적으로 수정되었습니다.", responseDto),
                HttpStatus.OK
        );
    }

    @Operation(summary = "프로젝트 상세 조회")
    @GetMapping("/api/post/{postId}")
    public ResponseEntity<Object> getPost(@Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId,
                                          @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<MemberListResponseDto> member = teamService.getMember(postId);

        boolean isBookmarkChecked = false;
        String userStatus;
        Post post = postService.loadPostByPostId(postId);

        if (userDetails != null) {
            User user = userDetails.getUser();
            if (postService.isTeamStarter(post, user.getSnsId())) {
                userStatus = UserStatus.USER_STATUS_TEAM_STARTER.getUserStatus();
            } else if (teamService.isMember(post, user)) {
                userStatus = UserStatus.USER_STATUS_MEMBER.getUserStatus();
            } else if (applicantService.isApplicant(post, user)) {
                userStatus = UserStatus.USER_STATUS_APPLICANT.getUserStatus();
            } else {
                userStatus = UserStatus.USER_STATUS_USER.getUserStatus();
            }
            isBookmarkChecked = postService.hasPostFromUserBookmarkList(post, user.getSnsId());
        } else {
            userStatus = UserStatus.USER_STATUS_ANONYMOUS.getUserStatus();
        }
        PostResponseDto postDetail = new PostResponseDto(post, isBookmarkChecked);
        return new ResponseEntity<>(
                new ResponseDto("프로젝트 상세 정보 조회 성공", new PostDetailDto(postDetail, member, userStatus)),
                HttpStatus.OK
        );
    }

    @Operation(summary = "프로젝트 Github URL 업데이트")
    @PostMapping("/api/post/{postId}/url")
    @CacheEvict(value = "Post", allEntries=true)
    public ResponseEntity<Object> updateUrl(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody UrlUpdateRequestDto requestDto,
                                            @Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long postId) {
        String snsId = Optional.ofNullable(userDetails).orElseThrow(
                () -> new NoAuthException(ErrorCode.NO_AUTHENTICATION_ERROR)
        ).getSnsId();

        PostResponseDto responseDto = postService.updateUrl(requestDto.getBackUrl(), requestDto.getFrontUrl(), snsId, postId);
        return new ResponseEntity<>(
                new ResponseDto("프로젝트 URL이 성공적으로 저장되었습니다.", responseDto),
                HttpStatus.OK
        );
    }

    @Operation(summary = "프로젝트 검색")
    @GetMapping("/api/post/search")
    public ResponseEntity<Object> searchPost(@Parameter(description = "키워드", in = ParameterIn.QUERY) @RequestParam String keyword,
                                             @Parameter(description = "정렬", in = ParameterIn.QUERY, example = "createdAt") @RequestParam String sort,
//                                             @Parameter(description = "정렬", in = ParameterIn.QUERY, example = "15") @RequestParam int displayNum,
//                                             @Parameter(description = "정렬", in = ParameterIn.QUERY, example = "1") @RequestParam int page,
                                             @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String snsId = Optional.ofNullable(userDetails).map(UserDetailsImpl::getSnsId).orElse("");
        List<PostResponseDto> postList = postService.searchPost(snsId, keyword, sort);
        return new ResponseEntity<>(
                new ResponseDto("프로젝트 검색 성공.", postList),
                HttpStatus.OK
        );
    }
}