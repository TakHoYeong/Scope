package com.studycollaboproject.scope.domain.applicant.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.studycollaboproject.scope.domain.applicant.model.QApplicant.applicant;


@Repository
@RequiredArgsConstructor
public class ApplicantRepositoryExtensionImpl implements ApplicantRepositoryExtension {
    private final JPAQueryFactory queryFactory;


    @Override
    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        Integer fetchOne = queryFactory.selectOne()
                .from(applicant)
                .where(applicant.user.id.eq(userId)
                        .and(applicant.post.id.eq(postId)))
                .fetchFirst();

        return fetchOne != null;
    }
}
