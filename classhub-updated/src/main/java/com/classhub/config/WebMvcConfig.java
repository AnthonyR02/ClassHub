package com.classhub.config;

import com.classhub.security.RoleEnforcementInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RoleEnforcementInterceptor roleEnforcementInterceptor;

    public WebMvcConfig(RoleEnforcementInterceptor roleEnforcementInterceptor) {
        this.roleEnforcementInterceptor = roleEnforcementInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleEnforcementInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }
}
