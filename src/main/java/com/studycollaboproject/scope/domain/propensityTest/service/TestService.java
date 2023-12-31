package com.studycollaboproject.scope.domain.propensityTest.service;

import com.studycollaboproject.scope.domain.propensityTest.dto.TestResultDto;
import com.studycollaboproject.scope.global.error.exception.ErrorCode;
import com.studycollaboproject.scope.global.error.exception.BadRequestException;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TestService {
    private final UserRepository userRepository;

    @Transactional
    public TestResultDto updatePropensityType(String snsId, List<String> userPropensityType, List<String> memberPropensityType) {
        User user = userRepository.findBySnsId(snsId).orElseThrow(
                () -> new BadRequestException(ErrorCode.NO_USER_ERROR)
        );

        String userType = testResult(userPropensityType);       //user의 성향 테스트 결과
        String memberType = testResult(memberPropensityType);   //user가 원하는 성향 테스트 결과

        return new TestResultDto(user.updateUserPropensityType(userType),
                user.updateMemberPropensityType(memberType));
    }

    public String testResult(List<String> userPropensityType) {
        Map<String, Integer> userTypeMap = new HashMap<>();
        StringBuilder userType = new StringBuilder();
        for (String userSelected : userPropensityType) {
            userTypeMap.put(userSelected, userTypeMap.getOrDefault(userSelected, 0) + 1);
        }
        int typeLValue = userTypeMap.getOrDefault("L", 0);

        userType.append(userTypeMap.getOrDefault("L", 0) > userTypeMap.getOrDefault("F", 0) ? 'L' : 'F');
        userType.append(userTypeMap.getOrDefault("V", 0) > userTypeMap.getOrDefault("H", 0) ? 'V' : 'H');
        userType.append(userTypeMap.getOrDefault("P", 0) > userTypeMap.getOrDefault("G", 0) ? 'P' : 'G');

        String userTypeString = new String(userType);
        if(userTypeString.equals("FHP") && typeLValue > 0){
            userTypeString = "RHP";
        }

        return userTypeString;
    }
}

