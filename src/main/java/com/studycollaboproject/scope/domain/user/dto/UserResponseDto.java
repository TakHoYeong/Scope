package com.studycollaboproject.scope.domain.user.dto;

import com.studycollaboproject.scope.domain.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class UserResponseDto {

    @Schema(description = "사용자 성향")
    private String userPropensityType;
    @Schema(description = "사용자의 선호 팀원 성향")
    private String memberPropensityType;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "소개")
    private String introduction;
    @Schema(description = "기술 스택 리스트")
    private List<String> techStackList;
    @Schema(description = "아이디")
    private Long userId;
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "이메일")
    private Boolean isVerifiedEmail;



    public UserResponseDto(User user, List<String> techStackList){
        this.userPropensityType = user.getUserPropensityType();
        this.introduction = user.getIntroduction();
        this.memberPropensityType = user.getMemberPropensityType();
        this.nickname = user.getNickname();
        this.techStackList = techStackList;
        this.userId = user.getId();
        this.isVerifiedEmail = user.getIsVerifiedEmail();
        if(isVerifiedEmail){
            this.email = user.getEmail();
        } else {
            this.email = "이메일 미인증";
        }
//        this.isVerifiedEmail = getIsVerifiedEmail();
    }
    public UserResponseDto(User user) {
        this.userPropensityType = user.getUserPropensityType();
        this.memberPropensityType = user.getMemberPropensityType();
        this.nickname = user.getNickname();
        this.userId = user.getId();
        this.email = user.getEmail();
    }

}
