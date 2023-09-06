package com.studycollaboproject.scope.domain.user.repository;

import com.studycollaboproject.scope.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findBySnsId(String snsId);

    Optional<User> findByNickname(String nickname);

    User findByUserPropensityType(String userPropensityType);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
