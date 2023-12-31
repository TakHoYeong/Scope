package com.studycollaboproject.scope.domain.applicant.service;

import com.studycollaboproject.scope.domain.applicant.model.Applicant;
import com.studycollaboproject.scope.domain.applicant.repository.ApplicantRepository;
import com.studycollaboproject.scope.domain.team.dto.MemberListResponseDto;
import com.studycollaboproject.scope.global.error.exception.ErrorCode;
import com.studycollaboproject.scope.global.error.exception.BadRequestException;
import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.post.model.ProjectStatus;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.domain.post.repository.PostRepository;
import com.studycollaboproject.scope.domain.team.repository.TeamRepository;
import com.studycollaboproject.scope.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicantService {
    private final ApplicantRepository applicantRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public Applicant applyPost(String snsId, Long postId, String comment) {

        // [예외처리] 요청한 유저의 정보가 탈퇴 등과 같은 이유로 존재하지 않을 때
        User user = userRepository.findBySnsId(snsId).orElseThrow(
                () -> new BadRequestException(ErrorCode.NO_USER_ERROR)
        );
        // [예외처리] 조회하고자 하는 게시물이 삭제 등과 같은 이유로 존재하지 않을 때
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException(ErrorCode.NO_POST_ERROR)
        );
        // [예외처리] 신청했던 프로젝트에 다시 신청하는 경우
        if(teamRepository.existsByPostIdAndUserSnsId(postId, snsId)){
            throw new BadRequestException(ErrorCode.ALREADY_TEAM_ERROR);
        }
        if(applicantRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            throw new BadRequestException(ErrorCode.ALREADY_APPLY_POST_ERROR);
        }
        // [예외처리] 이미 시작한 프로젝트에 신청할 경우
        if (!post.getProjectStatus().equals(ProjectStatus.PROJECT_STATUS_RECRUITMENT)){
            throw new BadRequestException(ErrorCode.ALREADY_STARTED_ERROR);
        }

        Applicant applicant = Applicant.builder()
                .user(user)
                .post(post)
                .comment(comment)
                .build();

        return applicantRepository.save(applicant);
    }

    @Transactional
    public void cancelApply(String snsId, Long postId) {

        // [예외처리] 요청한 유저의 정보가 탈퇴 등과 같은 이유로 존재하지 않을 때
        User user = userRepository.findBySnsId(snsId).orElseThrow(
                () -> new BadRequestException(ErrorCode.NO_USER_ERROR)
        );
        // [예외처리] 조회하고자 하는 게시물이 삭제 등과 같은 이유로 존재하지 않을 때
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException(ErrorCode.NO_POST_ERROR)
        );
        // [예외처리] 대기열에 대기 정보가 없을 때
        Applicant applicant = applicantRepository.findByUserAndPost(user, post).orElseThrow(
                () -> new BadRequestException(ErrorCode.NO_APPLICANT_ERROR)
        );

        applicant.deleteApply();
        applicantRepository.delete(applicant);
    }

    @Transactional
    public List<MemberListResponseDto> getApplicant(Post post) {
        return applicantRepository.findAllByPost(post)
                .stream().map(MemberListResponseDto::new).collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean isApplicant(Post post, User user) {
        return applicantRepository.findByUserAndPost(user,post).isPresent();
    }
}
