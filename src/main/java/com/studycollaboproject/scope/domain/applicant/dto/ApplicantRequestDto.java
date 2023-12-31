package com.studycollaboproject.scope.domain.applicant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "지원 요청")
public class ApplicantRequestDto {
    @Schema(description = "한줄 소개")
    private String comment;
}
