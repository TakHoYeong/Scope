package com.studycollaboproject.scope.domain.applicant.repository;

public interface ApplicantRepositoryExtension {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
}