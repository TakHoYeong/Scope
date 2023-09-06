package com.studycollaboproject.scope.domain.team.service;

import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.post.model.ProjectStatus;
import com.studycollaboproject.scope.domain.applicant.model.Applicant;
import com.studycollaboproject.scope.domain.team.dto.MemberListResponseDto;
import com.studycollaboproject.scope.domain.team.model.Team;
import com.studycollaboproject.scope.domain.team.repository.TeamRepository;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.global.error.exception.BadRequestException;
import com.studycollaboproject.scope.global.error.exception.ErrorCode;
import com.studycollaboproject.scope.global.error.exception.ForbiddenException;
import com.studycollaboproject.scope.domain.applicant.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final ApplicantRepository applicantRepository;

    @Transactional
    public boolean acceptMember(Post post, User user, Boolean accept) {
        // [예외처리] 대기열에서 신청자의 정보를 찾을 수 없을 때
        Applicant applicant = applicantRepository.findByUserAndPost(user, post).orElseThrow(
                () -> new BadRequestException(ErrorCode.NO_APPLICANT_ERROR)
        );
        if (accept && post.getTotalMember() == post.getRecruitmentMember()) {
            throw new BadRequestException(ErrorCode.NO_EXCEED_MEMBER_ERROR);
        }
        //대기열에서 삭제
        applicant.deleteApply();
        applicantRepository.delete(applicant);

        if (teamRepository.existsByPostIdAndUserSnsId(post.getId(), user.getSnsId())) {
            throw new BadRequestException(ErrorCode.ALREADY_TEAM_ERROR);
        }

        if (accept) {
            Team team = Team.builder()
                    .user(user)
                    .post(post)
                    .build();
            teamRepository.save(team);
            return true;
        }
        return false;
    }

    @Transactional
    public List<MemberListResponseDto> getMember(Long postId) {
        return teamRepository.findAllByPostId(postId)
                .stream().map(MemberListResponseDto::new).collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public void memberDelete(User user, Post post) {
        if (post.getUser().getId().equals(user.getId())) {
            throw new BadRequestException(ErrorCode.NOT_AVAILABLE_ACCESS);
        } else if (post.getProjectStatus().equals(ProjectStatus.PROJECT_STATUS_RECRUITMENT)) {
            Team team = teamRepository.findByUserAndPost(user, post).orElseThrow(
                    () -> new ForbiddenException(ErrorCode.NO_TEAM_ERROR)
            );
            team.deleteTeam();
            teamRepository.deleteByUserAndPost(user, post);
        } else throw new BadRequestException(ErrorCode.NO_RECRUITMENT_ERROR);
    }

    public boolean isMember(Post post, User user) {
        return teamRepository.findByUserAndPost(user, post).isPresent();

    }
}
