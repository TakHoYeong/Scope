package com.studycollaboproject.scope.global.common.mail;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final MailService mailService;

    @Operation(summary = "이메일 인증 코드 확인")
    @GetMapping("api/user/auth/email/{userId}")
    public String recEmailCode(@Parameter(description = "인증 코드", in = ParameterIn.QUERY) @RequestParam String code,
                               @Parameter(description = "이메일", in = ParameterIn.QUERY) @RequestParam String email,
                               @Parameter(description = "프로젝트 ID", in = ParameterIn.PATH) @PathVariable Long userId, Model model) {
        String msg = mailService.emailAuthCodeCheck(code, userId, email);
        model.addAttribute("msg", msg);
        return "responsePage";
    }
}
