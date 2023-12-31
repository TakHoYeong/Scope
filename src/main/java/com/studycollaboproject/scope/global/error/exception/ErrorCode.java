package com.studycollaboproject.scope.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NO_USER_ERROR("해당 유저를 찾을 수 없습니다."),
    NO_POST_ERROR("해당 프로젝트를 찾을 수 없습니다."),
    NO_APPLICANT_ERROR("해당 지원 정보를 찾을 수 없습니다."),
    NO_AUTHENTICATION_ERROR("로그인 사용자만 사용할 수 있습니다."),
    NO_AUTHORIZATION_ERROR("접근 권한이 없습니다."),
    NO_TOKEN_ERROR("토큰이 존재하지 않습니다."),
    NO_MATCH_ITEM_ERROR("일치하는 값이 없습니다."),
    ALREADY_APPLY_POST_ERROR("이미 지원한 프로젝트입니다."),
    ALREADY_EMAIL_ERROR("이미 사용중인 이메일입니다."),
    ALREADY_NICKNAME_ERROR("이미 사용중인 닉네임입니다."),
    IMAGE_SAVE_ERROR("이미지 저장에 실패하였습니다."),
    INVALID_INPUT_ERROR("입력 값이 잘못되었습니다."),
    NO_BOOKMARK_MY_POST_ERROR("자신의 게시물은 북마크할 수 없습니다."),
    NO_TEAM_ERROR("프로젝트의 팀 구성원이 아닙니다."),
    ALREADY_ASSESSMENT_ERROR("이미 평가를 완료했습니다."),
    TOKEN_EXPIRATION_ERROR("로그인 정보가 만료되었습니다."),
    ALREADY_STARTED_ERROR("모집중인 프로젝트가 아닙니다."),
    NO_RECRUITMENT_ERROR("이미 진행중인 프로젝트입니다."),
    NO_EXCEED_MEMBER_ERROR("이미 모집 정원이 가득 찼습니다."),
    NOT_AVAILABLE_ACCESS("불가능한 접근입니다."),
    ALREADY_TEAM_ERROR("이미 팀원인 프로젝트입니다."),
    NO_IPBANLIST_ERROR("IP 밴 리스트에 없는 아이피입니다."),
    NO_EMAIL_PATTERN("이메일 형식이 맞지 않습니다.")
    ;
    private String message;
}
