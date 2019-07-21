package com.ts.server.ods.controller.configure;

import com.ts.server.ods.controller.interceptor.AuthorizationInterceptor;
import com.ts.server.ods.security.authenticate.AuthenticateService;
import com.ts.server.ods.security.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Api接口配置
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Configuration
public class ApiConfigure implements WebMvcConfigurer {
    private final TokenService tokenService;
    private final AuthenticateService authenticateService;

    @Autowired
    public ApiConfigure(TokenService tokenService, AuthenticateService authenticateService) {
        this.tokenService = tokenService;
        this.authenticateService = authenticateService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationInterceptor(tokenService, authenticateService))
                .addPathPatterns("/**")
                .excludePathPatterns("/*/login", "/*/logout", "/*/smsPassword");
    }
}
