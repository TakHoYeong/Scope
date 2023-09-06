package com.studycollaboproject.scope.domain.applicant.repository;

import com.studycollaboproject.scope.domain.applicant.model.Applicant;
import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long>, ApplicantRepositoryExtension {
    Optional<Applicant> findByUserAndPost(User user, Post post);
    void deleteAllByPost(Post post);
    List<Applicant> findAllByPost(Post post);

    void deleteAllByUser(User user);
}
