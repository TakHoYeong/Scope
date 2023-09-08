package com.studycollaboproject.scope.domain.post.repository;

import com.studycollaboproject.scope.domain.post.model.Bookmark;
import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findAllByUserNickname(String nickname);

    Optional<Bookmark> findByPostAndUser(Post post, User user);

    List<Bookmark> findAllByUser(User user);

    void deleteAllByPost(Post post);

    void deleteByUserAndPost(User user, Post post);

    void deleteAllByUser(User user);

    List<Bookmark> findAllByUserSnsIdAndPostIn(String snsId, List<Post> postList);
}
