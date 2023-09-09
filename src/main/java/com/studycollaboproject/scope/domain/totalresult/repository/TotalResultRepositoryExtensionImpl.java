package com.studycollaboproject.scope.domain.totalresult.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.studycollaboproject.scope.domain.totalresult.model.QTotalResult.totalResult;


// model 패키지 안에 3개의 static 클래스가 실종 EntityPath?. 뭐 전달하는거 같은데
@Repository
@RequiredArgsConstructor
public class TotalResultRepositoryExtensionImpl implements TotalResultRepositoryExtension {
    private final JPAQueryFactory queryFactory;

    @Override
    public void updateAssessmentResult(String userType, List<String> memberTypes) {
        queryFactory.update(totalResult)
                .set(totalResult.result, totalResult.result.add(1))
                .where(totalResult.userType.eq(userType)
                        .and(totalResult.memberType.in(memberTypes)))
                .execute();
    }
}