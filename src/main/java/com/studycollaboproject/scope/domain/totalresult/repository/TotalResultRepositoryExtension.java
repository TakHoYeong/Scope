package com.studycollaboproject.scope.domain.totalresult.repository;

import java.util.List;

public interface TotalResultRepositoryExtension {
    void updateAssessmentResult(String userType, List<String> memberTypes);
}