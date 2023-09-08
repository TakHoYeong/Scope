package com.studycollaboproject.scope.domain.post.repository;

import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.post.model.Tech;
import com.studycollaboproject.scope.domain.post.model.TechStack;
import com.studycollaboproject.scope.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    List<TechStack> findAllByTechIn(List<Tech> Tech);

    void deleteAllByUser(User user);

    void deleteAllByPost(Post post);

    List<TechStack> findAllByUserIsNullAndTechIn(List<Tech> tech);

    List<TechStack> findAllByUser_SnsId(String snsId);
}
