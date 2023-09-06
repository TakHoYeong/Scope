package com.studycollaboproject.scope.domain.team.repository;

import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.team.model.Team;
import com.studycollaboproject.scope.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryExtension {

    Optional<Team> findByUserAndPost(User user, Post post);

    void deleteAllByPost(Post post);

    void deleteAllByUser(User user);

    void deleteByUserAndPost(User member, Post post);
}
