package com.studycollaboproject.scope.global.config;

import com.studycollaboproject.scope.global.filter.ExceptionHandlerFilter;
import com.studycollaboproject.scope.global.filter.JwtAuthenticationFilter;
import com.studycollaboproject.scope.global.filter.LogFilter;
import com.studycollaboproject.scope.global.ipfilter.HttpInterceptor;
import com.studycollaboproject.scope.global.ipfilter.IpBanService;
import com.studycollaboproject.scope.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.io.File;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final IpBanService ipBanService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/*")
                .allowedOrigins("https://scopewith.com/", "https://www.scopewith.com/")
                //클라이언트 로컬 주소임. 클라이언트에서 내 서버의 api에 접근 시 허용에 관한 부분. CORS.
                //2개 이상의 origin에 대해서 허용할 수 있음!
                .allowedMethods("POST", "GET", "PUT", "DELETE", "HEAD", "OPTIONS") // 클라이언트에서 요청하는 메소드 어디까지 허용할 것인가.
                .allowCredentials(true);
    }

    @Bean
    public FilterRegistrationBean<Filter> logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> exceptionFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ExceptionHandlerFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> jwtAuthFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtAuthenticationFilter(jwtTokenProvider));
        filterRegistrationBean.setOrder(3);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File file = new File("");
        String rootPath = "file://" + file.getAbsoluteFile().getPath() + "/images/";
        registry.addResourceHandler("/*", "/images/*")
                .addResourceLocations(rootPath, "classpath:", "classpath:/static/", "classpath:/templates/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HttpInterceptor httpInterceptor = new HttpInterceptor(ipBanService);
        registry.addInterceptor(httpInterceptor);
    }

}
