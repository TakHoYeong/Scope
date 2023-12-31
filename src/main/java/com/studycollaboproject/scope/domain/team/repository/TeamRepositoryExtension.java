package com.studycollaboproject.scope.domain.team.repository;

import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.team.model.Team;
import com.studycollaboproject.scope.domain.user.model.User;

import java.util.List;

public interface TeamRepositoryExtension {
    List<Team> findAllByUser(User user);

    List<Team> findAssessmentTeamMember(Post post, List<Long> userIds);

    List<Team> findAllByPostId(Long postId);

    boolean existsByPostIdAndUserSnsId(Long postId, String userSnsId);
}