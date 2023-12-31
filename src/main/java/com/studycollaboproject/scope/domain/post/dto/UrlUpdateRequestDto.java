package com.studycollaboproject.scope.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UrlUpdateRequestDto {
    private String frontUrl;
    private String backUrl;
}
