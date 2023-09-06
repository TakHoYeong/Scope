package com.studycollaboproject.scope.domain.totalresult.repository;

import com.studycollaboproject.scope.domain.totalresult.model.TotalResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TotalResultRepository extends JpaRepository<TotalResult,Long>, TotalResultRepositoryExtension{
    TotalResult findByUserTypeAndMemberType(String user, String member);
    List<TotalResult> findAllByUserType(String userType);
}
