package com.studycollaboproject.scope.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studycollaboproject.scope.model.Post;
import com.studycollaboproject.scope.model.ProjectStatus;
import com.studycollaboproject.scope.model.QTechStack;
import com.studycollaboproject.scope.model.Tech;

import javax.persistence.EntityManager;
import java.util.List;

import static com.studycollaboproject.scope.model.QPost.post;
import static com.studycollaboproject.scope.model.QUser.user;

public class PostRepositoryExtensionImpl implements PostRepositoryExtension {
    private final JPAQueryFactory queryFactory;

    public PostRepositoryExtensionImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Post> findAllByTechInOrderByCreatedAt(List<Tech> techList) {
        return queryFactory.selectFrom(post)
                .where(post.techStackList.any().tech.in(techList))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.createdAt.desc())
                .orderBy(post.projectStatus.desc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Post> findAllByTechInOrderByStartDate(List<Tech> techList) {
        return queryFactory.selectFrom(post)
                .where(post.techStackList.any().tech.in(techList))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.startDate.asc())
                .orderBy(post.projectStatus.desc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Post> findAllByBookmarkOrderByStartDate(String snsId) {
        return queryFactory.selectFrom(post)
                .where(post.bookmarkList.any().user.snsId.equalsIgnoreCase(snsId))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.startDate.asc())
                .orderBy(post.projectStatus.desc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Post> findAllByBookmarkOrderByCreatedAt(String snsId) {
        return queryFactory.selectFrom(post)
                .where(post.bookmarkList.any().user.snsId.equalsIgnoreCase(snsId))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.createdAt.desc())
                .orderBy(post.projectStatus.desc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Post> findAllByPropensityTypeOrderByStartDate(String propensity, List<Tech> techList, String snsId) {
        return queryFactory.selectFrom(post)
                .where(post.user.snsId.notEqualsIgnoreCase("unknown")
                        .and(post.user.snsId.notEqualsIgnoreCase(snsId))
                        .and(post.projectStatus.eq(ProjectStatus.PROJECT_STATUS_RECRUITMENT))
                        .and(post.techStackList.any().tech.in(techList))
                        .and(post.user.userPropensityType.eq(propensity)))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.startDate.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Post> findAllByPropensityTypeOrderByCreatedAt(String propensity, List<Tech> techList, String snsId) {
        return queryFactory.selectFrom(post)
                .where(post.user.snsId.notEqualsIgnoreCase("unknown")
                        .and(post.user.snsId.notEqualsIgnoreCase(snsId))
                        .and(post.projectStatus.eq(ProjectStatus.PROJECT_STATUS_RECRUITMENT))
                        .and(post.techStackList.any().tech.in(techList))
                        .and(post.user.userPropensityType.eq(propensity)))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.createdAt.desc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Post> findMemberPostByUserSnsId(String snsId) {
        return queryFactory.selectFrom(post)
                .where(post.teamList.any().user.snsId.eq(snsId))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.createdAt.desc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Post> findReadyPostByUserSnsId(String snsId) {
        return queryFactory.selectFrom(post)
                .where(post.applicantList.any().user.snsId.eq(snsId))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.createdAt.desc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Post> findAllBookmarkByUserSnsId(String snsId) {
        return queryFactory.selectFrom(post)
                .where(post.bookmarkList.any().user.snsId.eq(snsId))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.createdAt.desc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Post> findAllByKeywordOrderByCreatedAt(String keyword) {
        return queryFactory.selectFrom(post)
                .where(post.title.contains(keyword)
                        .or(post.contents.contains(keyword)))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.createdAt.desc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Post> findAllByKeywordOrderByStartDate(String keyword) {
        return queryFactory.selectFrom(post)
                .where(post.title.contains(keyword)
                        .or(post.contents.contains(keyword)))
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.techStackList, QTechStack.techStack).fetchJoin()
                .orderBy(post.startDate.asc())
                .distinct()
                .fetch();
    }
}
